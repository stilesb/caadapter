/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.common;


/**
 * Define a list of constants that are used by menu and some of action definition.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-12-09 19:04:17 $
 */
public interface ActionConstants
{
	//The naming convention:
	//NEW_MAP_FILE is used as the key to the text, while the NEW_MAP_FILE_TXT is used as the action name;
	//this is because the action name in display may be the same but we really need to distinguish them in a map
	//between a new and an open command.

	String OPEN = "Open...";
	String CLOSE = "Close";
	String CLOSE_ALL = "Close All";
	String SAVE = "Save";
	String SAVE_AS = "Save As...";
	String EXIT = "Exit";
	String HELP_TOPIC = "HELP_TOPIC";

	String HELP = "Help ";
	String GENERATE_REPORT = "Generate Report...";
	String VALIDATE = "Validate";
	String ANOTATE = "Anotate";
	String REFRESH = "Refresh";
	String NEW_MAP_FILE = "Transformation Mapping";
	String OPEN_MAP_FILE = "Open Transformation Mapping";
	String NEW_XML_Transformation = "XML to XML Transformation";
	String NEW_XSLT_STYLESHEET="XSLT Stylesheet Artifact";
	String NEW_XQUERY_STATEMENT="XQuery Artifact";
    //String NEW_ADD_SCENARIO_TAG = "***";
    String NEW_CSV_Transformation = "CSV to XML Transformation";
	String NEW_HL7_V2_Transformation = "HL7 v2 to XML Transformation";
	String NEW_XML_CDA_Transformation = "XML to CDA Transformation";
	String NEW_CSV_CDA_Transformation = "CSV to CDA Transformation";
	String NEW_HL7_V2_CDA_Transformation = "HL7 v2 to CDA Transformation";
    String FILE_NAME_UNTITLED_TAG = "Untitled_";
    String MESSAGE_NOT_A_MAPPING_FILE = "NOT A MAPPING FILE....";
	String SCENARIO_DELETE_SECURITY_CONFIRMATION_CODE_FILE = "SecurityCode.txt";
    //String CMTS_HELP_MENU_CONTENT_URL = "https://wiki.nci.nih.gov/display/caCORE/caAdapter+Common+Mapping+and+Transformation+Service+v1.0+User%27s+Guide";
    String CMTS_HELP_MENU_CONTENT_URL = "https://wiki.nci.nih.gov/x/TI14B";

}

/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY : UI update.
 * HISTORY :
 * HISTORY : Revision 1.1  2008/11/04 15:58:57  linc
 * HISTORY : updated.
 * HISTORY :
 */
