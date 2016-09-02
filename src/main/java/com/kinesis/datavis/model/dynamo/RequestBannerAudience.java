package com.kinesis.datavis.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

import java.util.Date;

/**
 * Created by eugennekhai on 30/08/16.
 */
@Data
@DynamoDBTable(tableName = "KinesisDataVisSample-NameToBeReplacedByDynamoDBMapper")
//@AllArgsConstructor
public class RequestBannerAudience {
    @DynamoDBHashKey
    private String bidRequestId;

    @DynamoDBRangeKey
    private Date timestamp;

    @DynamoDBAttribute
    private String bannerId;

    @DynamoDBAttribute
    private String audienceId;



    public RequestBannerAudience() {}
    public RequestBannerAudience(Date timestamp, String bannerId, String bidRequestId, String audienceId) {
        this.timestamp = timestamp;
        this.bannerId = bannerId;
        this.bidRequestId = bidRequestId;
        this.audienceId = audienceId;
    }
}
