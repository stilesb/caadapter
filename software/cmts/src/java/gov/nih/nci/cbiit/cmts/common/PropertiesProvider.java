/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.cbiit.cmts.common;

import gov.nih.nci.cbiit.cmts.ui.properties.PropertiesResult;




/**
 * Define the interface to provide underline data or meta bean's properties information
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2009-10-27 18:20:31 $
 */
public interface PropertiesProvider
{
	/**
	 * Return the title of this provider that may be used to distinguish from others.
	 * @return the title of this provider that may be used to distinguish from others.
	 */
	String getTitle();
	/**
	 * This functions will return an array of PropertyDescriptor that would
	 * help reflection and/or introspection to figure out what information would be
	 * presented to the user.
	 */
	public PropertiesResult getPropertyDescriptors() throws Exception;
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/12/29 22:18:18  linc
 * HISTORY      : function UI added.
 * HISTORY      :
 */
