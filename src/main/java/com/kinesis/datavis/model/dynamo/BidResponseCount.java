package com.kinesis.datavis.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

import java.util.Date;

/**
 * Created by eugennekhai on 28/08/16.
 */
@Data
@DynamoDBTable(tableName = "KinesisDataVisSample-NameToBeReplacedByDynamoDBMapper")
public class BidResponseCount {
    @DynamoDBHashKey
    private String hashKey;

    @DynamoDBRangeKey
    private Date timestamp;

    @DynamoDBAttribute
    private Long count = 0L;

    // Store the hostname of the worker that updated the count
    @DynamoDBAttribute
    private String host;

    @DynamoDBAttribute
    private String bannerId;

    @DynamoDBAttribute
    private String audienceId;
}
