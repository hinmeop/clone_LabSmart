package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Học kỳ (report_exports.semester). */
public enum Semester implements CodedEnum {
    HK1("HK1"),
    HK2("HK2"),
    HK3("HK3");

    private final String value;

    Semester(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Semester fromValue(String value) {
        return CodedEnum.fromValue(Semester.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<Semester> {
        public JpaConverter() {
            super(Semester.class);
        }
    }
}
