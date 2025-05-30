package project.global.security.service;

import project.domain.member.Member;
import project.domain.member.repository.MemberRepository;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;
import project.global.security.dto.UserDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member =  memberRepository.findByEmail(username).orElseThrow(
                () -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND_BY_EMAIL)
        );
        return new UserDetailsDTO(member);

    }

}
