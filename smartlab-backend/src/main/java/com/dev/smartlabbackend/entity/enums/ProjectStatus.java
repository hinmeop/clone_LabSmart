package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái project. */
public enum ProjectStatus implements CodedEnum {
    PENDING("pending"),
    PROCESSING("processing"),
    DONE("done"),
    CANCEL("cancel");

    private final String value;

    ProjectStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProjectStatus fromValue(String value) {
        return CodedEnum.fromValue(ProjectStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<ProjectStatus> {
        public JpaConverter() {
            super(ProjectStatus.class);
        }
    }
}
