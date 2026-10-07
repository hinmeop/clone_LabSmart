package com.dev.smartlabbackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Converter;

/** Vai trò trong project (project_members.project_role). */
public enum ProjectRole implements CodedEnum {
    OWNER("owner"),
    COORDINATOR("coordinator"),
    LEADER("leader"),
    MEMBER("member");

    private final String value;

    ProjectRole(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProjectRole fromValue(String value) {
        return CodedEnum.fromValue(ProjectRole.class, value);
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends AbstractCodedEnumConverter<ProjectRole> {
        public JpaConverter() {
            super(ProjectRole.class);
        }
    }
}
