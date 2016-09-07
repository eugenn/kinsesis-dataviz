package com.kinesis.datavis.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by eugennekhai on 07/09/16.
 */
public class AppProperties {
    private Properties props;
    private String type;

    public AppProperties(String type, String path) {
        this.props = loadProperties(path);
        this.type = type;
    }

    private Properties loadProperties(String path) {
        final Properties properties = new Properties();

        try (final FileInputStream stream = new FileInputStream(path)) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public String appName() {
        return props.get(type + ".aggregator.name").toString();
    }

    public String countTable() {
        return props.get(type + ".counts.table").toString();
    }

    public String streamName() {
        return props.get(type + ".stream").toString();
    }

    public String servletName() {
        return props.get(type + ".servlet").toString();
    }

    public String endpoint() {
        return props.get(type + ".endpoint").toString();
    }

    public String webRoot() {
        return props.get("webserver.wwwroot").toString();
    }
    public String dbUrl() {
        return props.get("jdbc.url").toString();
    }

    public String dbUser() {
        return props.get("jdbc.user").toString();
    }

    public String dbPassword() {
        return props.get("jdbc.password").toString();
    }

    public String getRegion() {
        return props.get("aws.region").toString();
    }
}
