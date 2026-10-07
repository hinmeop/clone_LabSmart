package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái xuất báo cáo. */
public enum ExportStatus implements CodedEnum {
    PROCESSING("processing"),
    COMPLETED("completed"),
    FAILED("failed"),
    CANCELLED("cancelled");

    private final String value;

    ExportStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ExportStatus fromValue(String value) {
        return CodedEnum.fromValue(ExportStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<ExportStatus> {
        public JpaConverter() {
            super(ExportStatus.class);
        }
    }
}
