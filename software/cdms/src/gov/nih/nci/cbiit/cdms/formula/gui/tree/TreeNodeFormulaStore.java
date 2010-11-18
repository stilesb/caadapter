package gov.nih.nci.cbiit.cdms.formula.gui.tree;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeFormulaStore  extends DefaultMutableTreeNode {

	public TreeNodeFormulaStore (FormulaStore store)
	{
		super(store);
		if (store.getFormula()!=null)
		{
			for (FormulaMeta formula:store.getFormula())
				this.add(new DefaultMutableTreeNode(formula));
		}
	}
}