/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVSegmentImpl.java,v 1.4 2007-07-10 18:14:16 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.csv.data.impl;

import gov.nih.nci.caadapter.common.DataObjectImpl;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segment;

import java.util.ArrayList;

import gov.nih.nci.caadapter.hl7.datatype.Cardinality;

/**
 * Implementation of a segment that is contained within segmented csv data file.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: umkis $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date        $Date: 2007-07-10 18:14:16 $
 */

public class CSVSegmentImpl extends DataObjectImpl implements CSVSegment{
    private static final String LOGID = "$RCSfile: CSVSegmentImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVSegmentImpl.java,v 1.4 2007-07-10 18:14:16 umkis Exp $";

    public ArrayList<CSVField> fields = new ArrayList<CSVField>();
    public ArrayList<CSVSegment> childSegments = new ArrayList<CSVSegment>();
    public CSVSegment parentSegment = null;
    public CardinalityType cardinality = (new C_segment()).getCardinality();
    // constructors
    public CSVSegmentImpl(CSVSegmentMeta metaObject) {
        super(metaObject);
    }

    // getters and setters
    public ArrayList<CSVField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<CSVField> fields) {
        this.fields = fields;
    }

    public ArrayList<CSVSegment> getChildSegments() {
        return childSegments;
    }

    public void setChildSegments(ArrayList<CSVSegment> childSegments) {
        this.childSegments = childSegments;
    }

    public CSVSegment getParentSegment() {
        return parentSegment;
    }

    public void setParentSegment(CSVSegment parentSegment) {
        this.parentSegment = parentSegment;
    }

    // convenience method.
    public void addChildSegment(CSVSegment segment){
        this.childSegments.add(segment);
    }

    public String toString()
    {
        return getName();
    }

    public CardinalityType getCardinalityType()
    {
        return cardinality;
    }
    public void setCardinalityType(CardinalityType type)
    {
        cardinality = type;
    }
    public String getCardinalityWithString()
    {
        return cardinality.toString().substring(0, (new C_segment()).getCardinality().toString().length());
    }
    public boolean isChoiceSegment()
    {
        return cardinality.toString().endsWith(Config.SUFFIX_OF_CHOICE_CARDINALITY);
    }
    public void setCardinalityWithString(String type) throws IllegalArgumentException
    {
        cardinality = CardinalityType.valueOf(type);
    }
    public int getMaxCardinality()
    {
        Cardinality _cardinality = null;
        try
        {
            _cardinality = new Cardinality(getCardinalityWithString());
        }
        catch(IllegalArgumentException ie) {}
        return _cardinality.getMaximum();
    }
    public int getMinCardinality()
    {
        Cardinality _cardinality = null;
        try
        {
            _cardinality = new Cardinality(getCardinalityWithString());
        }
        catch(IllegalArgumentException ie) {}
        return _cardinality.getMinimum();
    }
}

/*
    public ArrayList<CSVField> fields = new ArrayList<CSVField>();
    public ArrayList<CSVSegment> childSegments = new ArrayList<CSVSegment>();
    public CSVSegment parentSegment = null;
    private String cardinality;
    // constructors
    public CSVSegmentImpl(CSVSegmentMeta metaObject) {
        super(metaObject);
    }

    // getters and setters
    public ArrayList<CSVField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<CSVField> fields) {
        this.fields = fields;
    }

    public ArrayList<CSVSegment> getChildSegments() {
        return childSegments;
    }

    public void setChildSegments(ArrayList<CSVSegment> childSegments) {
        this.childSegments = childSegments;
    }

    public CSVSegment getParentSegment() {
        return parentSegment;
    }

    public void setParentSegment(CSVSegment parentSegment) {
        this.parentSegment = parentSegment;
    }
    
    // convenience method.
    public void addChildSegment(CSVSegment segment){
        this.childSegments.add(segment);
    }

    public String toString()
    {
        return getName();
    }

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}

}
*/