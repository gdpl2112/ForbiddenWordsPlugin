package io.github.gdpl2112.forbiddenWord.entity;

import io.github.gdpl2112.database.anno.TableId;

/**
 * @author github.kloping
 */
public class IllegalWord {
    @TableId(increment = true)
    private Integer id;
    private String c;
    private Integer mode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }
}
