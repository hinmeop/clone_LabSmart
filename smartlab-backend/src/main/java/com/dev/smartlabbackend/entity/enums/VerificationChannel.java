package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Kênh gửi mã xác thực. */
public enum VerificationChannel implements CodedEnum {
    EMAIL("email"),
    PHONE("phone");

    private final String value;

    VerificationChannel(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VerificationChannel fromValue(String value) {
        return CodedEnum.fromValue(VerificationChannel.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<VerificationChannel> {
        public JpaConverter() {
            super(VerificationChannel.class);
        }
    }
}
