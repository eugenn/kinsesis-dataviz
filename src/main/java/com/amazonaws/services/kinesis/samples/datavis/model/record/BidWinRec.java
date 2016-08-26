package com.amazonaws.services.kinesis.samples.datavis.model.record;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by eugennekhai on 25/08/16.
 */
@Data
public class BidWinRec {
    // Store the hostname of the worker that updated the count
    private String host;

    private String bidRequestId;

    private BigDecimal winPrice = BigDecimal.ZERO;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BidWinRec that = (BidWinRec) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (bidRequestId != null ? !bidRequestId.equals(that.bidRequestId) : that.bidRequestId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bidRequestId != null ? bidRequestId.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BidWinRec{" +
                "bidRequestId='" + bidRequestId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
