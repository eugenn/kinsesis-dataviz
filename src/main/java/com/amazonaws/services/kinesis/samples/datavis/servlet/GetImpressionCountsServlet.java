package com.amazonaws.services.kinesis.samples.datavis.servlet;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.kinesis.samples.datavis.model.dynamo.ImpressionCount;
import com.amazonaws.services.kinesis.samples.datavis.utils.DynamoDBUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by eugennekhai on 28/08/16.
 */
public class GetImpressionCountsServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(GetBidRqCountsServlet.class);

    private static final ThreadLocal<DateFormat> DATE_FORMATTER = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            // ISO-8601 format
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(UTC);
            return df;
        }
    };
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    // This is not serializable and we're not implementing safeguards to support it. Jetty is highly unlikely to
    // serialize this servlet anyway.
    private transient ObjectMapper JSON = new ObjectMapper();

    // This is not serializable and we're not implementing safeguards to support it. Jetty is highly unlikely to
    // serialize this servlet anyway.
    private transient DynamoDBMapper mapper;

    private static final String PARAMETER_RESOURCE = "resource";
    private static final String PARAMETER_RANGE_IN_SECONDS = "range_in_seconds";

    public GetImpressionCountsServlet(DynamoDBMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException("DynamoDBMapper must not be null");
        }
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(req.getQueryString(), params, "UTF-8");

        // We need both parameters to properly query for counts
        if (!params.containsKey(PARAMETER_RESOURCE) || !params.containsKey(PARAMETER_RANGE_IN_SECONDS)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Parse query string as a single integer - the number of seconds since "now" to query for new counts
        String resource = params.getString(PARAMETER_RESOURCE);
        resource = "11111111111";
        int rangeInSeconds = Integer.parseInt(params.getString(PARAMETER_RANGE_IN_SECONDS));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -1 * rangeInSeconds);
        Date startTime = c.getTime();
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Querying for counts of resource %s since %s", resource, DATE_FORMATTER.get().format(startTime)));
        }

        DynamoDBQueryExpression<ImpressionCount> query = new DynamoDBQueryExpression<>();


        ImpressionCount hashKey = new ImpressionCount();
        hashKey.setHashKey(DynamoDBUtils.getHashKey());

        query.setHashKeyValues(hashKey);

        Condition recentUpdates =
                new Condition().withComparisonOperator(ComparisonOperator.GT)
                        .withAttributeValueList(new AttributeValue().withS(DATE_FORMATTER.get().format(startTime)));
        Condition attrFilter =
                new Condition().
                        withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(resource));

        query.setRangeKeyConditions(Collections.singletonMap("timestamp", recentUpdates));
        query.setQueryFilter(Collections.singletonMap("bidRequestId", attrFilter));

        List<ImpressionCount> counts = mapper.query(ImpressionCount.class, query);

        System.out.println(counts.size());
        // Return the counts as JSON
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        JSON.writeValue(resp.getWriter(), counts);
    }
}
