package project.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


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

        memberRepository.save(entity);

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

    public Member findMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_NICKNAME));
    }

    public boolean checkMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();

    }

    private Member findMemberById(Member member) {
        return memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }


}
