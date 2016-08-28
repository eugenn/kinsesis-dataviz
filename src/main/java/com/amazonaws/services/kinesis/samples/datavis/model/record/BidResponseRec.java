package com.amazonaws.services.kinesis.samples.datavis.model.record;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by eugennekhai on 28/08/16.
 */
@Getter
@AllArgsConstructor
public class BidResponseRec {
    private String bidResponse;
    private String type;
    private String wh;

    public BidResponseRec() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BidResponseRec that = (BidResponseRec) o;

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
        return "BidResponseRec{" +
                "wf='" + wh + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
