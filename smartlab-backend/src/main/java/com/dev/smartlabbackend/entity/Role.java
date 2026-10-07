package com.dev.smartlabbackend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum Role {
    IT_ADMIN("it_admin"),
    BUSINESS_ADMIN("business_admin"),
    MANAGER("manager"),
    LEADER("leader"),
    MEMBER("member"),
    GUEST("guest");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Role fromValue(String value) {
        if (value == null) return null;
        for (Role r : Role.values()) {
            if (r.value.equalsIgnoreCase(value) || r.name().equalsIgnoreCase(value)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid role value: " + value);
    }

    @Converter(autoApply = true)
    public static class RoleConverter implements AttributeConverter<Role, String> {
        @Override
        public String convertToDatabaseColumn(Role role) {
            return role == null ? null : role.getValue();
        }

        @Override
        public Role convertToEntityAttribute(String dbData) {
            return dbData == null ? null : Role.fromValue(dbData);
        }
    }
}
