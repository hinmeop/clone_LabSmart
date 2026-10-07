package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Phạm vi post. */
public enum PostScope implements CodedEnum {
    LAB("lab"),
    PROJECT("project");

    private final String value;

    PostScope(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PostScope fromValue(String value) {
        return CodedEnum.fromValue(PostScope.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<PostScope> {
        public JpaConverter() {
            super(PostScope.class);
        }
    }
}
