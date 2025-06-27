package project.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.domain.member.Member;
import project.domain.member.dto.MemberRequest;
import project.domain.member.dto.MemberRequest.JoinDTO;
import project.domain.member.dto.MemberRequest.UpdateDTO;
import project.domain.member.dto.MemberRequest.UpdatePasswordDTO;
import project.domain.member.dto.MemberResponse.DetailInfoDTO;
import project.domain.member.service.MemberService;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;
import project.global.security.annotation.LoginMember;


@Tag(name = "회원 API", description = "회원 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(
        summary = "회원 가입",
        description = "사용자로부터 회원 정보를 받아 회원가입을 처리합니다."
    )
    @PostMapping("/join")
    public ApiResponse<Boolean> createMember(
        @RequestBody JoinDTO joinDTO
    ) {
        return memberService.join(joinDTO);
    }

    @Operation(
        summary = "닉네임 중복확인",
        description = "사용자로부터 닉네임을 받아 중복을 확인합니다."
    )
    @GetMapping("/check")
    public ApiResponse<Boolean> checkNickname(
        @RequestParam String nickname
    ) {
        boolean chk = memberService.checkMemberByNickname(nickname);
        if (chk) {
            return ApiResponse.onSuccess(true);
        } else {
            throw new GeneralException(ErrorStatus.MEMBER_DUPLICATE_BY_NICKNAME);
        }
    }

    @Operation(
        summary = "사용자 정보 조회",
        description = "사용자의 정보를 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ApiResponse<DetailInfoDTO> getMemberInfo(
        @LoginMember Member member
    ) {
        return memberService.getMemberDetailInfo(member);
    }

    @Operation(
        summary = "사용자 정보 수정",
        description = "사용자로부터 수정할 정보를 받아 수정합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping
    public ApiResponse<DetailInfoDTO> updateMember(
        @LoginMember Member member,
        @RequestBody UpdateDTO updateDTO
    ) {
        return memberService.updateMember(member, updateDTO);
    }

    @Operation(
        summary = "사용자 프로필 사진 수정",
        description = "사용자가 요청한 사진으로 프로필을 수정합니다."
    )
    @PatchMapping("/image")
    public ApiResponse<Boolean> updateMemberImage(
        @LoginMember Member member,
        @RequestParam("profileImage") MultipartFile file
    ) {
        return memberService.updateProfile(member, file);
    }


    @Operation(
        summary = "비밀번호 수정",
        description = "사용자로부터 새로운 비밀번호를 받고 수정해줍니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/new-password")
    public ApiResponse<Boolean> changePassword(
        @LoginMember Member member,
        @RequestBody UpdatePasswordDTO updatePasswordDTO
    ) {
        memberService.createNewPassword(member, updatePasswordDTO.getPassword());
        return ApiResponse.onSuccess(true);
    }

    @Operation(
        summary = "회원 탈퇴",
        description = "해당하는 사용자 회원탈퇴를 합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ApiResponse<Boolean> deleteMember(
        @LoginMember Member member
    ) {
        return memberService.deleteMember(member);
    }

    @Operation(
        summary = "관리자 로그인",
        description = "관리자 해당하는 브랜드 관리 페이지로 이동합니다."
    )
    @PostMapping("admin-login")
    public ApiResponse<String> adminLongin(
        @RequestBody MemberRequest.LoginDTO request
    ) {
        String redirectUrl =
            "http://3.106.232.7:8501/admin?brand=" + memberService.getVaildAdmin(request);

        return ApiResponse.onSuccess(redirectUrl);
    }

}
