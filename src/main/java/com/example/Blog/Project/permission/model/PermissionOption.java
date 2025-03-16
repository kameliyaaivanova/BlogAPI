package com.example.Blog.Project.permission.model;

public enum PermissionOption {

    // Posts
    CREATE_POSTS("p.c"),
    UPDATE_POSTS("p.u"),
    DELETE_POSTS("p.d"),
    READ_POSTS("p.r"),

    // Users
    CREATE_USERS("u.c"),
    UPDATE_USERS("u.u"),
    DELETE_USERS("u.d"),
    READ_USERS("u.r"),

    // Roles
    CREATE_ROLES("r.c"),
    UPDATE_ROLES("r.u"),
    DELETE_ROLES("r.d"),
    READ_ROLES("r.r"),

    // Categories
    CREATE_CATEGORIES("c.c"),
    UPDATE_CATEGORIES("c.u"),
    READ_CATEGORIES("c.r"),

    // Analysis
    READ_STATISTICS("s.r");

    private final String abbreviation;

    PermissionOption(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getDescription() {
        StringBuilder descriptionSb = new StringBuilder();

        String[] parts = this.name().split("_");

        for (String part : parts) {
            String uppercaseFirst = part.substring(0, 1).toUpperCase();
            descriptionSb.append(uppercaseFirst).append(part.substring(1).toLowerCase()).append(" ");
        }

        return descriptionSb.toString().trim();
    }
}
