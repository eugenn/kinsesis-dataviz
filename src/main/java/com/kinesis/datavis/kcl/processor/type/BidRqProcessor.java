package com.kinesis.datavis.kcl.processor.type;

import com.amazonaws.services.kinesis.model.Record;
import com.jdbc.dao.MappingDAO;
import com.kinesis.connectors.s3.buffer.IBuffer;
import com.kinesis.openrtb.BidRequest;

/**
 * Created by eugennekhai on 08/09/16.
 */
public class BidRqProcessor extends CommonTypeProcessor<BidRequest> {
    public BidRqProcessor(MappingDAO mappingDAO, IBuffer<byte[]> buffer) {
        super(mappingDAO, buffer);
    }

    @Override
    public BidRequest process(Record r, BidRequest obj) {

        buffer.consumeRecord(r.getData().array(), r.getData().array().length, r.getSequenceNumber());

        return obj;
    }
}
