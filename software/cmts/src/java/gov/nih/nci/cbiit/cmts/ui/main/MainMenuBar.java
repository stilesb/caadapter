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

package gov.nih.nci.cbiit.cmts.ui.main;

import gov.nih.nci.cbiit.cmts.ui.actions.AboutViewAction;
import gov.nih.nci.cbiit.cmts.ui.actions.AbstractContextAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultCloseAllAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultCloseAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultSaveAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultSaveAsAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultExitAction;
import gov.nih.nci.cbiit.cmts.ui.actions.HelpViewAction;
import gov.nih.nci.cbiit.cmts.ui.actions.NewArtifactAction;
import gov.nih.nci.cbiit.cmts.ui.actions.NewMapFileAction;
import gov.nih.nci.cbiit.cmts.ui.actions.NewTransformationAction;
import gov.nih.nci.cbiit.cmts.ui.actions.OpenMapFileAction;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.MenuConstants;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

import javax.swing.*;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.net.URL;
import java.net.MalformedURLException;

import netscape.javascript.JSObject;
import netscape.javascript.JSException;

/**
 * This class manages the definitions and instantiations of menu items. It will
 * coordinate ContextManager class to deal with context sensitive menu
 * switches.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2009-11-23 18:30:56 $
 */
@SuppressWarnings("serial")
public class MainMenuBar extends JMenuBar
{

    //ContextManager contextManager = null;
    MainFrameContainer mainFrame = null;
    private Map<String, AbstractContextAction> actionMap;
    private Map<String, JMenuItem> menuItemMap;
    private Map<String, JMenu> menuMap;

    private JMenuItem exitMenuItem = null;

    public MainMenuBar(MainFrameContainer mf)//ContextManager contextManager) {
    {//this.contextManager = contextManager;
        this.mainFrame = mf;//contextManager.getMainFrame();
        initialize();
    }

    private void initialize()
    {
        actionMap = Collections.synchronizedMap(new HashMap<String, AbstractContextAction>());
        menuItemMap = Collections.synchronizedMap(new HashMap<String, JMenuItem>());
        menuMap = Collections.synchronizedMap(new HashMap<String, JMenu>());

        add(constructFileMenu());
        JMenu helpMenu=new JMenu("Help");
        JMenuItem aboutItem=new JMenuItem(new AboutViewAction(mainFrame));
        helpMenu.add(aboutItem);

        JMenuItem helpItem=new JMenuItem(new HelpViewAction(mainFrame));
        helpMenu.add(helpItem);
        add(helpMenu);
    }


    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#enableAction(java.lang.String, boolean)
      */
    public void enableAction(String actionConstant, boolean value)
    {
        Action action = getDefinedAction(actionConstant);// (Action)actionMap.get(actionConstant);
        if (action != null)
        {
            action.setEnabled(value);
        } else
        {
            String msg = "Action could not be found for '" + actionConstant + "'.";
            System.err.println(msg);
            //Log.logWarning(this.getClass(), msg);
        }
    }

    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#getDefinedAction(java.lang.String)
      */
    public Action getDefinedAction(String actionConstant)
    {
        return (Action) actionMap.get(actionConstant);
    }

    public JMenuItem getDefinedMenuItem(String actionConstant)
    {
        return (JMenuItem) menuItemMap.get(actionConstant);
    }

    public JMenu getDefinedMenu(String actionConstant)
    {
        return (JMenu) menuMap.get(actionConstant);
    }

