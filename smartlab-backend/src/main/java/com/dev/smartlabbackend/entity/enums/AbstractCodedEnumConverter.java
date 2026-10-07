package com.dev.smartlabbackend.entity.enums;

import jakarta.persistence.AttributeConverter;

/**
 * Converter dùng chung: Enum <-> giá trị chuỗi trong DB (cột varchar + CHECK constraint).
 */
public abstract class AbstractCodedEnumConverter<E extends Enum<E> & CodedEnum>
        implements AttributeConverter<E, String> {

    private final Class<E> type;

    protected AbstractCodedEnumConverter(Class<E> type) {
        this.type = type;
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        return CodedEnum.fromValue(type, dbData);
    }
}
