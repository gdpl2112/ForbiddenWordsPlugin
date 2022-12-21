package io.github.gdpl2112.forbiddenWord;

/**
 * @author github.kloping
 */
public class Config {
    private String database = "./conf/forbiddenWord/database.kdb";
    private Boolean ocr = true;

    public Boolean getOcr() {
        return ocr;
    }

    public void setOcr(Boolean ocr) {
        this.ocr = ocr;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
