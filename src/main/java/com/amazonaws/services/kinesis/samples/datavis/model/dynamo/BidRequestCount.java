package com.amazonaws.services.kinesis.samples.datavis.model.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.ddb.ReferrerCountMarshaller;
import com.amazonaws.services.kinesis.samples.datavis.model.TypeCount;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

    // Ordered list of referrer counts in descending order. Top N can be simply obtained by inspecting the first N
    // counts.
    private List<TypeCount> typeCounts;

    @DynamoDBAttribute
    @DynamoDBMarshalling(marshallerClass = ReferrerCountMarshaller.class)
    public List<TypeCount> getTypeCounts() {
        return typeCounts;
    }

    public void setTypeCounts(List<TypeCount> typeCounts) {
        this.typeCounts = typeCounts;
    }


}
