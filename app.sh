#!/usr/bin/env bash
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.writer.BidRequestStreamWriter 2 /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/brq-writer.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.app.BidRequestCounter /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/brq-counter.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.server.WebServer 8081 bidrq /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/brq-server.log &

nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.writer.BidWinStreamWriter 2 /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/bwin-writer.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.app.BidWinCounter /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/bwin-counter.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.server.WebServer 8080 bidwin /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/bwin-server.log &

nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.writer.BidResponseStreamWriter 2 /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/brsp-writer.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.app.BidResponseCounter /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/brsp-counter.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.server.WebServer 8082 bidrsp /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/brsp-server.log &

nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.writer.ImpressionStreamWriter 2 /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/imp-writer.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.app.ImpressionCounter /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/imp-counter.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.server.WebServer 8083 impression /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/imp-server.log &

nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.writer.ClicksStreamWriter 2 /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/cks-writer.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.app.ClicksCounter /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/cks-counter.log &
nohup java -cp "kinesis-1.1.2.jar:lib/*" com.kinesis.datavis.server.WebServer 8084 clicks /home/ec2-user/java/app.properties &>> /home/ec2-user/java/logs/cks-server.log &