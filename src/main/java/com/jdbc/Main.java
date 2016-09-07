package com.jdbc;

import com.jdbc.vo.Mapping;
import com.kinesis.datavis.app.CounterApp;
import com.kinesis.datavis.model.record.BidWinRec;
import com.kinesis.datavis.utils.AppProperties;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String args[]){
		
		Mapping mapping1 = new Mapping("ddwwwwww22223234edewf", "cwefewwwrrfewf", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		Mapping mapping2 = new Mapping("ddwwwww11wwwwedewf", "wwww", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		Mapping mapping3 = new Mapping("ddwwwwwdddddwwedewf", "cwefewrreeefewf", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		List<Mapping> recs = Arrays.asList(mapping1, mapping2, mapping3);

//		JDBCMappingDAO jdbcPersonDAO = new JDBCMappingDAO();
//		jdbcPersonDAO.getConnection();
//		jdbcPersonDAO.batchInsert(recs);

//		jdbcPersonDAO.deleteAll();

//		jdbcPersonDAO.insert(mapping1);

//		Mapping mapping = jdbcPersonDAO.load("ddwwwwww22223234edewf");

//		System.out.println(mapping);

//		System.out.println(jdbcPersonDAO.count());

		BidWinRec rec1 = new BidWinRec("req1", "bn123", "a123", 0d);
		BidWinRec rec2 = new BidWinRec("req2", "bn123", "a123", 0d);
		BidWinRec rec3 = new BidWinRec("req3", "bn123", "a123", 0d);




		CounterApp counterApp = new CounterApp();
//		Properties props = counterApp.loadProperties();

		AppProperties appProperties = new AppProperties("bidrq", "/Users/eugennekhai/Projects/Streams/kinsesis-dataviz/src/main/resources/app.properties");

		System.out.println(appProperties.streamName());

//		System.out.println(map.size());

//		System.out.println(map.get(rec2));
//		jdbcPersonDAO.deleteAll();
//		jdbcPersonDAO.select();
//		jdbcPersonDAO.closeConnection();


		
	}
}
