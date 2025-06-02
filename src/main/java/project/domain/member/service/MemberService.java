package project.domain.member.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.cart.Cart;
import project.domain.cart.repository.CartRepository;
import project.domain.member.Member;
import project.domain.member.dto.MemberConverter;
import project.domain.member.dto.MemberRequest.JoinDTO;
import project.domain.member.repository.MemberRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public ApiResponse<Boolean> join(JoinDTO joinDTO) {

        memberRepository.findByEmail(joinDTO.getEmail())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_DUPLICATE_BY_EMAIL));

        memberRepository.findByNickname(joinDTO.getNickname())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_Nickname));

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

    @Transactional
    public void createNewPassword(Member member, String password) {
        member.setPassword(bCryptPasswordEncoder.encode(password));
        memberRepository.save(member);
    }

    public Member findMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_DUPLICATE_BY_NICKNAME));
    }

    public boolean checkMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }


}
