/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.actions;


import gov.nih.nci.cbiit.cmps.core.Mapping;
import gov.nih.nci.cbiit.cmps.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmps.transform.XQueryBuilder;
import gov.nih.nci.cbiit.cmps.transform.XQueryTransformer;
import gov.nih.nci.cbiit.cmps.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmps.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmps.ui.main.MainFrame;
import gov.nih.nci.cbiit.cmps.ui.message.MessagePanel;
import gov.nih.nci.cbiit.cmps.ui.message.OpenMessageWizard;
import gov.nih.nci.cbiit.cmps.util.FileUtil;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileWriter;

/**
 * This class defines the new message transformation action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.5 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public class NewMessageAction extends AbstractContextAction
		{
	private static final String COMMAND_NAME = ActionConstants.NEW_MESSAGE_FILE;
	private static final Character COMMAND_MNEMONIC = new Character('M');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK, false);

	private MainFrame mainFrame;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NewMessageAction(MainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
		//mainContextManager = cm;
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public NewMessageAction(String name, MainFrame mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public NewMessageAction(String name, Icon icon, MainFrame mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		//do not know how to set the icon location name, or just do not matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		OpenMessageWizard w = new OpenMessageWizard(mainFrame, COMMAND_NAME, true);
		DefaultSettings.centerWindow(w);
		w.setVisible(true);
		if(w.isOkButtonClicked()){
			MessagePanel newMsgPane=new MessagePanel();
			
			mainFrame.addNewTab(newMsgPane);
			Mapping map = MappingFactory.loadMapping(w.getMapFile());
			XQueryBuilder builder = new XQueryBuilder(map);
			String queryString = builder.getXQuery();
			System.out.println("$$$$$$ query: \n"+queryString);
			XQueryTransformer tester= new XQueryTransformer();
			String srcFile = FileUtil.getRelativePath(w.getDataFile());
			tester.setFilename(srcFile);
			tester.setQuery(queryString);
			String xmlResult=tester.executeQuery();
			newMsgPane.setMessageText(xmlResult);
			FileWriter writer = new FileWriter(w.getDestFile());
			writer.write(xmlResult);
			writer.close();
			
		}
		setSuccessfullyPerformed(true);
		JOptionPane.showMessageDialog(mainFrame, "Transformation has completed successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
		
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return mainFrame;
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2009/11/10 19:14:11  wangeug
 * HISTORY      : setup message panel
 * HISTORY      :
 * HISTORY      :
 */