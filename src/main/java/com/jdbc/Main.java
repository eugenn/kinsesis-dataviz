package com.jdbc;

import com.jdbc.dao.JDBCMappingDAO;
import com.jdbc.vo.Mapping;
import com.kinesis.datavis.model.record.BidWinRec;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String args[]){
		
		Mapping mapping1 = new Mapping("ddwwwwww22223234edewf", "cwefewwwrrfewf", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		Mapping mapping2 = new Mapping("ddwwwww11wwwwedewf", "wwww", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		Mapping mapping3 = new Mapping("ddwwwwwdddddwwedewf", "cwefewrreeefewf", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		List<Mapping> recs = Arrays.asList(mapping1, mapping2, mapping3);

		JDBCMappingDAO jdbcPersonDAO = new JDBCMappingDAO();
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

		Map map = new HashMap<>();
		map.put(rec1, 0);
		map.put(rec2, 1);
		map.put(rec3, 2);





		System.out.println(map.size());

		System.out.println(map.get(rec2));
//		jdbcPersonDAO.deleteAll();
//		jdbcPersonDAO.select();
//		jdbcPersonDAO.closeConnection();


		
	}
}
