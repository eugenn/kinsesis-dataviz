package com.amazonaws.services.kinesis.samples.datavis.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

import java.util.Date;

/**
 * Created by eugennekhai on 24/08/16.
 */
@Data
@DynamoDBTable(tableName = "KinesisDataVisSample-NameToBeReplacedByDynamoDBMapper")
public class BidRequestCount {
    @DynamoDBHashKey
    private String hashKey;

    @DynamoDBRangeKey
    private Date timestamp;

    @DynamoDBAttribute
    private String wh;

    // Store the hostname of the worker that updated the count
    @DynamoDBAttribute
    private String host;

    @DynamoDBAttribute
    private Long count = 0L;

}
