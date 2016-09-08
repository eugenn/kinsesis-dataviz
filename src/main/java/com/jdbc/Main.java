package com.jdbc;

import com.jdbc.vo.Mapping;
import com.kinesis.datavis.app.CounterApp;
import com.kinesis.datavis.model.record.BidWinRec;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

		TimeZone UTC = TimeZone.getTimeZone("UTC");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(UTC);



		System.out.println(new Timestamp(System.currentTimeMillis() * 1000).toString());



		BidWinRec rec1 = new BidWinRec("req1", "bn123", "a123", 0d);
		BidWinRec rec2 = new BidWinRec("req2", "bn123", "a123", 0d);
		BidWinRec rec3 = new BidWinRec("req3", "bn123", "a123", 0d);

//		for (Mapping rec : recs) {
//			System.out.println(System.currentTimeMillis());
//			System.out.println(UUID.randomUUID().toString());
//		}


		BigDecimal bd = new BigDecimal((System.nanoTime()));

		System.out.println(bd.toEngineeringString());

		System.out.println(System.currentTimeMillis() + "");
		System.out.println(System.nanoTime() + "");

		long millis = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);

		Date date = new Date(millis );
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm aaa");
		String dateFormatted = formatter.format(date);

		System.out.println(dateFormatted);


		CounterApp counterApp = new CounterApp();
//		Properties props = counterApp.loadProperties();

//		AppProperties appProperties = new AppProperties("bidrq", "/Users/eugennekhai/Projects/Streams/kinsesis-dataviz/src/main/resources/app.properties");

//		System.out.println(appProperties.streamName());

//		System.out.println(map.size());

//		System.out.println(map.get(rec2));
//		jdbcPersonDAO.deleteAll();
//		jdbcPersonDAO.select();
//		jdbcPersonDAO.closeConnection();


		
	}
}
