package io.github.gdpl2112.forbiddenWord.entity;

import io.github.gdpl2112.database.anno.TableId;

/**
 * @author github.kloping
 */
public class Enables {
    @TableId(unique = true)
    private Long gid;
    private Boolean k;

    public Long getGid() {
        return gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
    }

    public Boolean getK() {
        return k;
    }

    public void setK(Boolean k) {
        this.k = k;
    }
}
