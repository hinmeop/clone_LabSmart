package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái bài tuyển dụng. */
public enum RecruitmentStatus implements CodedEnum {
    DRAFT("draft"),
    OPEN("open"),
    CLOSED("closed");

    private final String value;

    RecruitmentStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RecruitmentStatus fromValue(String value) {
        return CodedEnum.fromValue(RecruitmentStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<RecruitmentStatus> {
        public JpaConverter() {
            super(RecruitmentStatus.class);
        }
    }
}
