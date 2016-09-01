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
public class BannerRequestMapper {
    @DynamoDBHashKey
    private String hashKey;

    @DynamoDBRangeKey
    private Date timestamp;

    @DynamoDBAttribute
    private String bannerId;

    @DynamoDBAttribute
    private String bidRequestId;

    @DynamoDBAttribute
    private String audienceId;

    @DynamoDBAutoGeneratedKey
    private String key;

    public BannerRequestMapper() {}
    public BannerRequestMapper(String hashKey, Date timestamp, String bannerId, String bidRequestId, String audienceId) {
        this.hashKey = hashKey;
        this.timestamp = timestamp;
        this.bannerId = bannerId;
        this.bidRequestId = bidRequestId;
        this.audienceId = audienceId;
    }
}
