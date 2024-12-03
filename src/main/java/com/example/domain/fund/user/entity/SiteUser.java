package com.example.domain.fund.user.entity;

import com.example.domain.fund.global.jpa.BaseEntity;
import com.example.domain.propercity.entity.Propensity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class SiteUser extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "propensity_id", referencedColumnName = "propensityId")
    private Propensity propensity; // 투자 성향 (Foreign Key)

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String socialProvider; // 소셜로그인에 필요함

    @Column(columnDefinition = "text")
    private String intro;

    private String nickname;

    private String thumbnailImg;

}
