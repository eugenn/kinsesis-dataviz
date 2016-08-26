package com.amazonaws.services.kinesis.samples.datavis.model.record;

import lombok.Data;

/**
 * Created by eugennekhai on 24/08/16.
 */
@Data
public class BidRequestRec {
    private String type;
    private String wh;

    public BidRequestRec() {}
    public BidRequestRec(String type, String wh) {
        this.wh = wh;
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BidRequestRec that = (BidRequestRec) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (wh != null ? !wh.equals(that.wh) : that.wh != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = wh != null ? wh.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BidRequestRec{" +
                "wf='" + wh + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
