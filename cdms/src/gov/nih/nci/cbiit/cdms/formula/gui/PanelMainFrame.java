package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.common.util.DefaultSettings;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.gui.constants.ActionConstants;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.CellRenderFormula;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.TreeMouseAdapter;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.TreeNodeFormulaStore;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.bind.JAXBException;


public class PanelMainFrame extends JPanel {

	private JTree localTree=null;
	private File localStoreFile=null;
	private SplitPaneFormula rightSplit;
	//private SplitCentralPane centralSplit;
    private FormulaMainPanel centralSplit;

    private Frame parentFrame;
//    public PanelMainFrame()
//	{
//		super();
//		initUI();
//	}
    public PanelMainFrame(Frame frame)
	{
		super();
        parentFrame = frame;
        initUI();
	}

//    public SplitCentralPane getCentralSplit() {
//		return centralSplit;
//	}
    public FormulaMainPanel getCentralSplit() {
		return centralSplit;
	}

    /**
	 * Save local formula store into a local file
	 * @param sameStore If save the local store to its original file
	 */
	public void saveLocalFormulaStore(boolean sameStore)
	{
		if (!sameStore||localStoreFile==null)
		{
	        File file = DefaultSettings.getUserInputOfFileFromGUI(this,
	                ActionConstants.FORMULA_FILE_EXTENSION, "Save Formula Store", true, false);
	        if (file != null)
	        {
	        	localStoreFile=file;
	        }
		}
    	TreeNodeFormulaStore localTreeNode=(TreeNodeFormulaStore)localTree.getModel().getRoot();
    	FormulaStore localStore=(FormulaStore)localTreeNode.getUserObject();
    	try {
			FormulaFactory.saveFormulaStore(localStore, localStoreFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Open a local file as local formula store
	 * @param file
	 */
	public void openLocalFormulaStore(File file)
	{
		try {
			FormulaStore newStore=FormulaFactory.loadFormulaStore(file);
			if (newStore!=null)
			{
				FormulaFactory.updateLocalStore(newStore);
				localStoreFile=file;
			}

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TreeNodeFormulaStore newStoreNode= new TreeNodeFormulaStore(FormulaFactory.getLocalStore());

		DefaultTreeModel treeModel=(DefaultTreeModel)localTree.getModel();
		treeModel.setRoot(newStoreNode);
		localTree.setSelectionRow(0);
	}
	private void initUI()
	{
		this.setLayout(new BorderLayout());
		add(createLeftJSplitPane(),  BorderLayout.WEST);
		JSplitPane centerRightSplit =new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerRightSplit.add(centralSplit);
		centerRightSplit.add(rightSplit);
		add(centerRightSplit, BorderLayout.CENTER);
 	}

	private JSplitPane createLeftJSplitPane()
	{
		rightSplit=new SplitPaneFormula();
		//centralSplit=new SplitCentralPane();
        centralSplit=createCenterJSplitPane();

        JSplitPane leftSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		TreeNodeFormulaStore localStoreNode= new TreeNodeFormulaStore(FormulaFactory.getLocalStore());
		localTree = new JTree(localStoreNode);
		localTree.setCellRenderer(new CellRenderFormula());
		localTree.addMouseListener(new TreeMouseAdapter());
		localTree.addTreeSelectionListener(rightSplit);
		localTree.addTreeSelectionListener(centralSplit);
		JScrollPane localScroll =new JScrollPane(localTree);
		localScroll.setPreferredSize(new Dimension(150,350));
		leftSplit.add(localScroll);

		TreeNodeFormulaStore commonStoreNode= new TreeNodeFormulaStore(FormulaFactory.getCommonStore());
		JTree commonStoreTree = new JTree(commonStoreNode);
		commonStoreTree.setCellRenderer(new CellRenderFormula());
		commonStoreTree.addMouseListener(new TreeMouseAdapter());
		commonStoreTree.addTreeSelectionListener(rightSplit);
		commonStoreTree.addTreeSelectionListener(centralSplit);
		JScrollPane commonScroll =new JScrollPane(commonStoreTree);
		commonScroll.setPreferredSize(new Dimension(150,150));
		leftSplit.add( commonScroll);

		return leftSplit;
	}

	private FormulaMainPanel createCenterJSplitPane()
	{
        return new FormulaMainPanel(parentFrame);
//		JSplitPane centerSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		centerSplit.add(new JButton("Uper"));
//		centerSplit.add(new JButton("Down"));
//		return centerSplit;
	}

}