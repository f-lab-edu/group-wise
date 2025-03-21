package wj.flab.group_wise.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.config.JwtTokenProvider;
import wj.flab.group_wise.domain.Member;
import wj.flab.group_wise.domain.exception.AlreadyExistsException;
import wj.flab.group_wise.domain.exception.AuthenticationFailedException;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.dto.JwtResponse;
import wj.flab.group_wise.dto.member.MemberCreateRequest;
import wj.flab.group_wise.dto.member.MemberLoginRequest;
import wj.flab.group_wise.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username) // Member로 변경
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 모든 사용자에게 "ROLE_USER" 권한 부여
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        return new org.springframework.security.core.userdetails.User(
            member.getUsername(),
            member.getPassword(),
            authorities
        );
    }

    @Transactional(readOnly = true)
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException(TargetEntity.MEMBER, memberId));
    }

    public Long registerMember(MemberCreateRequest memberCreateRequest) {

        if (memberRepository.existsByUsername(memberCreateRequest.username())) {
            throw new AlreadyExistsException(TargetEntity.MEMBER,
                memberCreateRequest.username() + "는 이미 존재하는 사용자입니다.");
        }

        Member member = new Member(
            memberCreateRequest.username(),
            passwordEncoder.encode(memberCreateRequest.password())
        );

        memberRepository.save(member);
        return member.getId();
    }

    public JwtResponse login(MemberLoginRequest memberLoginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    memberLoginRequest.username(),
                    memberLoginRequest.password()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.createToken(userDetails.getUsername());

            return new JwtResponse(token);
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException();
        }
    }

}