    private JMenu constructFileMenu()
    {
        DefaultSaveAction defaultSaveAction = new DefaultSaveAction(mainFrame);
        JMenuItem saveMenuItem = new JMenuItem(defaultSaveAction);
        actionMap.put(ActionConstants.SAVE, defaultSaveAction);
        menuItemMap.put(ActionConstants.SAVE, saveMenuItem);
        DefaultSaveAsAction defaultSaveAsAction = new DefaultSaveAsAction(mainFrame);
        //ImageIcon saveAsImageIcon = new ImageIcon(DefaultSettings.getImage("fileSaveAs.gif"));
        ImageIcon saveAsImageIcon = new ImageIcon(DefaultSettings.getImage("ico_saveAs.bmp"));
        defaultSaveAsAction.setIcon(saveAsImageIcon);
        JMenuItem saveAsMenuItem = new JMenuItem(defaultSaveAsAction);

        //saveAsMenuItem.setIcon(saveAsImageIcon);
        actionMap.put(ActionConstants.SAVE_AS, defaultSaveAsAction);
        menuItemMap.put(ActionConstants.SAVE_AS, saveAsMenuItem);
        DefaultCloseAction defaultCloseAction = new DefaultCloseAction(mainFrame);
        JMenuItem closeMenuItem = new JMenuItem(defaultCloseAction);
        actionMap.put(ActionConstants.CLOSE, defaultCloseAction);
        menuItemMap.put(ActionConstants.CLOSE, closeMenuItem);
        DefaultCloseAllAction closeAllAction = new DefaultCloseAllAction(mainFrame);
        JMenuItem closeAllMenuItem = new JMenuItem(closeAllAction);
        actionMap.put(ActionConstants.CLOSE_ALL, closeAllAction);
        menuItemMap.put(ActionConstants.CLOSE_ALL, closeAllMenuItem);

        OpenMapFileAction openMapAction = new OpenMapFileAction(mainFrame);
        JMenuItem openMapFileItem = new JMenuItem(openMapAction);
        actionMap.put(ActionConstants.OPEN_MAP_FILE, openMapAction);
        menuItemMap.put(ActionConstants.OPEN_MAP_FILE, openMapFileItem);


        if (isEnableExitMenu())
        {
            DefaultExitAction exitAction = new DefaultExitAction(mainFrame);//.getAssociatedUIComponent());
            exitMenuItem = new JMenuItem(exitAction);
            actionMap.put(ActionConstants.EXIT, exitAction);
            menuItemMap.put(ActionConstants.EXIT, exitMenuItem);


        }
        // link them together
        JMenu fileMenu = new JMenu(MenuConstants.FILE_MENU_NAME);
        fileMenu.setMnemonic('F');



        JMenu newMenu = constructNewCmtsMenu();
        //ImageIcon newImageIcon = new ImageIcon(DefaultSettings.getImage("_Add_tables.gif"));
        ImageIcon newImageIcon = new ImageIcon(DefaultSettings.getImage("ico_new.bmp"));
        newMenu.setIcon(newImageIcon);
        fileMenu.add(newMenu);
        fileMenu.addSeparator();
        //fileMenu.add(constructOpenMenu());
        fileMenu.add(openMapFileItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(closeMenuItem);
        fileMenu.add(closeAllMenuItem);
        if (exitMenuItem != null)
        {
            fileMenu.addSeparator();
            fileMenu.add(exitMenuItem);
        }
        menuMap.put(MenuConstants.FILE_MENU_NAME, fileMenu);
        defaultSaveAction.setEnabled(false);
        defaultSaveAsAction.setEnabled(false);
        defaultCloseAction.setEnabled(false);
        closeAllAction.setEnabled(false);

        //fileMenu.addActionListener(
        fileMenu.addMenuListener(
           new MenuListener()
           {
               public void menuCanceled(MenuEvent e) {}
               public void menuDeselected(MenuEvent e) {}
               public void menuSelected(MenuEvent e)
               {
                   URL u = FileUtil.getCodeBase();
                       if (u != null) {} //System.out.println("C - Already exist - Applet documentBase=" + u.toString());
                       else
                       {
                           try
                           {
                            URL u1 = mainFrame.getMainApplet().getDocumentBase();
                            System.out.println("Applet documentBase=" + u1.toString());
                            FileUtil.setCodeBase(u1);
                           }
                           catch(Exception ee)
                           {
                                URL url = null;
                                String urlStr = "http://caadapter.nci.nih.gov/caadapter-cmts/";
                                try
                                {
                                    url = new URL(urlStr);
                                }
                                catch(MalformedURLException me)
                                {
                                    url = null;
                                }
                                if (url != null) FileUtil.setCodeBase(url);
                           }
                       }
                   if (mainFrame.getMainApplet() != null)
                   {




                       JMenuItem exitMenu;
                       try
                       {
                           exitMenu = mainFrame.getMainApplet().getMainMenuBar().getExitMenuItem();
                       }
                       catch(Exception ee)
                       {
                           return;
                       }
                       if (!exitMenu.isEnabled()) return;
                       String webBrowser = null;
                        try
                        {
                            //System.out.println("aa1");
                            JSObject win = (JSObject) JSObject.getWindow(mainFrame.getMainApplet());
                            //System.out.println("aa2");
                            webBrowser = (String) win.eval("whichBrs()");
                            //System.out.println("aa3 : webBrowser=" + webBrowser);
                        }
                        catch(JSException je)
                        {
                            System.out.println("Failure : JSObject.getWindow() : " + je.getMessage());
                            webBrowser = null;
                        }

                        if ((webBrowser != null)&&(!webBrowser.trim().equals("")))
                        {
                            if (!webBrowser.equalsIgnoreCase("Internet Explorer"))
                            {
                                exitMenu.setToolTipText("On " + webBrowser + " browser, this menu doesn't work.\nPlease, use web browser's 'Exit' menu.");
                                exitMenu.setEnabled(false);
                            }
                        }
                   }
               }
           }
        );

        return fileMenu;
    }



    private JMenu constructOpenMenu()
    {
        // construct actions and menu items.
        OpenMapFileAction openMapAction = new OpenMapFileAction(mainFrame);

        JMenuItem openMapFileItem = new JMenuItem(openMapAction);
        actionMap.put(ActionConstants.OPEN_MAP_FILE, openMapAction);
        menuItemMap.put(ActionConstants.OPEN_MAP_FILE, openMapFileItem);

        // link them together
        JMenu openMenu = new JMenu("      " + MenuConstants.OPEN_MENU_NAME);
        openMenu.setMnemonic('O');
        openMenu.add(openMapFileItem);

        return openMenu;
    }

    private JMenu constructNewCmtsMenu()
    {
        //user should be authorized to use HL7 artifacts
        JMenu newGroup = new JMenu("New");
        newGroup.setMnemonic('N');

        NewMapFileAction newMapAction = new NewMapFileAction(mainFrame);
        JMenuItem newCmpsMapItem = new JMenuItem(newMapAction);
        actionMap.put(ActionConstants.NEW_MAP_FILE, newMapAction);
        menuItemMap.put(ActionConstants.NEW_MAP_FILE, newCmpsMapItem);
        newGroup.add(newCmpsMapItem);

        NewTransformationAction newXmlMessage = new NewTransformationAction(ActionConstants.NEW_XML_Transformation, mainFrame);
        JMenuItem newXmlTransformationItem = new JMenuItem(newXmlMessage);
        actionMap.put(ActionConstants.NEW_XML_Transformation, newXmlMessage);
        menuItemMap.put(ActionConstants.NEW_XML_Transformation, newXmlTransformationItem);
        newGroup.add(newXmlTransformationItem);
        newGroup.addSeparator();
        NewArtifactAction newQueryAction = new NewArtifactAction(ActionConstants.NEW_XQUERY_STATEMENT, mainFrame);
        JMenuItem newXQeuryItem = new JMenuItem(newQueryAction);
        actionMap.put(ActionConstants.NEW_XQUERY_STATEMENT, newQueryAction);
        menuItemMap.put(ActionConstants.NEW_XQUERY_STATEMENT, newXQeuryItem);
        newGroup.add(newXQeuryItem);

        NewArtifactAction newXSLTAction = new NewArtifactAction(ActionConstants.NEW_XSLT_STYLESHEET, mainFrame);
        JMenuItem newXSLTItem = new JMenuItem(newXSLTAction);
        actionMap.put(ActionConstants.NEW_XSLT_STYLESHEET, newXSLTAction);
        menuItemMap.put(ActionConstants.NEW_XSLT_STYLESHEET, newXSLTItem);
        newGroup.add(newXSLTItem);

/**
        NewTransformationAction newCsvMessage = new NewTransformationAction(ActionConstants.NEW_CSV_Transformation, mainFrame);
        JMenuItem newCsvTransformationItem = new JMenuItem(newCsvMessage);
        actionMap.put(ActionConstants.NEW_CSV_Transformation, newCsvMessage);
        menuItemMap.put(ActionConstants.NEW_CSV_Transformation, newCsvTransformationItem);
        newGroup.add(newCsvTransformationItem);

        NewTransformationAction newHl7v2Message = new NewTransformationAction(ActionConstants.NEW_HL7_V2_Transformation, mainFrame);
        JMenuItem newHl7v2TransformationItem = new JMenuItem(newHl7v2Message);
        actionMap.put(ActionConstants.NEW_HL7_V2_Transformation, newHl7v2Message);
        menuItemMap.put(ActionConstants.NEW_HL7_V2_Transformation, newHl7v2TransformationItem);
        newGroup.add(newHl7v2TransformationItem);

        newGroup.addSeparator();
        NewTransformationAction xmlToCdaMessage = new NewTransformationAction(ActionConstants.NEW_XML_CDA_Transformation, mainFrame);
        JMenuItem newXmlToCdaTransformationItem = new JMenuItem(xmlToCdaMessage);
        actionMap.put(ActionConstants.NEW_XML_CDA_Transformation, xmlToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_XML_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newXmlToCdaTransformationItem);

        NewTransformationAction csvToCdaMessage = new NewTransformationAction(ActionConstants.NEW_CSV_CDA_Transformation , mainFrame);
        JMenuItem newCsvToCdaTransformationItem = new JMenuItem(csvToCdaMessage);
        actionMap.put(ActionConstants.NEW_CSV_CDA_Transformation, csvToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_CSV_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newCsvToCdaTransformationItem);

        NewTransformationAction hl7v2ToCdaMessage = new NewTransformationAction(ActionConstants.NEW_HL7_V2_CDA_Transformation, mainFrame);
        JMenuItem newHl77v2ToCdaTransformationItem = new JMenuItem(hl7v2ToCdaMessage);
        actionMap.put(ActionConstants.NEW_HL7_V2_CDA_Transformation, hl7v2ToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_HL7_V2_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newHl77v2ToCdaTransformationItem);
   **/
        return newGroup;
    }


    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#resetMenus(boolean)
      */
    public void resetMenus(boolean hasActiveDocument)
    {// provide structure for
        // more menus to be
        // reset
        resetFileMenu(hasActiveDocument);
//		resetReportMenu(hasActiveDocument);
    }

    private void resetFileMenu(boolean hasActiveDocument)
    {
        resetNewSubMenu(hasActiveDocument);
        resetOpenSubMenu(hasActiveDocument);
        JMenuItem saveMenuItem = menuItemMap.get(ActionConstants.SAVE);
        JMenuItem saveAsMenuItem = menuItemMap.get(ActionConstants.SAVE_AS);
        //ImageIcon saveAsImageIcon = new ImageIcon(DefaultSettings.getImage("fileSaveAs.gif"));
        //saveAsMenuItem.setIcon(saveAsImageIcon);
//		JMenuItem validateMenuItem = menuItemMap.get(ActionConstants.VALIDATE);
		JMenuItem closeMenuItem = menuItemMap.get(ActionConstants.CLOSE);
		JMenuItem closeAllMenuItem = menuItemMap.get(ActionConstants.CLOSE_ALL);
//		JMenuItem anotateMenuItem = menuItemMap.get(ActionConstants.ANOTATE);
        //		saveMenuItem.setAction(null);
        //		saveMenuItem.setAction(defaultSaveAction);
        //		actionMap.put(ActionConstants.SAVE, defaultSaveAction);
        //		saveAsMenuItem.setAction(null);
        //		saveAsMenuItem.setAction(defaultSaveAsAction);
        //		actionMap.put(ActionConstants.SAVE_AS, defaultSaveAsAction);
        //		anotateMenuItem.setAction(null);
        //		anotateMenuItem.setAction(defaultAnotateAction);
        //		actionMap.put(ActionConstants.ANOTATE, defaultAnotateAction);
        //		validateMenuItem.setAction(null);
        //		validateMenuItem.setAction(defaultValidateAction);
        //		actionMap.put(ActionConstants.VALIDATE, defaultValidateAction);
        //		closeMenuItem.setAction(null);
        //		closeMenuItem.setAction(defaultCloseAction);
        //		actionMap.put(ActionConstants.CLOSE, defaultCloseAction);
//        saveMenuItem.getAction().setEnabled(false);
//        saveAsMenuItem.getAction().setEnabled(false);
//		anotateMenuItem.getAction().setEnabled(false);
//		validateMenuItem.getAction().setEnabled(false);
//		closeMenuItem.getAction().setEnabled(false);

        if (hasActiveDocument)
        {
            saveMenuItem.getAction().setEnabled(true);
            saveAsMenuItem.getAction().setEnabled(true);
            closeMenuItem.getAction().setEnabled(true);
            closeAllMenuItem.getAction().setEnabled(true);
        }
        else
        {
            boolean cTag = false;
            try
            {
                cTag = ((mainFrame.getAllTabs() != null)&&(mainFrame.getAllTabs().size() > 0));
            }
            catch(ArrayIndexOutOfBoundsException ae)
            {
                cTag = false;
            }

            if (cTag)
            {
                saveMenuItem.getAction().setEnabled(true);
                saveAsMenuItem.getAction().setEnabled(true);
                closeMenuItem.getAction().setEnabled(true);
                closeAllMenuItem.getAction().setEnabled(true);
            }
            else
            {
                saveMenuItem.getAction().setEnabled(false);
                saveAsMenuItem.getAction().setEnabled(false);
                closeMenuItem.getAction().setEnabled(false);
                closeAllMenuItem.getAction().setEnabled(false);
            }
        }

        
    }

    private void resetNewSubMenu(boolean hasActiveDocument)
    {
        if (!hasActiveDocument)
        {
            //			menuItemMap.get(ActionConstants.NEW_MAP_FILE).getAction().setEnabled(true);
            resetMenuItem(ActionConstants.NEW_MAP_FILE, true);
            //			newMapFileItem.getAction().setEnabled(true);
            //			newCSVSpecificationItem.getAction().setEnabled(true);
            //			menuItemMap.get(ActionConstants.NEW_CSV_SPEC).getAction().setEnabled(true);
        }
    }

    private void resetMenuItem(String itemName, boolean newValue)
    {
        JMenuItem menuItem = menuItemMap.get(itemName);
        if (menuItem != null)
        {
            Action a = menuItem.getAction();
            if (a != null)
                a.setEnabled(newValue);
        }
    }

    private void resetOpenSubMenu(boolean hasActiveDocument)
    {
        if (!hasActiveDocument)
        {
            resetMenuItem(ActionConstants.OPEN_MAP_FILE, true);
        }
    }

//	private void resetReportMenu(boolean hasActiveDocument)
//	{
//		if (!hasActiveDocument)
//		{
//			resetMenuItem(ActionConstants.GENERATE_REPORT, false);
//		}
//	}

    public void enableCloseAllAction(boolean newValue)
    {
        Action closeAllAction = actionMap.get(ActionConstants.CLOSE_ALL);
        if (closeAllAction != null)
        {
            closeAllAction.setEnabled(newValue);
            JMenuItem closeAllMenuItem = menuItemMap.get(ActionConstants.CLOSE_ALL);
            closeAllMenuItem.setAction(null);
            closeAllMenuItem.setAction(closeAllAction);
            // closeAllMenuItem.invalidate();
        }
    }
    public JMenuItem getExitMenuItem()
    {
        return exitMenuItem;
    }
    private boolean isEnableExitMenu()
    {
        if (mainFrame.getMainFrame() != null)
        {
            //System.out.println("Run on the MainFrame");
            return true;
        }
        if (mainFrame.getMainApplet() != null)
        {
            try
            {
                JSObject jsOb = (JSObject) JSObject.getWindow(mainFrame.getMainApplet());
                //System.out.println("Run on a Web Browser : " + jsOb.getClass().getCanonicalName());
                return true;
            }
            catch(JSException je)
            {
                Container com = mainFrame.getMainApplet();
                while(com != null)
                {
                    //System.out.println("  This Container: " + com.getClass().getCanonicalName());
                    if (com instanceof Window)
                    {
                        //System.out.println("Run on a AppletViewer or another Applet applecation: " + com.getClass().getCanonicalName());
                        return true;
                    }
                    com = com.getParent();
                }
            }
        }
        return true;
    }
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.2  2008/12/09 19:04:17  linc
 * HISTORY : First GUI release
 * HISTORY :
 * HISTORY : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY : UI update.
 * HISTORY :
 */
