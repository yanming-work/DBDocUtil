package com.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.aop.ThrowsAdvice;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class BDDocUtil {

	
	
	public static  String DBType =null;
	private static String driverName = null;
	private static String dbName =null;
	private static String url = null;
	private static String user =null;
	private static String password = null;
	
	
	 public static void main(String[] args) {
		 
		 
			 //mysql
			DBType ="mysql";
			driverName = "com.mysql.jdbc.Driver";
			//高版本mysql需要设置 useSSL
			dbName = "test";
			url = "jdbc:mysql://127.0.0.1:3306/"+dbName+"?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false";
			user = "root";
			password = "123456";
		
			try{
			 	String filePath="d://db.doc";
			 	String dbTemplateFileName="DBTemplate.xml";
			 	genWordFile(createDBTableList(), filePath, dbTemplateFileName);
			 	System.out.println("数据库文档生成成功！");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	 
	 
	 public static String createDoc(String dbType,String driverName,String dbName,String url,String user,String password,String docFilePath,String dbTemplateFileName) {
		 String msg="数据库文档生成失败!";	
		 try{
		 if(dbTemplateFileName==null || "".equals(dbTemplateFileName)){
		 		dbTemplateFileName="DBTemplate.xml";
		 	}
		 	
		 	if(docFilePath==null || "".equals(docFilePath)){
		 		docFilePath="D://数据库文档.doc";
		 	}
		 	
		 	
		 	BDDocUtil.DBType =dbType;
		 	BDDocUtil.driverName = driverName;
		 	BDDocUtil.dbName =dbName;
		 	BDDocUtil.url = url;
		 	BDDocUtil.user = user;
		 	BDDocUtil.password = password;
		 	
		 	genWordFile(createDBTableList(), docFilePath, dbTemplateFileName);
		 	System.out.println("数据库文档生成成功！");
		 	msg="数据库文档生成成功！请查看文件："+docFilePath;
		 }catch (Exception e) {
			e.printStackTrace();
			msg="数据库文档生成失败!程序异常!-----错误："+e.getMessage();
		 }
		 	return msg;
		}
	 
	
	private static ResultSet rSet = null;
	private static Connection conn = null;
	private static PreparedStatement    pst=null;
	// 初始化数据库链接
    public static Connection initDB() throws Exception{
        // 不同的数据库有不同的驱动
    	
            // 加载驱动
            Class.forName(driverName);
            // 设置 配置数据
            // 1.url(数据看服务器的ip地址 数据库服务端口号 数据库实例)
            // 2.user
            // 3.password
              conn = DriverManager.getConnection(url, user, password);
            // 开始连接数据库
            //System.out.println("数据库连接成功..");
            
       
        return conn;
    }
    
    
    
    
    
    public static  List<Table>    createDBTableList() throws Exception{
    	List<Table> tableList=null;
    	try{
    	//初始化，获取数据库链接
  	  	  conn =initDB();
        	//获取所有的数据库表
    	   	  tableList=getAllTables( conn);
    	   	 if(tableList!=null && tableList.size()>0){
    	   		for (int i=0;i< tableList.size();i++) {
        	   		//查询表的字段
        	   		List<TableColumn> tableColumnList=getTableColumns(tableList.get(i).getTableName(),conn);
        	   		if(tableColumnList!=null && tableColumnList.size()>0){
        	   			tableList.get(i).setColumnList(tableColumnList);
        	   		}
        	   		System.out.println((i+1)+"/"+tableList.size()+",获取表信息"+tableList.get(i).getTableName());
        	   	}
    	   	 }
    	   	 
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally{
	        try {
	            if(rSet !=null)rSet.close();
	        } catch (Exception e) {
	        }
	        try {
	            if(pst !=null)pst.close();
	        } catch (Exception e) {
	        }
	        try {
	            if(conn !=null)conn.close();
	        } catch (Exception e) {
        }

    } 
    	   	
    	   	return tableList;
	   	
				
    }

    
    
    
private static Configuration freemarkerConfig;
	
	static {
		freemarkerConfig = new Configuration(Configuration.VERSION_2_3_28);
		freemarkerConfig.setEncoding(Locale.getDefault(), "UTF-8");
	}
    
    
	
	public static void genWordFile( List<Table> tableList,String filePath,final String dbTemplateFileName) throws Exception {
		
			System.out.println("开始生成doc文档");
			Map<String,Object> result = new HashMap<String,Object>();
			
			result.put("title", "数据库文档");
			result.put("tableList", tableList);

			freemarkerConfig.setTemplateLoader(new URLTemplateLoader() {
				
				@Override
				protected URL getURL(String arg0) {
					URL url=null;
					//此处需要注意test.xml模板的路径,不要搞错了，否则获取不到模板，我是放在src/main/java目录下,/test.xml
					if(BDDocUtil.class.getResource(dbTemplateFileName)!=null){
						url=BDDocUtil.class.getResource(dbTemplateFileName);
					}else{
						url=BDDocUtil.class.getResource("/"+dbTemplateFileName);
					}
					return url;
				}
			});
			
			Template temp = freemarkerConfig.getTemplate(dbTemplateFileName);
			
			File targetFile = new File(filePath);
			Writer out = new OutputStreamWriter(new FileOutputStream(targetFile),"UTF-8");
			
			//执行模板替换
			temp.process(result, out);
			out.flush();
	}
	
    
    /***
     * 
		Oracle 查询库中所有表名、字段名、字段名说明，查询表的数据条数、表名、中文表名、
		
		查询所有表名：
		select t.table_name from user_tables t;
		查询所有字段名：
		select t.column_name from user_col_comments t;
		查询指定表的所有字段名：
		select t.column_name from user_col_comments t where t.table_name = 'BIZ_DICT_XB';
		查询指定表的所有字段名和字段说明：
		select t.column_name, t.comments from user_col_comments t where t.table_name = 'BIZ_DICT_XB';
		查询所有表的表名和表说明：
		select t.table_name,f.comments from user_tables t inner join user_tab_comments f on t.table_name = f.table_name;
		查询模糊表名的表名和表说明：
		select t.table_name from user_tables t where t.table_name like 'BIZ_DICT%';
		select t.table_name,f.comments from user_tables t inner join user_tab_comments f on t.table_name = f.table_name where t.table_name like 'BIZ_DICT%';
		
		--查询表的数据条数、表名、中文表名
		select a.num_rows, a.TABLE_NAME, b.COMMENTS
		from user_tables a, user_tab_comments b
		WHERE a.TABLE_NAME = b.TABLE_NAME
		order by TABLE_NAME;
     */
    
    public static List<Table> getAllTables(Connection conn)  throws Exception{
    	 List<Table> tableList=null;
		    	
		        // *查询
		        // 创建sql语句
		        String sqlString =null;
		        if(DBType!=null && conn!=null){
		        	if("ORACLE".equals(DBType.trim().toUpperCase())){
		        		 sqlString= "select t.table_name,f.comments from user_tables t inner join user_tab_comments f on t.table_name = f.table_name";
		        	}else if("MYSQL".equals(DBType.trim().toUpperCase())){
		        		sqlString= "select table_name,table_comment from information_schema.tables where table_schema='"+dbName+"' and table_type='base table'";
		        	} 
				        	
				        	//查询数据库
				        	if(sqlString!=null && !"".equals(sqlString)){
				        		
				        		 // 编译sql语句
				                // 执行查询
				                  pst = conn.prepareStatement(sqlString);
				                  rSet = pst.executeQuery();
				                // 遍历结果
				                tableList= new ArrayList<Table>();
				              //必须通过BDDocUtil对象来创建
				                while (rSet.next()) {
				                	Table table= new Table();
				                     //System.out.print(rSet.getString(1) + "\t");
				                     //System.out.print(rSet.getString(2) + "\t");
				                     //System.out.print("\n");
				                     table.setTableName(rSet.getString(1));
					                 table.setComments(replaceSpecialCharOfXmlStr(rSet.getString(2)));
				                     tableList.add(table);
				                }
				        	}
		        	 
		        }
    	 
    	 return tableList;
    }
    
    
    
    
   
    
    
    
    public static List<TableColumn> getTableColumns(String tableName,Connection  conn)  throws Exception{
   	 List<TableColumn> tableColumnList=null;
		        // *查询
		        // 创建sql语句
		        String sqlString =null;
		        if(DBType!=null && conn!=null && tableName!=null && !"".equals(tableName)){
		        	if("ORACLE".equals(DBType.trim().toUpperCase())){
		        		 sqlString= " select  tab.column_name ,tab.data_type,tab.char_length, tab.data_precision, tab.data_scale ,tab.nullable,tab.data_default , col.comments "
								 +"from user_tab_columns tab,user_col_comments col	where  tab.Table_Name = col.Table_Name   and tab.Column_Name = col.Column_Name  and tab.Table_Name ='"+tableName+"'";
							
		        	}else if("MYSQL".equals(DBType.trim().toUpperCase())){
		        		//data_type
						 sqlString= " select column_name,column_type,character_maximum_length,numeric_precision, numeric_scale,is_nullable,column_default,column_comment from information_schema.columns where table_schema='"+dbName+"' and table_name='"+tableName+"'";
					
		        	}
				        	
				        	//查询数据库
				        	if(sqlString!=null && !"".equals(sqlString)){
				        		
				        		 // 编译sql语句
				                // 执行查询
				                pst = conn.prepareStatement(sqlString);
				                rSet = pst.executeQuery();
				                // 遍历结果
				                tableColumnList= new ArrayList<TableColumn>();
				              //必须通过BDDocUtil对象来创建
				               
				                while (rSet.next()) {
				                	 TableColumn tableColumn= new TableColumn();
				                     //System.out.print(rSet.getString(1) + "\t");
				                     //System.out.print(rSet.getString(2) + "\t");
				                     //System.out.print("\n");
				                	tableColumn.setColumnName(rSet.getString(1));
				                	
				                	if("ORACLE".equals(DBType.trim().toUpperCase())){
				                		String dataType=rSet.getString(2);
				                		if(rSet.getInt(3)<=0){
				                			if(rSet.getInt(4)>0){
				                				dataType=dataType+"("+rSet.getInt(4)+","+rSet.getInt(5)+")";
				                			}
				                		}else{
				                			dataType=dataType+"("+rSet.getInt(3)+")";
				                		}
				                		tableColumn.setDataType(dataType);	
				                	}else{
				                		tableColumn.setDataType(rSet.getString(2));
				                	}
				                	
				                	tableColumn.setNullable(rSet.getString(6));
				                	tableColumn.setDataDefault(rSet.getString(7));
				                	tableColumn.setComments( replaceSpecialCharOfXmlStr(rSet.getString(8)));
				                     tableColumnList.add(tableColumn);
				                }
				        	}
		        	 
		        }
   	 
   	 return tableColumnList;
   }
    
    
    
 
    
    
    public static String   replaceSpecialCharOfXmlStr(String str){
    	if(str!=null){
    		if(str.indexOf("<")>=0){
    			//&lt; < 小于号
    			str=str.replace("<", "&lt;");
    		}
    		
    		if(str.indexOf(">")>=0){
    			// &gt; > 大于号
    			str=str.replace(">", "&gt;");
    		}
    		
    		if(str.indexOf("&")>=0){
    			// &amp; & 和
    			str=str.replace("&", "&amp;");
    		}
    		
    		if(str.indexOf("'")>=0){
    			str=str.replace("'", "&apos;");
    		}
    		
    		if(str.indexOf("\"")>=0){
    			// &quot; " 双引号 
    			str=str.replace("\"", "&quot;");
    		}
    	}
    	return str;
    }
    

   
    
   
  
    
	
}
