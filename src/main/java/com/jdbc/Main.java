package com.jdbc;

import com.jdbc.dao.JDBCMappingDAO;
import com.jdbc.vo.Mapping;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String args[]){
		
		Mapping mapping1 = new Mapping("ddwwwwww22223234edewf", "cwefewwwrrfewf", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		Mapping mapping2 = new Mapping("ddwwwww11wwwwedewf", "wwww", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		Mapping mapping3 = new Mapping("ddwwwwwdddddwwedewf", "cwefewrreeefewf", "wefefereefref", new Timestamp(System.currentTimeMillis()));

		List<Mapping> recs = Arrays.asList(mapping1, mapping2, mapping3);

		JDBCMappingDAO jdbcPersonDAO = new JDBCMappingDAO();
		jdbcPersonDAO.getConnection();
//		jdbcPersonDAO.batchInsert(recs);

//		jdbcPersonDAO.deleteAll();

//		jdbcPersonDAO.insert(mapping1);

//		Mapping mapping = jdbcPersonDAO.load("ddwwwwww22223234edewf");

//		System.out.println(mapping);

		System.out.println(jdbcPersonDAO.count());
//		jdbcPersonDAO.deleteAll();
//		jdbcPersonDAO.select();
//		jdbcPersonDAO.closeConnection();


		
	}
}
