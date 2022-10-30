package SWYG3.SubdivisionSubdivision.login;

import SWYG3.SubdivisionSubdivision.entity.Member;
import SWYG3.SubdivisionSubdivision.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    public Member login(String email, String password) { // return값이 null이면 로그인 실패
//        Optional<Member> findedMemberOptional = memberRepository.findByEmail(email);
//        Member member = findedMemberOptional.get();
//
//        if(member.getPassword().equals(password)) {
//            return member;
//        }else {
//            return null;
//        }

        return memberRepository.findRegisteredMember(email)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

}
