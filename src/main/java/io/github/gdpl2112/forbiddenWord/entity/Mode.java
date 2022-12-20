package io.github.gdpl2112.forbiddenWord.entity;

import io.github.gdpl2112.database.anno.TableId;

/**
 * @author github.kloping
 */
public class Mode {
    @TableId(increment = true)
    private Integer id;
    /**
     * 警告次数
     */
    private Integer n = 3;
    /**
     * 触发禁词提示
     */
    private String tips;
    /**
     * 禁言时长 s
     */
    private Integer t;
    /**
     * 是否撤回
     */
    private Boolean recall = true;
    /**
     * 是否重置记录
     */
    private Boolean reset = true;


    public Boolean getReset() {
        return reset;
    }

    public void setReset(Boolean reset) {
        this.reset = reset;
    }

    public Boolean getRecall() {
        return recall;
    }

    public void setRecall(Boolean recall) {
        this.recall = recall;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }
}