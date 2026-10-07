package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Loại hội thoại. */
public enum ConversationType implements CodedEnum {
    DIRECT("direct"),
    GROUP("group");

    private final String value;

    ConversationType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ConversationType fromValue(String value) {
        return CodedEnum.fromValue(ConversationType.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<ConversationType> {
        public JpaConverter() {
            super(ConversationType.class);
        }
    }
}
