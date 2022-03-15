package com.example.imserver.domain.po;

import java.util.Date;

public class TalkBox {
    private Long id;

    private String userid;

    private String talkid;

    private Integer talktype;

    private Date createtime;

    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getTalkid() {
        return talkid;
    }

    public void setTalkid(String talkid) {
        this.talkid = talkid == null ? null : talkid.trim();
    }

    public Integer getTalktype() {
        return talktype;
    }

    public void setTalktype(Integer talktype) {
        this.talktype = talktype;
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