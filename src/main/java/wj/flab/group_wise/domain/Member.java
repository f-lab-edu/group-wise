package wj.flab.group_wise.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email; // email 로 변경하자
    private String password;
    private String address;

    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Member(String email, String password, String address) {
        this.email = email;
        this.password = password;
        this.address = address;
    }

}
