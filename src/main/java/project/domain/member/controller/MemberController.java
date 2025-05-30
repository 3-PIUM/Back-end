package project.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.domain.member.Member;
import project.domain.member.dto.MemberRequest.JoinDTO;
import project.domain.member.service.MemberService;
import project.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ApiResponse<Boolean> createMember(
        JoinDTO joinDTO
    ) {
        return memberService.join(joinDTO);
    }

    @GetMapping("/check")
    public ApiResponse<Boolean> checkNickname(
        @RequestParam String nickname
    ) {
        boolean b = memberService.checkMemberByNickname(nickname);
        return ApiResponse.onSuccess(b);
    }
}
