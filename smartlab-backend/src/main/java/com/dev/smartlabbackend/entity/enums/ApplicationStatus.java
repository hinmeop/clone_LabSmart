package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái đơn ứng tuyển. */
public enum ApplicationStatus implements CodedEnum {
    PENDING("pending"),
    INTERVIEW("interview"),
    PASSED("passed"),
    FAILED("failed"),
    WITHDRAWN("withdrawn");

    private final String value;

    ApplicationStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ApplicationStatus fromValue(String value) {
        return CodedEnum.fromValue(ApplicationStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<ApplicationStatus> {
        public JpaConverter() {
            super(ApplicationStatus.class);
        }
    }
}
