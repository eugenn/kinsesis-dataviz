/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.kinesis.datavis.utils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javax.servlet.http.HttpServlet;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A collection of utilities for the Amazon Kinesis sample application.
 */
public class AppUtils {

    /**
     * Creates a new client configuration with a uniquely identifiable value for this sample application.
     * 
     * @param clientConfig The client configuration to copy.
     * @return A new client configuration based on the provided one with its user agent overridden.
     */
    public static ClientConfiguration configureUserAgentForSample(ClientConfiguration clientConfig) {
        ClientConfiguration newConfig = new ClientConfiguration(clientConfig);
        StringBuilder userAgent = new StringBuilder(ClientConfiguration.DEFAULT_USER_AGENT);

        // Separate regions of the UserAgent with a space
        userAgent.append(" ");
        // Append the repository name followed by version number of the sample
        userAgent.append("amazon-kinesis-data-visualization-sample/1.1.2");

        newConfig.setUserAgent(userAgent.toString());

        return newConfig;
    }

    /**
     * Creates a Region object corresponding to the AWS Region. If an invalid region is passed in
     * then the JVM is terminated with an exit code of 1.
     * 
     * @param regionStr the common name of the region for e.g. 'us-east-1'.
     * @return A Region object corresponding to regionStr.
     */
    public static Region parseRegion(String regionStr) {
        Region region = RegionUtils.getRegion(regionStr);

        if (region == null) {
            System.err.println(regionStr + " is not a valid AWS region.");
            System.exit(1);
        }
        return region;
    }

    public static HttpServlet instServlet(String name, DynamoDBMapper mapper) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> clazz = Class.forName("com.kinesis.datavis.servlet." + name);
        Constructor<?> constructor = clazz.getConstructor(DynamoDBMapper.class);
        return (HttpServlet) constructor.newInstance(mapper);
    }

}
