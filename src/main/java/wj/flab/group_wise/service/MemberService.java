package wj.flab.group_wise.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.Member;
import wj.flab.group_wise.domain.exception.EntityNotFoundException;
import wj.flab.group_wise.domain.exception.TargetEntity;
import wj.flab.group_wise.dto.member.MemberCreateRequest;
import wj.flab.group_wise.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

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

    public Long createMember(MemberCreateRequest memberCreateRequest) {
        Member member = memberRepository.save(memberCreateRequest.toEntity());
        return member.getId();
    }

}
