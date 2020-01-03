package ru.avkurbatov_home.enums;

public enum Sex {
    MALE("M"),
    FEMALE("F");

    public final String db;

    Sex(String db) {
        this.db = db;
    }

    public static Sex of(String db) {
        if (db == null) {
            return null;
        }
        for (Sex sex : values()) {
            if (sex.db.equals(db)) {
                return sex;
            }
        }
        throw new IllegalArgumentException("No Sex is defined for db-value " + db);
    }
}
