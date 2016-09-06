package com.kinesis.datavis.model.record;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by eugennekhai on 25/08/16.
 */
@Data
@AllArgsConstructor
public class BidWinRec {
    private String bidRequestId;
    private String bannerId;
    private String audienceId;
    private Double winPrice = Double.valueOf(0);

    public BidWinRec() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BidWinRec that = (BidWinRec) o;

        if (bannerId != null ? !bannerId.equals(that.bannerId) : that.bannerId != null) return false;
        if (audienceId != null ? !audienceId.equals(that.audienceId) : that.audienceId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bannerId != null ? bannerId.hashCode() : 0;
        result = 31 * result + (audienceId != null ? audienceId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BidWinRec{" +
                "bidRequestId='" + bidRequestId + '\'' +
                '}';
    }

}
