/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.csv.meta;

import gov.nih.nci.caadapter.common.MetaObject;

/**
 * Interface for a field metadata (contained within csv segment).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-09-24 20:42:38 $
 */

public interface CSVFieldMeta extends MetaObject{
    public int getColumn();
    public void setColumn(int column);
    public CSVSegmentMeta getSegment();
    public void setSegment(CSVSegmentMeta newSegmentMeta);
    // convenience.
    public String getSegmentName();
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
