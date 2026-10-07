package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái task. */
public enum TaskStatus implements CodedEnum {
    PENDING("pending"),
    PROCESSING("processing"),
    PENDING_REVIEW("pending_review"),
    DONE("done"),
    REJECTED("rejected"),
    CANCEL("cancel");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TaskStatus fromValue(String value) {
        return CodedEnum.fromValue(TaskStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<TaskStatus> {
        public JpaConverter() {
            super(TaskStatus.class);
        }
    }
}
