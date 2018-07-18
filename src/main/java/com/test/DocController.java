package com.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/doc")
public class DocController {
	
 	@RequestMapping("/")
 	public ModelAndView index(){    
	    return new ModelAndView("index.html");    
	} 
 	
 	@RequestMapping("")
 	public ModelAndView index2(){    
	    return new ModelAndView("index.html");    
	} 
 	
	@RequestMapping(value = "/init")  
	public String init(String dbType,String driverName,String ip,String port,String dbName,String user,String password,String docFilePath,String dbTemplateFileName,Model model){    
		String msg="参数有误！或缺失参数！";
		if(dbType!=null && !"".equals(dbType)&& ip!=null&& !"".equals(ip) && port !=null && !"".equals(port)&& dbName!=null&& !"".equals(dbName)){
			String url=null;
			if("ORACLE".equals(dbType.trim().toUpperCase())){
				//"jdbc:oracle:thin:@127.0.0.1:1521:orcl"
				if(dbName.indexOf(":")==0){
					url="jdbc:oracle:thin:@"+ip+":"+port+":"+dbName;
				}else{
					if(dbName.indexOf("/")==0){
						url="jdbc:oracle:thin:@"+ip+":"+port+dbName;
					}else{
						url="jdbc:oracle:thin:@"+ip+":"+port+"/"+dbName;
					}
				}
				
				
			}else if("MYSQL".equals(dbType.trim().toUpperCase())){
				//"jdbc:mysql://127.0.0.1:3306/"+dbName+"?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false";
				
				url="jdbc:mysql://"+ip+":"+port+"/"+dbName+"?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false";
		
			}
			
			msg=BDDocUtil.createDoc(dbType, driverName, dbName, url, user, password, docFilePath, dbTemplateFileName);
			 
		}
		
		
		 model.addAttribute("msg", msg);
         return "success";
	} 
}
