package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Hình thức phỏng vấn. */
public enum InterviewFormat implements CodedEnum {
    OFFLINE("offline"),
    ONLINE("online");

    private final String value;

    InterviewFormat(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InterviewFormat fromValue(String value) {
        return CodedEnum.fromValue(InterviewFormat.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<InterviewFormat> {
        public JpaConverter() {
            super(InterviewFormat.class);
        }
    }
}
