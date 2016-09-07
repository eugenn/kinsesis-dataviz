package com.kinesis.datavis.server;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.kinesis.datavis.utils.AppProperties;
import com.kinesis.datavis.utils.AppUtils;
import com.kinesis.datavis.utils.DynamoDBUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by eugennekhai on 28/08/16.
 */
public class WebServer {
    /**
     * Start an embedded web server.
     *
     * @param args Expecting 4 arguments: Port number, File path to static content, the name of the
     *        DynamoDB table where counts are persisted to, and the AWS region in which these resources
     *        exist or should be created.
     * @throws Exception Error starting the web server.
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server(Integer.parseInt(args[0]));

        AppProperties appProps = new AppProperties("webserver", args[1]);

        String wwwroot = appProps.webRoot();
        String countsTableName = appProps.countTable();
        String servlet = appProps.servletName();
        String endpoint = appProps.endpoint();
        Region region = AppUtils.parseRegion(appProps.getRegion());

        // Servlet context
        ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS | ServletContextHandler.NO_SECURITY);
        context.setContextPath("/api");

        // Static resource context
        ResourceHandler resources = new ResourceHandler();
        resources.setDirectoriesListed(true);
        resources.setResourceBase(wwwroot);

        // Create the servlet to handle /GetCounts
        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        ClientConfiguration clientConfig = AppUtils.configureUserAgentForSample(new ClientConfiguration());

        AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(credentialsProvider, clientConfig);
        dynamoDB.setRegion(region);

        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDB);
        context.addServlet(new ServletHolder(AppUtils.instServlet(servlet, dynamoDBUtils.createMapperForTable(countsTableName))),
                "/" + endpoint +"/*");

        HandlerList handlers = new HandlerList();
        handlers.addHandler(context);
        handlers.addHandler(resources);
        handlers.addHandler(new DefaultHandler());


        server.setHandler(handlers);
        server.start();
        server.join();
    }


}

