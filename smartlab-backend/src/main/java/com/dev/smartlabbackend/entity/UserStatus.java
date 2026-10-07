package com.dev.smartlabbackend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    LOCKED("locked");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserStatus fromValue(String value) {
        if (value == null) return null;
        for (UserStatus s : UserStatus.values()) {
            if (s.value.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }

    @Converter(autoApply = true)
    public static class UserStatusConverter implements AttributeConverter<UserStatus, String> {
        @Override
        public String convertToDatabaseColumn(UserStatus status) {
            return status == null ? null : status.getValue();
        }

        @Override
        public UserStatus convertToEntityAttribute(String dbData) {
            return dbData == null ? null : UserStatus.fromValue(dbData);
        }
    }
}
