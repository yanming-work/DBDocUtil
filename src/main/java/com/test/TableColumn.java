package com.test;

public class TableColumn {


	private String columnName;
	private String dataType;
	private String nullable;
	private String dataDefault;
	private String comments;
	
	
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getNullable() {
		return nullable;
	}
	
	
	public void setNullable(String nullable) {
		
		if("ORACLE".equals(BDDocUtil.DBType.trim().toUpperCase())){
			if("Y".equals(nullable)){
				nullable="TRUE";
			}else {
				nullable="FALSE";
			}
		}else if("MYSQL".equals(BDDocUtil.DBType.trim().toUpperCase())){
			
		}
		
		this.nullable = nullable;
	}
	public String getDataDefault() {
		return dataDefault;
	}
	public void setDataDefault(String dataDefault) {
		this.dataDefault = dataDefault;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	


}
