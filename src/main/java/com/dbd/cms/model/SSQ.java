package com.dbd.cms.model;

import java.io.Serializable;

/**
 * Created by yuhaihui8913 on 2017/5/22.
 */
//@SuppressWarnings("serial")
public class SSQ implements Serializable{

    private static final long serialVersionUID = -1239222675255689240L;
    private int id;
    private String name;
    private int sort;
    private int pid;

    public SSQ() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SSQ ssq = (SSQ) o;

        if (id != ssq.id) return false;
        if (sort != ssq.sort) return false;
        if (pid != ssq.pid) return false;
        return name != null ? name.equals(ssq.name) : ssq.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + sort;
        result = 31 * result + pid;
        return result;
    }
}
