package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum có giá trị lưu DB/JSON khác tên hằng (vd: PENDING_REVIEW <-> "pending_review").
 */
public interface CodedEnum {

    @JsonValue
    String getValue();

    static <E extends Enum<E> & CodedEnum> E fromValue(Class<E> type, String value) {
        if (value == null) return null;
        for (E e : type.getEnumConstants()) {
            if (e.getValue().equalsIgnoreCase(value) || e.name().equalsIgnoreCase(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid " + type.getSimpleName() + " value: " + value);
    }
}
