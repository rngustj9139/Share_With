package SWYG3.SubdivisionSubdivision.repository;

import SWYG3.SubdivisionSubdivision.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Member save(Member member) {
        em.persist(member);

        return member;
    }

    public Member findById(Long id) {
        Member findedMember = em.find(Member.class, id);

        return findedMember;
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findByEmail(String email) {
        List<Member> members = em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();

        return members.stream().findAny(); // 리스트안에 값이 없으면 null을 반환한다.
    }

    public Optional<Member> findRegisteredMember(String email) {
        return findAll().stream()
                .filter(m -> m.getEmail().equals(email))
                .findFirst();
    }

    public Optional<Member> findByName(String name) {
        List<Member> members = em.createQuery("select m from Member m where m.memberName = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return members.stream().findAny(); // 리스트 안에 값이 없으면 null을 반환한다.
    }

    public Optional<Member> findByNickName(String nickName) {
        List<Member> members = em.createQuery("select m from Member m where m.nickName = :nickName", Member.class)
                .setParameter("nickName", nickName)
                .getResultList();

        return members.stream().findAny(); // 리스트 안에 값이 없으면 null을 반환한다.
    }

}
