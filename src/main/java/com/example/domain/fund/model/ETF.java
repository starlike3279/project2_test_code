package com.example.domain.fund.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ETF {
    @Id
    private String code;
    private String name;

    @Enumerated(EnumType.STRING)
    private ETFCategory category;

    @Enumerated(EnumType.STRING)
    private ETFSubCategory subCategory;

    @Builder
    public ETF(String code, String name, ETFCategory category, ETFSubCategory subCategory) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
    }
}

