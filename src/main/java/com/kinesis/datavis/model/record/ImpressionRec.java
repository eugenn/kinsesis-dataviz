package com.kinesis.datavis.model.record;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by eugennekhai on 28/08/16.
 */
@Getter
@AllArgsConstructor
public class ImpressionRec {
    private String bidRequestId;
    private String type;
    private BigDecimal winPrice = BigDecimal.ZERO;

    public ImpressionRec() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImpressionRec that = (ImpressionRec) o;

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
        return "ImpressionRec{" +
                "bidRequestId='" + bidRequestId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
