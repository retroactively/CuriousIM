package com.example.imserver.domain;

import java.util.Date;

public class UserFriend {
    private Long id;

    private Long userid;

    private Long userfriendid;

    private Date createtime;

    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getUserfriendid() {
        return userfriendid;
    }

    public void setUserfriendid(Long userfriendid) {
        this.userfriendid = userfriendid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}