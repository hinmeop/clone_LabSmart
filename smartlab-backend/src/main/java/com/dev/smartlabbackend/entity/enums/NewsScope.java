package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/**
 * Phạm vi người đọc cho tin tức (news).
 * public: Công khai
 * internal: Nội bộ
 * group: Nhóm
 */
public enum NewsScope implements CodedEnum {
    PUBLIC("public"),
    INTERNAL("internal"),
    GROUP("group");

    private final String value;

    NewsScope(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NewsScope fromValue(String value) {
        return CodedEnum.fromValue(NewsScope.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<NewsScope> {
        public JpaConverter() {
            super(NewsScope.class);
        }
    }
}
