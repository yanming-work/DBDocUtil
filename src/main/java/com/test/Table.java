package com.test;

import java.util.List;

public class Table {

	private String tableName;
	private String comments;
	
	private List<TableColumn> columnList;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<TableColumn> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<TableColumn> columnList) {
		this.columnList = columnList;
	}
	

}
