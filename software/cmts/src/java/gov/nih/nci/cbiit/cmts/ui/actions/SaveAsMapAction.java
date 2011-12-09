/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.AbstractTabPanel;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines a concrete "Save As" action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.4 $
 * @date       $Date: 2009-11-03 18:30:56 $
 */
public class SaveAsMapAction extends DefaultSaveAsAction
{

	protected AbstractTabPanel viewerPanel;
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveAsMapAction(AbstractTabPanel mappingPanel)
	{
		this(COMMAND_NAME,null, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveAsMapAction(String name, Icon icon, AbstractTabPanel tabPanel)
	{
		super(name, icon, null);
		viewerPanel = tabPanel;
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		if(viewerPanel!=null)
		{
			if (viewerPanel instanceof MappingMainPanel)
			{
				MappingMainPanel mappingMain=(MappingMainPanel)viewerPanel;
				if(mappingMain.getSourceTree()==null || mappingMain.getTargetTree()==null)
				{
					String msg = "Enter both source and target information before saving the map specification.";
					JOptionPane.showMessageDialog(viewerPanel, msg, "Error", JOptionPane.ERROR_MESSAGE);
					setSuccessfullyPerformed(false);
					return false;
				}
			}
		}

        String extension = viewerPanel.getViewFileExtension();
        if (extension.equals(".xq")) extension = ".xql";
        File file = DefaultSettings.getUserInputOfFileFromGUI(viewerPanel, extension, "Save As...", true, true);

        if (file != null)
			setSuccessfullyPerformed(processSaveFile(file));
		
		return isSuccessfullyPerformed();
	}

	@SuppressWarnings("unchecked")
	protected boolean processSaveFile(File file) throws Exception
	{
		boolean oldChangeValue = viewerPanel.isChanged();
		preActionPerformed(viewerPanel);
		viewerPanel.persistFile(file);
		try
		{
			if (!GeneralUtilities.areEqual(defaultFile, file))
			{//not equal, change it.
				removeFileUsageListener(defaultFile, viewerPanel);
				defaultFile = file;
			}
			postActionPerformed(viewerPanel);
//			JOptionPane.showMessageDialog(viewerPanel.getParent(), "Mapping data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
			viewerPanel.setSaveFile(file);
			return true;
		}
		catch(Throwable e)
		{
			//restore the change value since something occurred and believe the save process is aborted.
			viewerPanel.setChanged(oldChangeValue);
			//rethrow the exeception
			e.printStackTrace();
			throw new Exception(e);
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2009/10/27 19:25:33  wangeug
 * HISTORY      : clean codes
 * HISTORY      :
 * HISTORY      : Revision 1.2  2009/10/27 18:22:05  wangeug
 * HISTORY      : clean codes
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/09 19:04:17  linc
 * HISTORY      : First GUI release
 * HISTORY      :
 */
