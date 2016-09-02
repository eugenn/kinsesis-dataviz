package com.jdbc.dao;

import com.jdbc.vo.Mapping;

import java.util.List;

public interface MappingDAO {
	
	void insert(Mapping mapping);
	Mapping load(String bidrequestId);
	void batchInsert(List<Mapping> mappings);
	void deleteAll();
	List<Mapping> select();

	long count();

}
