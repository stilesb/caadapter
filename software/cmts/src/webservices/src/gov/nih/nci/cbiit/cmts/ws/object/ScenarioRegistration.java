/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.cbiit.cmts.ws.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Apr 2, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-04-13 15:25:05 $
 * @since caAdapter v4.2
 */

public class ScenarioRegistration {

	private String name;
	private String transferType;
	private String mappingFile;
	private String targetFile;
	private String sourceSpecFile;
	private List<String> vocabuaryMappings;
	private Date dateCreated;
    private List<String> subSourceSpecFiles;
    private List<String> subTargetSpecFiles;

    public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	/**
	 * @return the createDate
	 */
	public Date getDateCreate() {
		return dateCreated;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setDateCreate(Date createDate) {
		this.dateCreated = createDate;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the mappingFile
	 */
	public String getMappingFile() {
		return mappingFile;
	}
	/**
	 * @param mappingFile the mappingFile to set
	 */
	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}
	/**
	 * @return the targetFile
	 */
	public String getTargetFile() {
		return targetFile;
    }
//    public String getTargetFile() {
//		String res = targetFile;
//        if ((subTargetSpecFiles == null)||(subTargetSpecFiles.size() == 0)) return res;
//
//        for (String str:subTargetSpecFiles)
//        {
//            res = res + "<br>" + str;
//        }
//        return res;
//    }
    /**
	 * @param targetFile the targetFile to set
	 */
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}


    /**
	 * @return the sourceSpecFile
	 */
	public String getSourceSpecFile() {
		return sourceSpecFile;
	}
//    public String getSourceSpecFile() {
//		String res = sourceSpecFile;
//        if ((subSourceSpecFiles == null)||(subSourceSpecFiles.size() == 0)) return res;
//
//        for (String str:subSourceSpecFiles)
//        {
//            res = res + "<br>" + str;
//        }
//        return res;
//    }
    /**
	 * @param sourceSpecFile the sourceSpecFile to set
	 */
	public void setSourceSpecFile(String sourceSpecFile) {
		this.sourceSpecFile = sourceSpecFile;
	}
	/**
	 * @return the vocabuaryMappings
	 */
	public List<String> getVocabuaryMappings() {
		return vocabuaryMappings;
	}
	/**
	 * @param vocabuaryMappings the vocabuaryMappings to set
	 */
	public void setVocabuaryMappings(List<String> vocabuaryMappings) {
		this.vocabuaryMappings = vocabuaryMappings;
	}

	/**
	 * Add one VOM into mapping scenario
	 * @param newVom
	 */
	public void addVocabuaryMappingFile(String newVom)
	{
		if (vocabuaryMappings==null)
			vocabuaryMappings=new ArrayList<String>();
		vocabuaryMappings.add(newVom);
	}

    /**
	 * Add one source spec file into mapping scenario
	 * @param newFile
	 */
    public void addSubSourceSpecFiles(String newFile)
	{
		if (subSourceSpecFiles==null)
			subSourceSpecFiles=new ArrayList<String>();
		subSourceSpecFiles.add(newFile);
	}
    /**
	 * @return the subSourceSpecFiles
	 */
	public List<String> getSubSourceSpecFiles() {
		return subSourceSpecFiles;
	}
//    public String getSubSourceSpecFiles() {
//		if ((subSourceSpecFiles == null)||(subSourceSpecFiles.size() == 0)) return "";
//        String res = "";
//        for (String str:subSourceSpecFiles)
//        {
//            res = "<br/>" + str;
//        }
//        return res;
//    }

    /**
	 * Add one target spec file into mapping scenario
	 * @param newFile
	 */
    public void addSubTargetSpecFiles(String newFile)
	{
		if (subTargetSpecFiles==null)
			subTargetSpecFiles=new ArrayList<String>();
		subTargetSpecFiles.add(newFile);
	}
    /**
	 * @return the subTargetSpecFiles
	 */
	public List<String> getSubTargetSpecFiles() {
		return subTargetSpecFiles;
	}
//    public String getSubTargetSpecFiles() {
//		if ((subTargetSpecFiles == null)||(subTargetSpecFiles.size() == 0)) return "";
//        String res = "";
//        for (String str:subTargetSpecFiles)
//        {
//            res = "<br/>" + str;
//        }
//        return res;
//    }
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/