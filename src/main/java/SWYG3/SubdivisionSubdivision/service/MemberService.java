package SWYG3.SubdivisionSubdivision.service;

import SWYG3.SubdivisionSubdivision.entity.Member;
import SWYG3.SubdivisionSubdivision.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member join(Member member) {

        return memberRepository.save(member);
    }

    public Member findOneMemberById(Long id) {

        return memberRepository.findById(id);
    }

    public Optional<Member> findOneMemberByEmail(String email) {

        return memberRepository.findByEmail(email);
    }

    public Optional<Member> findOneByNickName(String nickName) {

        return memberRepository.findByNickName(nickName);
    }

    public void updateAuthKey(Map<String, String> map) {
        String memberEmail = map.get("email");
        String authKey = map.get("authKey");

        Member findedMember = memberRepository.findByEmail(memberEmail).get();
        findedMember.setAuthKey(authKey);
    }

    public void updateAuthStatus(Map<String, String> map) {
        String authKey = map.get("authKey");
        Member findedMember = memberRepository.findByEmail(map.get("email")).get();
        String savedAuthKey = findedMember.getAuthKey();

        if (authKey.equals(savedAuthKey)) {
            findedMember.setAuthStatus(1);
        }
    }

    public void updatePassword(Map<String, String> map) {
        Member findedMember = memberRepository.findByEmail(map.get("email")).get();
        findedMember.setPassword(map.get("password"));
    }

}
