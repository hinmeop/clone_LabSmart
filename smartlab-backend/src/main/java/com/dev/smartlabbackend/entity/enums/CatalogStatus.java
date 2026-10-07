package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái danh mục (domains, project_types). */
public enum CatalogStatus implements CodedEnum {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    CatalogStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CatalogStatus fromValue(String value) {
        return CodedEnum.fromValue(CatalogStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<CatalogStatus> {
        public JpaConverter() {
            super(CatalogStatus.class);
        }
    }
}
