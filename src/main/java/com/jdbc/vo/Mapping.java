package com.jdbc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Mapping {
	String bidrequestId;
	String bannerId;
	String audienceId;
	Timestamp timestamp;

}
