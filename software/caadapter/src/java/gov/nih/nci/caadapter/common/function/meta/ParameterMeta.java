/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.function.meta;

import gov.nih.nci.caadapter.common.MetaObject;

/**
 * interface ParameterMeta
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-09-25 18:48:58 $
*/

public interface ParameterMeta extends MetaObject{
	public void finalize() throws Throwable;

    public int getParameterPosition();
    public void setParameterPosition(int newParamPosition);

    public String getParameterName();
    public void setParameterName(String newParamName);

    public String getParameterType();
    public void setParameterType(String newParamType);

    public boolean isInput();
    public void setIsInput(boolean input);

    public FunctionMeta getFunctionMeta();
//    public String getParameterUUID();
//
//    public void setParameterUUID(String newParamUUID);

}
 /**
  * HISTORY      : $Log: not supported by cvs2svn $
 */
