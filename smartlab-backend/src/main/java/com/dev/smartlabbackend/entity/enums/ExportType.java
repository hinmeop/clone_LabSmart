package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Loại xuất báo cáo. */
public enum ExportType implements CodedEnum {
    MANUAL("manual"),
    AI("ai");

    private final String value;

    ExportType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ExportType fromValue(String value) {
        return CodedEnum.fromValue(ExportType.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<ExportType> {
        public JpaConverter() {
            super(ExportType.class);
        }
    }
}
