package project.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.domain.cart.Cart;
import project.domain.cart.repository.CartRepository;
import project.domain.member.Member;
import project.domain.member.dto.MemberConverter;
import project.domain.member.dto.MemberRequest;
import project.domain.member.dto.MemberRequest.JoinDTO;
import project.domain.member.dto.MemberResponse;
import project.domain.member.dto.MemberResponse.DetailInfoDTO;
import project.domain.member.repository.MemberRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;
import project.global.s3.util.S3Uploader;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Uploader s3Uploader;

    private final String dirName = "member-images";


    // 회원가입
    @Transactional
    public ApiResponse<Boolean> join(JoinDTO joinDTO) {

        memberRepository.findByEmail(joinDTO.getEmail())
            .ifPresent(member -> {
                throw new GeneralException(ErrorStatus.MEMBER_DUPLICATE_BY_EMAIL);
            });

        memberRepository.findByNickname(joinDTO.getNickname())
            .ifPresent(member -> {
                throw new GeneralException(ErrorStatus.MEMBER_DUPLICATE_BY_NICKNAME);
            });

        Member entity = MemberConverter.toEntity(joinDTO);
        entity.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));

        // 멤버 정보 저장
        Member savedMember = memberRepository.save(entity);

        // 멤버 생성 시 자동으로 장바구니도 생성됨
        Cart cart = Cart.createCart(savedMember);
        cartRepository.save(cart);

        return ApiResponse.onSuccess(true);
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
            () -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_EMAIL)
        );
    }

    // 새로운 비밀번호를 생성
    @Transactional
    public void createNewPassword(Member member, String password) {
        member.setPassword(bCryptPasswordEncoder.encode(password));
        memberRepository.save(member);
    }

    // 멤버의 정보 조회
    public ApiResponse<MemberResponse.DetailInfoDTO> getMemberDetailInfo(Member member) {
        Member memberById = findMemberById(member);
        DetailInfoDTO detailInfoDTO = MemberConverter.toDetailInfoDTO(memberById);
        return ApiResponse.onSuccess(detailInfoDTO);
    }

    // 멤버 정보 수정
    @Transactional
    public ApiResponse<MemberResponse.DetailInfoDTO> updateMember(Member member,
        MemberRequest.UpdateDTO updateDTO) {
        Member memberById = findMemberById(member);

        memberById.updateMember(updateDTO);
        memberRepository.save(memberById);

        DetailInfoDTO detailInfoDTO = MemberConverter.toDetailInfoDTO(memberById);
        return ApiResponse.onSuccess(detailInfoDTO);
    }

    //멤버 프로필사진 수정
    @Transactional
    public ApiResponse<Boolean> updateProfile(Member member, MultipartFile file) {
        Member updateMember = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_ID));

        String profileImg = updateMember.getProfileImg();
        // 기존 이미지 삭제
        deleteProfileImg(profileImg);
        // 수정 이미지 업로드
        String newProfileImg = updateProfileImg(file);

        updateMember.updateProfileImg(newProfileImg);

        return ApiResponse.onSuccess(true);
    }

    @Transactional
    public ApiResponse<Boolean> deleteMember(Member member) {
        memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_ID));
        memberRepository.deleteById(member.getId());
        return ApiResponse.onSuccess(true);
    }

    private String updateProfileImg(MultipartFile file) {
        try {
            return s3Uploader.uploadFile(file, dirName);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, "S3 업로드를 실패했습니다.");
        }
    }

    // S3 멤버 프로필 삭제
    private void deleteProfileImg(String profileImg) {
        if (profileImg != null) {
            try {
                String fileName = s3Uploader.extractFileNameFromUrl(profileImg);
                s3Uploader.deleteFile(fileName, dirName);
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, "S3에서 삭제를 실패했습니다.");
            }
        }
    }

    public Member findMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_NICKNAME));
    }

    public boolean checkMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();

    }

    private Member findMemberById(Member member) {
        return memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_ID));
    }


}
