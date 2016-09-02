package com.kinesis.datavis.model.record;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by eugennekhai on 28/08/16.
 */
@Getter
@AllArgsConstructor
public class ImpressionRec {
    private String bidRequestId;
    private String bannerId;
    private String audienceId;
    private Double winPrice = 0d;

    public ImpressionRec() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImpressionRec that = (ImpressionRec) o;

        if (bidRequestId != null ? !bidRequestId.equals(that.bidRequestId) : that.bidRequestId != null) return false;
        if (bannerId != null ? !bannerId.equals(that.bannerId) : that.bannerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bidRequestId != null ? bidRequestId.hashCode() : 0;
        result = 31 * result + (bannerId != null ? bannerId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImpressionRec{" +
                "bidRequestId='" + bidRequestId + '\'' +
                ", bannerId='" + bannerId + '\'' +
                '}';
    }

}
