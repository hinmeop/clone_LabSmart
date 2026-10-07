package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Loại tin nhắn. */
public enum MessageType implements CodedEnum {
    TEXT("text"),
    IMAGE("image"),
    FILE("file"),
    SYSTEM("system");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MessageType fromValue(String value) {
        return CodedEnum.fromValue(MessageType.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<MessageType> {
        public JpaConverter() {
            super(MessageType.class);
        }
    }
}
