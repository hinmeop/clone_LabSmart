package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái news. */
public enum NewsStatus implements CodedEnum {
    DRAFT("draft"),
    SCHEDULED("scheduled"),
    PUBLISHED("published"),
    HIDDEN("hidden");

    private final String value;

    NewsStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NewsStatus fromValue(String value) {
        return CodedEnum.fromValue(NewsStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<NewsStatus> {
        public JpaConverter() {
            super(NewsStatus.class);
        }
    }
}
