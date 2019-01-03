package snu.bioinfo.pathway.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



import snu.bioinfo.pathway.controller.RunController;
import snu.bioinfo.pathway.domain.ToolVO;
import snu.bioinfo.pathway.persistence.MailHandler;

@Service("asyncTask")
public class AsyncTask {
	
	@Inject
	private JavaMailSender mailSender;
	
	@Autowired
	private MailHandler mh;
	
	private static final Logger logger = LoggerFactory.getLogger(RunController.class);
	
		@Async("executor")
	    public void executor(ToolVO tvo, ArrayList<String> fileName) throws MessagingException, UnsupportedEncodingException {
		 	ArrayList<String> toolSet = tvo.getToolSet();
	        String inputCSV = fileName.get(0);
	        String infoCSV = fileName.get(1);
	        String projectName = tvo.getProjectName();
	        
	        for(int i = 0; i < toolSet.size(); i++) {
	        	
				Process p = null;
				ProcessBuilder pb = null;
				List<String> cmdList = new ArrayList<String>();
				String toolName = toolSet.get(i);
				
				// adding command and args to the list
		        try {		    		
		    		 
			         switch(toolName) {
				         case "GSVAdif" :
				        	 cmdList.add("./GSVAdif.R");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);  
				  	         break;
				         case "GSVAmax" :
				        	 cmdList.add("./GSVAmax.R");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);  
				        	 break;
				         case "CORG" :
				        	 cmdList.add("./CORG.R");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);  
				        	 break;
				         case "PADOG_prepare" :
				        	 cmdList.add("./PADOG_prepare.py");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);
				        	 break;
				         case "PADOG" :
				        	 cmdList.add("./PADOG.py");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);				 
				        	 break;
				         case "PLAGE" :
				        	 cmdList.add("./PLAGE.R");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);				 
				        	 break;
				         case "PathAct" :
				        	 cmdList.add("./PathAct.R");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);				 
				        	 break;
				         case "ssGSEA" :
				        	 cmdList.add("./ssGSEA.R");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);				 
				        	 break;
				         case "Test" :
				        	 cmdList.add("sh");
				        	 cmdList.add("./test.sh");		
				        	 break;
				         case "GSVA" :
				        	 cmdList.add("./GSVA.R");
				        	 cmdList.add(inputCSV);
				        	 cmdList.add(infoCSV);
				        	 cmdList.add(projectName);  	
				        	 break;
			        }	
			         pb = new ProcessBuilder(cmdList);
				        pb.directory(new File("/data/home/pathwaycloud/PathwayWebService/bin"));
						p = pb.start();			
						/*BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
				         String line; 
				         while((line = reader.readLine()) != null) { 
				        	 logger.info(line);
				         }  */        
		        	 p.waitFor();
		        	 
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					if(null != p) {
						p.destroy();
						
					}else {
						throw new RuntimeException("Cannot close Process Streams");
					}
				}
	        }
			 mh = new MailHandler(mailSender);	        	 
				mh.setFrom("sophia5848@gmail.com");
				mh.setTo(tvo.getUserEmail());
				mh.setSubject(tvo.getTitle());
				mh.setText(tvo.getContent());	        	 
				mh.send();	  
			
	    }
}
