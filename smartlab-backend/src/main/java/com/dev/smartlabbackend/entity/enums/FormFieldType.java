package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Kiểu trường form tuyển dụng. */
public enum FormFieldType implements CodedEnum {
    TEXT("text"),
    NUMBER("number"),
    EMAIL("email"),
    PHONE("phone"),
    FILE("file"),
    TEXTAREA("textarea");

    private final String value;

    FormFieldType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FormFieldType fromValue(String value) {
        return CodedEnum.fromValue(FormFieldType.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<FormFieldType> {
        public JpaConverter() {
            super(FormFieldType.class);
        }
    }
}
