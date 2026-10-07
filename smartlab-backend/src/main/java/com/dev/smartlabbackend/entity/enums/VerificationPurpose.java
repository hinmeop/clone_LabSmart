package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Mục đích mã xác thực. */
public enum VerificationPurpose implements CodedEnum {
    PASSWORD_RESET("password_reset");

    private final String value;

    VerificationPurpose(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VerificationPurpose fromValue(String value) {
        return CodedEnum.fromValue(VerificationPurpose.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<VerificationPurpose> {
        public JpaConverter() {
            super(VerificationPurpose.class);
        }
    }
}
