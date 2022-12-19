package io.github.gdpl2112.forbiddenWord.entity;

import io.github.gdpl2112.database.anno.TableId;

/**
 * @author github.kloping
 */
public class Record {
    @TableId(increment = true)
    private Integer id;
    private Long qid;
    private Integer wid;
    private Integer num;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getQid() {
        return qid;
    }

    public void setQid(Long qid) {
        this.qid = qid;
    }

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
