package gov.nih.nci.cbiit.cdms.formula.core;


import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.cbiit.cdms.formula.gui.properties.PropertiesResult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "formula", propOrder = {
	"annotation",
	"expression"
})
public class FormulaMeta extends BaseMeta {
	private TermMeta expression;
	private String annotation;
    @XmlAttribute
	private FormulaType type;
    @XmlAttribute
	private FormulaStatus status;
 
	public TermMeta getExpression() {
		return expression;
	}

	public void setExpression(TermMeta formula)
	{
		expression = formula;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public FormulaStatus getStatus() {
		return status;
	}

	public void setStatus(FormulaStatus status) {
		this.status = status;
	}

	public FormulaType getType() {
		return type;
	}

	public void setType(FormulaType type) {
		this.type = type;
	}

	@Override
	public String formatJavaStatement() {
		// TODO Auto-generated method stub
		return getExpression().formatJavaStatement();
	}

	public String toString()
	{
		StringBuffer rtnBf=new StringBuffer();
		rtnBf.append(this.getName() + " = ");
		rtnBf.append(this.getExpression().toString());
		return rtnBf.toString();
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Formula Properties";
	}

	@Override
	public PropertiesResult getPropertyDescriptors() throws Exception {
		Class<?> beanClass = this.getClass();

		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add( new PropertyDescriptor("Group", beanClass, "getType", null));
		propList.add( new PropertyDescriptor("Status", beanClass, "getStatus", null));
		propList.add( new PropertyDescriptor("Annotation", beanClass, "getAnnotation", null));
		//retrieve the expression term for "description"
		propList.add( new PropertyDescriptor("Description", beanClass, "getExpression", null));
		PropertiesResult result =super.getPropertyDescriptors();

		result.addPropertyDescriptors(this, propList);
		return result;
	}
}