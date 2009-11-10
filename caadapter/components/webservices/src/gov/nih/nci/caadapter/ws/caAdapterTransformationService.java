/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ws;

import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.ws.object.ScenarioRegistration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * caadapter Web Service to provide transformation service
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.8 $
 * @date $$Date: 2009-04-14 20:02:40 $
 * @since caadapter v1.3.1
 */

public class caAdapterTransformationService {

	/**
     * caadapter Web Service to provide transformation service
     *
     * @param mappingScenario The name of the mapping scenario 
     * @param csvString csv data in String format 
     * @return A collection of the transformed HL7 v3 message
     */

	public ArrayList<String> transformationService(String mappingScenario, String csvString) {

		  ArrayList<String> result = new ArrayList<String>(); 
		  
		  try {
			  ScenarioRegistration scenario=ScenarioUtil.findScenario(mappingScenario);
			  if (scenario==null)
			  {
				  result.add("Scenario is not found:"+mappingScenario);
				  return result;
			  }
			  String path =ScenarioUtil.SCENARIO_HOME;
			  String scenarioPath=path+"/"+mappingScenario;
			  boolean exists = (new File(scenarioPath)).exists();
			  if (exists) 
			  {
				  System.out.println("caAdapterTransformationService.transformationService()..path:"+path);
				  String mappingFileName = scenarioPath+"/"+scenario.getMappingFile();
				  System.out.println("mapping file:"+mappingFileName);
	
				  TransformationService transformationService = 
					  new TransformationService(mappingFileName,csvString,true);
				  System.out.println("caAdapterTransformationService.transformationService()..start transformation");
				  List<XMLElement> mapGenerateResults = transformationService.process();
				  System.out
						.println("caAdapterTransformationService.transformationService()..generated message count:"+mapGenerateResults.size());
				  for (int i = 0; i < mapGenerateResults.size(); i++)
				  {
					  XMLElement hl7Xml = mapGenerateResults.get(i);
					  System.out.println("caAdapterTransformationService.transformationService()..message toString:"+hl7Xml);
					  result.add(hl7Xml.toXML().toString());
				  }
				  result.add("\n\nprocessed");
				  return result;
			  } else {
				  result.add("Scenario files are not found:"+scenarioPath);
				  return result;
			  }
		  }catch(Exception e)
		  {
			  result.add(e.getStackTrace().toString());
			  e.printStackTrace();
		  }
		  result.add("no HL7 message");
		  return result;
	}
}