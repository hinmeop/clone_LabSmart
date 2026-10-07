package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Trạng thái post. */
public enum PostStatus implements CodedEnum {
    DRAFT("draft"),
    PENDING_REVIEW("pending_review"),
    PUBLISHED("published"),
    HIDDEN("hidden");

    private final String value;

    PostStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PostStatus fromValue(String value) {
        return CodedEnum.fromValue(PostStatus.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<PostStatus> {
        public JpaConverter() {
            super(PostStatus.class);
        }
    }
}
