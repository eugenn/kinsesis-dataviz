package com.kinesis.datavis.kcl.processor.type;

import com.amazonaws.services.kinesis.model.Record;
import com.jdbc.dao.MappingDAO;
import com.jdbc.vo.Mapping;
import com.kinesis.connectors.s3.buffer.IBuffer;
import com.kinesis.datavis.utils.ReflectionUtil;
import lombok.Getter;

import java.nio.charset.Charset;

/**
 * Created by eugennekhai on 07/09/16.
 */
public class CommonTypeProcessor<T> implements TypeProcessor<T> {
    @Getter
    private MappingDAO mappingDAO;
    @Getter
    protected IBuffer<byte[]> buffer;

    public CommonTypeProcessor(MappingDAO mappingDAO, IBuffer<byte[]> buffer) {
        this.mappingDAO = mappingDAO;
        this.buffer = buffer;
    }

    @Override
    public T process(T obj) {
        return null;
    }

    @Override
    public T process(Record r, T obj) {

        Mapping mapping = mappingDAO.load(ReflectionUtil.getValue(obj, "getBidRequestId"));

        if (mapping != null) {
            String bannerId = mapping.getBannerId();
            String audienceId = mapping.getAudienceId();

            ReflectionUtil.setValue(obj, "bannerId", bannerId);
            ReflectionUtil.setValue(obj, "audienceId", audienceId);

            String jsonPatched = toJSON(bannerId, audienceId, r.getData().array());

            buffer.consumeRecord(jsonPatched.getBytes(Charset.forName("UTF-8")), r.getData().array().length, r.getSequenceNumber());

        }

        return obj;
    }

    public String toJSON(String bannerId, String audienceId, byte[] data) {
        StringBuilder builder = new StringBuilder("{\"bannerId\":\"");
        builder.append(bannerId)
                .append("\",\"audienceId\":\"")
                .append(audienceId)
                .append("\",\"data\":")
                .append(new String(data))
                .append("}");
        return builder.toString();
    }

}
