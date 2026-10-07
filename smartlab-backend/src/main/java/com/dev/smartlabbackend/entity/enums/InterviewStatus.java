package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái phỏng vấn. */
public enum InterviewStatus implements CodedEnum {
    SCHEDULED("scheduled"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    private final String value;

    InterviewStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InterviewStatus fromValue(String value) {
        return CodedEnum.fromValue(InterviewStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<InterviewStatus> {
        public JpaConverter() {
            super(InterviewStatus.class);
        }
    }
}
