package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Vai trò trong hội thoại. */
public enum ChatParticipantRole implements CodedEnum {
    OWNER("owner"),
    ADMIN("admin"),
    MEMBER("member");

    private final String value;

    ChatParticipantRole(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ChatParticipantRole fromValue(String value) {
        return CodedEnum.fromValue(ChatParticipantRole.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<ChatParticipantRole> {
        public JpaConverter() {
            super(ChatParticipantRole.class);
        }
    }
}
