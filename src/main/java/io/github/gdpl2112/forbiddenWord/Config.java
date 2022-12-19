package io.github.gdpl2112.forbiddenWord;

/**
 * @author github.kloping
 */
public class Config {
    private String database = "./conf/forbiddenWord/database.kdb";

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
