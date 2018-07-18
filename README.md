# DBDocUtil
Springboot自动生成整个数据库文档 doc 文件
使用springboot2，所依赖的jar
  <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
  <properties>

		<!-- 文件拷贝时的编码 -->
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<!-- 编译时的编码 -->
		<maven.compiler.encoding>UTF-8</maven.compiler.encoding>
		<!-- jdk -->
		<jdk.version>1.8</jdk.version>
		
		<!-- junit test -->
		<junit.version>4.12</junit.version>
		
		<!-- Spring 版本 -->
		<spring.version>5.0.7.RELEASE</spring.version><!-- 4.3.17.RELEASE ；5.0.6.RELEASE -->
	
		<!-- Oracle驱动包 -->
		<oracle.version>11.2.0.1.0</oracle.version><!-- 10.2.0.2.0 -->
  		<!-- Mysql驱动包 -->
  		<mysql.version>5.1.46</mysql.version>
  </properties>
  
  <dependencies>
  
  		<!-- Oracle驱动包 -->
		<dependency>
			<groupId>com.oracle</groupId>
			<artifactId>ojdbc6</artifactId>
			<version>${oracle.version}</version>
		</dependency>
		<!-- Mysql驱动包 -->
		<dependency>
		    <groupId>mysql</groupId>
		    <artifactId>mysql-connector-java</artifactId>
		</dependency>
		
		<dependency>
		    <groupId>org.freemarker</groupId>
		    <artifactId>freemarker</artifactId>
		</dependency>
		  <!-- 热部署 -->
		<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-devtools</artifactId>
		    <optional>true</optional>
		    <scope>true</scope>
		</dependency>
    
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-thymeleaf</artifactId>          
		</dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
     
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
		
  </dependencies>
  
  具体逻辑
  1.查询数据库所有的表
  oracle
  select t.table_name,f.comments from user_tables t inner join user_tab_comments f on t.table_name = f.table_name
  mysql
  select table_name,table_comment from information_schema.tables where table_schema='数据库名' and table_type='base table'
  
  得到数据库所有表的集合，然后进行遍历，查询没张表的字段以及备注等信息
  
  oracle
  select  tab.column_name ,tab.data_type,tab.char_length, tab.data_precision, tab.data_scale ,tab.nullable,tab.data_default , col.comments from user_tab_columns tab,user_col_comments col	where  tab.Table_Name = col.Table_Name   and tab.Column_Name = col.Column_Name  and tab.Table_Name ='表名';
	
  mysql
  select column_name,column_type,character_maximum_length,numeric_precision, numeric_scale,is_nullable,column_default,column_comment from information_schema.columns where table_schema='数据库名' and table_name='表名'
  
  
  得到数据库所有的表以及表字段相关信息
  
  采用 FreeMarker 根据模板生成doc
  创建doc文件，设置样式，然后进行另存为xml（最好是2003版本的）,讲xml放到项目中
  
  把项目内容写入模板，生成doc文档
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
  
  
  
  xml模板文件遍历数据
  
  <#if tableList?? && (tableList?size > 0) >
    <#list tableList as tb >
      ${(tb.tableName)!''}
      ${(tb.comments)!''}
      <#if tb.getColumnList()?? && (tb.getColumnList()?size > 0) >
	      <#list tb.getColumnList() as column>
          ${(column.columnName)!''}
          ${(column.comments)!''}
          ......
        </#list>
	    </#if>
    </#list>
	</#if>
