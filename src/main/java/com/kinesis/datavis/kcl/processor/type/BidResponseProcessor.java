package com.kinesis.datavis.kcl.processor.type;

import com.amazonaws.services.kinesis.model.Record;
import com.jdbc.dao.MappingDAO;
import com.jdbc.vo.Mapping;
import com.kinesis.connectors.s3.buffer.FlushBuffer;
import com.kinesis.datavis.kcl.persistence.MappingThread;
import com.kinesis.openrtb.BidResponse;

import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;

/**
 * Created by eugennekhai on 07/09/16.
 */
public class BidResponseProcessor extends CommonTypeProcessor<BidResponse> {
    public BlockingQueue<Mapping> mappingsBuff;

    public BidResponseProcessor(MappingDAO mappingDAO, FlushBuffer<byte[]> buffer, BlockingQueue<Mapping> mappingsBuff) {
        super(mappingDAO, buffer);
        this.mappingsBuff = mappingsBuff;

        MappingThread<Mapping> mappingThread = new MappingThread<>(mappingDAO, mappingsBuff);
        mappingThread.setDaemon(true);
        mappingThread.start();

    }

    @Override
    public BidResponse process(Record r, BidResponse obj) {

        buffer.consumeRecord(r.getData().array(), r.getData().array().length, r.getSequenceNumber());

        mappingsBuff.add(new Mapping(obj.getId(), obj.getBannerId(), obj.getAudienceId(), new Timestamp(System.currentTimeMillis())));

        return obj;
    }
}
