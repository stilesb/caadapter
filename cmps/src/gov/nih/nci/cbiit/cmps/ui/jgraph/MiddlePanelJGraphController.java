/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.jgraph;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import gov.nih.nci.cbiit.cmps.core.Component;
import gov.nih.nci.cbiit.cmps.core.FunctionData;
import gov.nih.nci.cbiit.cmps.core.FunctionDef;
import gov.nih.nci.cbiit.cmps.core.LinkType;
import gov.nih.nci.cbiit.cmps.core.LinkpointType;
import gov.nih.nci.cbiit.cmps.core.Mapping;
import gov.nih.nci.cbiit.cmps.core.ViewType;
import gov.nih.nci.cbiit.cmps.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmps.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmps.ui.common.UIHelper;
import gov.nih.nci.cbiit.cmps.ui.function.FunctionBoxCell;
import gov.nih.nci.cbiit.cmps.ui.function.FunctionBoxDefaultPort;
import gov.nih.nci.cbiit.cmps.ui.function.FunctionBoxDefaultPortView;
import gov.nih.nci.cbiit.cmps.ui.function.FunctionBoxUserObject;
import gov.nih.nci.cbiit.cmps.ui.function.FunctionBoxView;
import gov.nih.nci.cbiit.cmps.ui.function.FunctionBoxViewManager;
import gov.nih.nci.cbiit.cmps.ui.mapping.CmpsMappingPanel;
import gov.nih.nci.cbiit.cmps.ui.mapping.ElementMetaLoader;
import gov.nih.nci.cbiit.cmps.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.cbiit.cmps.ui.properties.DefaultPropertiesSwitchController;
import gov.nih.nci.cbiit.cmps.ui.properties.PropertiesSwitchController;
import gov.nih.nci.cbiit.cmps.ui.tree.DefaultSourceTreeNode;
import gov.nih.nci.cbiit.cmps.ui.tree.DefaultTargetTreeNode;

import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

/**
 * This is the controller class of Middle Panel JGraph implementation. The MiddlePanelJGraphController class will deal with real implementation of some of
 * actions to modify (mainly CRUD) upon graph, and mainly focuses on drag-and-drop and handlings of repaint of graph, for example. MiddlePanelMarqueeHandler
 * will help handle key and mouse driven events such as display pop menus, etc.
 * 
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.8 $
 * @date       $Date: 2009-10-27 18:22:44 $
 *
 */
public class MiddlePanelJGraphController 
{
	private MiddlePanelJGraph graph = null;

	private MiddlePanelMarqueeHandler marqueeHandler = null;

	// the parent panels
	private MappingMiddlePanel middlePanel = null;

	private CmpsMappingPanel mappingPanel = null;
	private DefaultPropertiesSwitchController propertiesSwitchController;
	
	// a list of MappingViewCommonComponent
	private List<MappingViewCommonComponent> mappingViewList = null;

	private Mapping mappingData = null;

	private boolean isGraphChanged = false;

	//	private MappingPanelPropertiesSwitchController propertiesSwitchController;

	private Color graphBackgroundColor = new Color(222, 238, 255);

	//	private FunctionBoxViewUsageManager usageManager;

	private LinkSelectionHighlighter linkSelectionHighlighter;


	public LinkSelectionHighlighter getHighLighter(){
		return linkSelectionHighlighter;
	}
	// 
	// Construct the Graph using the Model as its Data Source
	public MiddlePanelJGraphController(MiddlePanelJGraph graph, MappingMiddlePanel middlePanel, CmpsMappingPanel mappingPanel) {
		this.graph = graph;
		// this.model = graph.getModel();
		this.middlePanel = middlePanel;
		this.mappingPanel = mappingPanel;
		initialization(false);
	}



	public void setJGraph(MiddlePanelJGraph newGraph)
	{
		this.graph = null;
		if ( linkSelectionHighlighter != null && this.graph != null ) {
			this.graph.removeGraphSelectionListener(linkSelectionHighlighter);
		}
		this.graph = newGraph;
		initialization(true);
	}

	private void initialization(boolean keepSourceTargetComponent)
	{
		// dropTarget = new DropTarget(graph, DnDConstants.ACTION_LINK, this);
		// Use a Custom Marquee Handler
		marqueeHandler = new MiddlePanelMarqueeHandler(graph, this);
		graph.setMarqueeHandler(marqueeHandler); 
		// Make Ports Visible by Default
		this.graph.setPortsVisible(true);
		// Use the Grid (but don't make it Visible)
		this.graph.setGridEnabled(true);
		// Set the Grid Size to 10 Pixel
		this.graph.setGridSize(6);
		// Set the Tolerance to 2 Pixel
		this.graph.setTolerance(2);
		// Accept edits if click on background
		this.graph.setInvokesStopCellEditing(true);
		// dose not allow control-drag
		this.graph.setCloneable(false);
		// // Jump to default port on connect
		// this.graph.setJumpToDefaultPort(true);
		// Container rootPane = csvPanel.getRootPane();
		// if (rootPane != null)
		// {
		// graph.setBackground(rootPane.getBackground());
		// graph.setForeground(rootPane.getBackground());
		// }
		// graph.setPortsVisible(false);
		// graph.setConnectable(true);
		// graph.setAntiAliased(false);
		graph.setSizeable(true);
		// // graph.setBendable(false);
		graph.setDragEnabled(true);
		graph.setDropEnabled(true);
		graph.setEditable(false);
		graph.setMoveable(true);
		graph.setBackground(graphBackgroundColor);
		//		graph.getSelectionModel().addGraphSelectionListener(getPropertiesSwitchController());
		// setMappingPairCellMap(Collections.synchronizedMap(new HashMap()));
		setMappingViewList(Collections.synchronizedList(new ArrayList<MappingViewCommonComponent>()));
		//		if ( this.mappingData != null && keepSourceTargetComponent ) {// just to clear graphs but not the source and target component if any.
		//			MappingImpl newMappingImpl = new MappingImpl();
		//			newMappingImpl.setSourceComponent(this.mappingData.getSourceComponent());
		//			newMappingImpl.setTargetComponent(this.mappingData.getTargetComponent());
		//			newMappingImpl.setMappingType(this.mappingData.getMappingType());
		//			setMappingData(newMappingImpl);
		//		} else {// initialize all
		//			setMappingData(new MappingImpl());
		//		}
		setGraphChanged(false);
		//		usageManager = null;
	}

	/**
	 * To register the highligher into graph and trees.
	 */
	private void registerLinkHighlighter()
	{
		// Register highlighter
		if ( linkSelectionHighlighter != null ) {
			if ( mappingPanel != null ) {
				JTree tree = mappingPanel.getSourceTree();
				if ( tree != null ) {
					tree.removeTreeSelectionListener(linkSelectionHighlighter);
				}
				tree = mappingPanel.getTargetTree();
				if ( tree != null ) {
					tree.removeTreeSelectionListener(linkSelectionHighlighter);
				}
			}
			this.graph.removeGraphSelectionListener(linkSelectionHighlighter);
		}
		linkSelectionHighlighter = new LinkSelectionHighlighter(mappingPanel, this.graph, middlePanel);
		// linkSelectionHighlighter = new LinkSelectionHighlighter(mappingPanel);
		// linkSelectionHighlighter.setGraph(this.graph);
		this.graph.addGraphSelectionListener(linkSelectionHighlighter);
		if ( mappingPanel != null ) {
			JTree tree = mappingPanel.getSourceTree();
			if ( tree != null ) {
				/**
				 * Register the selection listener to the tree level instead of selection model level gives the edge to know where the selection is originated
				 * in the linkSelectionHighlighter.
				 */
				tree.addTreeSelectionListener(linkSelectionHighlighter);
			}
			tree = mappingPanel.getTargetTree();
			if ( tree != null ) {
				tree.addTreeSelectionListener(linkSelectionHighlighter);
			}
		}
	}

	/**
	 * Explicitly set the value.
	 * 
	 * @param newValue
	 */
	public void setGraphChanged(boolean newValue)
	{
		isGraphChanged = newValue;
		if (isGraphChanged)
		{
			//update source and target tree
			mappingPanel.getTargetScrollPane().repaint();
			mappingPanel.getSourceScrollPane().repaint();
		}
	}

	public boolean isGraphChanged()
	{
		return isGraphChanged;
	}

	/**
	 * Return a more concrete implementation of original interface to provide graph selection listener interface.
	 * 
	 * @return MappingPanelPropertiesSwitchController
	 */
	//	public MappingPanelPropertiesSwitchController getPropertiesSwitchController()
	//	{
	//		if ( propertiesSwitchController == null ) {
	//			// propertiesSwitchController = new MappingPanelPropertiesSwitchController();
	//			propertiesSwitchController = new MappingPanelPropertiesSwitchController(graph);
	//		}
	//		return propertiesSwitchController; // To change body of implemented methods use File | Settings | File Templates.
	//	}

	/**
	 * @param node
	 * @param searchMode
	 *            any of the SEARCH_BY constants defined above.
	 * @return a list of MappingViewCommonComponent if any being found; an empty list if nothing is found.
	 */
	public List<MappingViewCommonComponent> findMappingViewCommonComponentList(Object node, String searchMode)
	{
		return MappingViewCommonComponent.findMappingViewCommonComponentListList(mappingViewList, node, searchMode);
	}

	public JGraph getGraph()
	{
		return graph;
	}

	public MappingMiddlePanel getMiddlePanel()
	{
		return middlePanel;
	}

	protected List getMappingViewList()
	{
		return mappingViewList;
	}

	/**
	 * Reset the mapping view list.
	 * 
	 * @param mappingViewList
	 */
	protected void setMappingViewList(List mappingViewList)
	{
		if ( this.mappingViewList != null && this.mappingViewList.size() > 0 ) {// clean up the mapping relation before re-assigning
			int size = this.mappingViewList.size();
			for (int i = 0; i < size; i++) {
				Object o = this.mappingViewList.get(i);
				if ( o instanceof MappingViewCommonComponent ) {
					MappingViewCommonComponent comp = (MappingViewCommonComponent) o;
					comp.setMappableFlag(false);
				}
			}
		}
		this.mappingViewList = mappingViewList;
	}

	public void setMappingData(Mapping mappingData)
	{
		if ( isGraphChanged() || graph.getRoots().length > 0 ) {// if changed, clear them up
			// clean up
			clearAllGraphCells();
		}
		this.mappingData = mappingData;
		if ( mappingData != null ) {
			constructMappingGraph();
			// clear the flag so that from this point on, any user change on the graph will be considered as change.
			setGraphChanged(false);
			registerLinkHighlighter();
		}
	}

	public void setMappingData(Mapping mappingData, boolean flag)
	{
		constructMappingGraph();
		setGraphChanged(false);
		registerLinkHighlighter();
	}


	/**
	 * Called by MiddlePanelMarqueeHandler Insert a new Edge between source and target
	 */
	public boolean handleConnect(DefaultPort source, DefaultPort target)
	{	
		if ( !source.getEdges().isEmpty() || !target.getEdges().isEmpty() ) {// either port has been used, should report
			StringBuffer msg = new StringBuffer();
			if ( !source.getEdges().isEmpty() ) {
				msg.append("This source port number is being used. Input again another port number.");
			}
			if ( !target.getEdges().isEmpty() ) {
				if ( msg.length() > 0 ) {
					msg.append("\n");
				}
				msg.append("This target port number is being used. Input again another port number.");
			}
			JOptionPane.showMessageDialog(middlePanel.getRootPane().getParent(), msg.toString(), "Mapping Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		//Log.logInfo(this, getClass().getName() + " will link source and target port.");
		// Construct Edge with no label
		DefaultEdge edge = new DefaultEdge();
		edge.setSource(source);
		edge.setTarget(target);
		AttributeMap lineStyle = UIHelper.getDefaultUnmovableEdgeStyle(source);
		if ( graph.getModel().acceptsSource(edge, source) && graph.getModel().acceptsTarget(edge, target) ) {
			// Create a Map that holds the attributes for the edge
			edge.getAttributes().applyMap(lineStyle);
			MappableNode sourceNode = (MappableNode) source;// getMappableNodeThroughPort(source);
			MappableNode targetNode = (MappableNode) target;// getMappableNodeThroughPort(target);
			if ( sourceNode == null || targetNode == null ) {
				StringBuffer msg = new StringBuffer("Cannot find mappable source or target node.");
				JOptionPane.showMessageDialog(middlePanel.getRootPane().getParent(), msg.toString(), "Mapping Error", JOptionPane.ERROR_MESSAGE);
			}
			// Insert the Edge and its Attributes
			graph.getGraphLayoutCache().insertEdge(edge, source, target);
			MappingViewCommonComponent comp = new MappingViewCommonComponent(sourceNode, targetNode, source, target, edge);
			edge.setUserObject(comp);
			mappingViewList.add(comp);
			setGraphChanged(true);
			return true;
		} else {
			List reasonList = ((MiddlePanelGraphModel) graph.getModel()).getNotAcceptableReasonList();
			JOptionPane.showMessageDialog(middlePanel.getRootPane().getParent(), reasonList.toArray(new Object[0]), "Mapping Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/**
	 * Handle the deletion of all graph cells on the middle panel.
	 */
	public synchronized void handleDeleteAll()
	{     
		clearAllGraphCells();

		//repaint the source and target scrollPanes
		mappingPanel.getSourceScrollPane().repaint();
		mappingPanel.getTargetScrollPane().repaint();

		System.out.println("middlePanel type: " + middlePanel.getKind() );

		setGraphChanged(true);
	}

	/**
	 * Handle the deletion of graph cells on the middle panel.
	 */
	public synchronized void handleDelete()
	{
		Object[] cells = graph.getSelectionCells();
		removeCells(cells, true);
		unmapCells(cells);
		setGraphChanged(true);
	}

	private void unmapCells(Object[] cells)
	{
		//System.out.println("middlePanel kind: " + middlePanel.getKind() );
		for(int i=0; i<cells.length; i++)
		{
			if(cells[i] == null || !(cells[i] instanceof DefaultEdge)) 
				continue;
			DefaultEdge edge = (DefaultEdge) cells[i];

			MappingViewCommonComponent e = (MappingViewCommonComponent) edge.getUserObject();
			DefaultSourceTreeNode srcNode = (DefaultSourceTreeNode) e.getSourceNode();
			DefaultTargetTreeNode tgtNode = (DefaultTargetTreeNode) e.getTargetNode();
			srcNode.setMapStatus(false);
			tgtNode.setMapStatus(false);
			mappingViewList.remove(e);
			
		}
	}

	/**
	 * Called by setMappingData() or constructMappingGraph(), and other methods.
	 */
	public void clearAllGraphCells()
	{
		// clean up
		Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(), graph.getRoots()).toArray();
		// call to remove all cells
		removeCells(cells, false);
		unmapCells(cells);		
		setMappingViewList(Collections.synchronizedList(new ArrayList<MappingViewCommonComponent>()));
		setGraphChanged(false);
	}

	private void removeCells(Object[] cells, boolean findAssociatedCells)
	{
		//repaint the source and target tree panel if a functionBox is deleted
		boolean repaintSourceTarget=false;
		if (cells!=null&&cells.length>0)
		{
			Object firstToDelete = cells[0];
			//			if (firstToDelete instanceof FunctionBoxCell)
			//				repaintSourceTarget=true;
		}

		cells = DefaultGraphModel.getDescendants(graph.getModel(), cells).toArray();
		if ( !findAssociatedCells ) {// no need to find associated cells, so directly remove them.
			graph.getGraphLayoutCache().remove(cells, true, true);
			return;
		}
		List cellSelectionList = new ArrayList(Arrays.asList(cells));
		List graphToBeDeleteList = new ArrayList();
		List mappingViewToBeDeletedList = new ArrayList();
		int size = mappingViewList.size();
		for (int i = 0; i < size; i++) {
			////			MappingViewCommonComponent comp = (MappingViewCommonComponent) mappingViewList.get(i);
			//			if ( comp.findMatchedCell(cellSelectionList) ) {
			//				mappingViewToBeDeletedList.add(comp);
			//			}
		}
		// reverse back in case some additions are added by calling comp.findMatchedCell() above.
		cells = cellSelectionList.toArray();
		if ( cells != null ) {
			cells = DefaultGraphModel.getDescendants(graph.getModel(), cells).toArray();
			// graph.getModel().remove(cells);
			graph.getGraphLayoutCache().remove(cells, true, true);
			// execute the clean-up of mappingViewList and reset the mappable flag only if after
			// succesfully removed them from graph above.
			//			for (Iterator it = mappingViewToBeDeletedList.iterator(); it.hasNext();) {
			//				((MappingViewCommonComponent) it.next()).setMappableFlag(false);
			//			}
			//			for (int i = 0; i < cells.length; i++) {
			//				if ( cells[i] instanceof FunctionBoxCell ) {
			//					FunctionBoxMutableViewInterface userObject = (FunctionBoxMutableViewInterface) ((FunctionBoxCell) cells[i]).getUserObject();
			//					getUsageManager().removeFunctionUsage(userObject);
			//				}
			//			}
			mappingViewList.removeAll(mappingViewToBeDeletedList);
		}

		//repaint the source and target scrollPanes
		if (repaintSourceTarget)
		{
			mappingPanel.getSourceScrollPane().repaint();
			mappingPanel.getTargetScrollPane().repaint();
		}
	}
	/**
	 * construct and add graph ports to the given cell with constructed attributes to the map.
	 * 
	 * @param function
	 * @param portAttributes
	 * @param parentMap
	 * @param cell
	 * @param cellAttributes
	 * @param numberOfPorts
	 * @param portOrientation
	 * @param maxPortsOfGivenFunction
	 *            the max number of input and output ports to help figure out the offset of the title area.
	 * @return the map of attributes.
	 */
	//	protected Map addGraphPorts(FunctionMeta function, Map portAttributes, ParentMap parentMap, DefaultGraphCell cell, Map cellAttributes, int numberOfPorts, int portOrientation, int maxPortsOfGivenFunction)
	//	{
	//		// Log.logInfo(this, "numOfPorts: " + numberOfPorts + ",orientation=" + portOrientation);
	//		// key=port, value=its attribute map of portAttributes
	////		Rectangle2D bounds = GraphConstants.getBounds(cellAttributes);
	//		Dimension portDimension = new Dimension(FunctionBoxDefaultPortView.MY_SIZE, FunctionBoxDefaultPortView.MY_SIZE);
	//		// create ports and need 100 percent unit for relative positioning.
	//		int unit = GraphConstants.PERMILLE;
	//		int offsetX = (int) portDimension.getWidth() / 2;
	//		int offsetY = (int) portDimension.getHeight() / 2;
	//		int interimFactor = (int) (unit / (numberOfPorts + 1));
	//		int offsetTitleHeight = ((int) (unit / (maxPortsOfGivenFunction + 1))) - interimFactor / 2;// interimFactor + 10;
	//		List<ParameterMeta> paramList = (portOrientation == UIHelper.PORT_LEFT) ? function.getInputDefinitionList() : function.getOuputDefinitionList();
	//		for (int i = 0; i < numberOfPorts; i++) {
	//			Map attriMap = new Hashtable();
	//			DefaultPort port = null;
	//			if ( portOrientation == UIHelper.PORT_LEFT ) {
	//				attriMap = UIHelper.getDefaultFunctionBoxPortAttributes(attriMap, portDimension);
	//				// GraphConstants.setOffset(attriMap, new Point2D.Double(bounds.getX() - offsetX, bounds.getY() + (interimFactor * (i + 1)) - offsetY));
	//				GraphConstants.setOffset(attriMap, new Point2D.Double(-offsetX, (interimFactor * (i + 1)) - offsetY + offsetTitleHeight));
	//				// port = new FunctionBoxDefaultPort(paramList.get(i));//UIHelper.getDefaultFunctionalBoxInputCaption() + " " + i);
	//			} else if ( portOrientation == UIHelper.PORT_RIGHT ) {
	//				attriMap = UIHelper.getDefaultFunctionBoxPortAttributes(attriMap, portDimension);
	//				// GraphConstants.setOffset(attriMap, new Point2D.Double(bounds.getX() + bounds.getWidth() - portDimension.getWidth() - offsetX, bounds.getY() +
	//				// (interimFactor * (i + 1)) - offsetY));
	//				GraphConstants.setOffset(attriMap, new Point2D.Double(unit + offsetX, (interimFactor * (i + 1)) - offsetY + offsetTitleHeight));
	//				// port = new FunctionBoxDefaultPort(UIHelper.getDefaultFunctionalBoxOutputCaption() + " " + i);
	//			}
	//			port = new FunctionBoxDefaultPort(paramList.get(i));// UIHelper.getDefaultFunctionalBoxInputCaption() + " " + i);
	//			cell.add(port);
	//			portAttributes.put(port, attriMap);
	//			parentMap.addEntry(port, cell);
	//		}
	//		// Add one Floating Port
	//		return portAttributes;
	//	}

	public void renderInJGraph(Graphics g)
	{
		//System.out.println("enter MiddlePanelJGraphController.renderInJGraph.");
		/** the real renderer */
		ConnectionSet cs = new ConnectionSet();
		Map attributes = new Hashtable();

		// render links
		//		List visibleSrcNodes=new ArrayList<DefaultMutableTreeNode>();
		//		if(mappingPanel.getSourceTree()!=null)
		//			visibleSrcNodes=((MappingBaseTree)mappingPanel.getSourceTree()).getAllVisibleMappedNode();
		//		List visibleTgrtNodes=new ArrayList<DefaultMutableTreeNode>();;
		//		if (mappingPanel.getTargetTree()!=null)
		//			visibleTgrtNodes=((MappingBaseTree)mappingPanel.getTargetTree()).getAllVisibleMappedNode();
		int mappingSize = mappingViewList.size();
		for (int i = 0; i < mappingSize; i++) {
			MappingViewCommonComponent mappingComponent = (MappingViewCommonComponent) mappingViewList.get(i);
			MappableNode sourceNode = mappingComponent.getSourceNode();
			MappableNode targetNode = mappingComponent.getTargetNode();
			DefaultGraphCell sourceCell = mappingComponent.getSourceCell();
			DefaultGraphCell targetCell = mappingComponent.getTargetCell();
			DefaultEdge linkEdge = mappingComponent.getLinkEdge();
			AttributeMap lineStyle = linkEdge.getAttributes();
			AttributeMap sourceNodeCellAttribute = sourceCell.getAttributes();
			AttributeMap targetNodeCellAttribute = targetCell.getAttributes();
			//			boolean sourceNodeDisplayed=true;
			//			boolean targetNodeDisplayed=true;
			try {
				//				if ( sourceNode instanceof FunctionBoxDefaultPort ) 
				//				{
				//					if ( targetNode instanceof FunctionBoxDefaultPort ) 
				//					{// todo: consider how to draw functional box movement.
				//					} 
				//					else if ( targetNode instanceof DefaultMutableTreeNode ) 
				//					{
				//						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) targetNode;
				////						targetNodeDisplayed=visibleTgrtNodes.contains(treeNode);
				//						adjustToNewPosition(treeNode, targetNodeCellAttribute);
				//					}
				//				} 
				if ( sourceNode instanceof DefaultMutableTreeNode ) 
				{// neither sourceNode nor targetNode is functional box, so this implies a direct map
					//					if ( !(targetNode instanceof FunctionBoxDefaultPort) && (targetNode instanceof DefaultMutableTreeNode) ) {
					if ( (targetNode instanceof DefaultMutableTreeNode) ) {
						// change target node
						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) targetNode;
						//						targetNodeDisplayed=visibleTgrtNodes.contains(treeNode);
						adjustToNewPosition(treeNode, targetNodeCellAttribute);
					}
					// change source node
					DefaultMutableTreeNode srcNode = (DefaultMutableTreeNode) sourceNode;
					//					sourceNodeDisplayed=visibleSrcNodes.contains(srcNode);
					adjustToNewPosition(srcNode, sourceNodeCellAttribute);
				}// end of else if(sourceNode instanceof DefaultMutableTreeNode)
				if ( sourceNodeCellAttribute != null
						&&targetNodeCellAttribute!=null) {// put in attribute if and only if it is constructed.
					attributes.put(sourceCell, sourceNodeCellAttribute);
					attributes.put(targetCell, targetNodeCellAttribute);
					//reset link color
					//					if (!targetNodeDisplayed||!sourceNodeDisplayed)
					//					{
					//						lineStyle.put("linecolor",this.graphBackgroundColor);
					//					}
					attributes.put(linkEdge, lineStyle);
					// cs.connect(linkEdge, sourceCell.getChildAt(0), targetCell.getChildAt(0));
					// Log.logInfo(this, "Drew line for : " + mappingComponent.toString());
				}
			} catch (Throwable e) {
				e.printStackTrace();
				//Log.logInfo(this, "Did not draw line for : " + mappingComponent.toString(true));
			}
		}// end of for
		graph.getGraphLayoutCache().edit(attributes, cs, null, null);
		graph.getGraphLayoutCache().setSelectsAllInsertedCells(false);
		//System.out.println("leave MiddlePanelJGraphController.renderInJGraph.");
	}


	/**
	 * Adjust the given treenode's display coordinates. If given tree node is null or the root, will simply ignore.
	 * 
	 * @param treeNode
	 *            the tree node
	 * @param oldAttributeMap
	 *            the existing attribute on the graph associated with the given tree node
	 * @return the oldAttributeMap after applying the newly calculated attribute.
	 */
	private AttributeMap adjustToNewPosition(DefaultMutableTreeNode treeNode, AttributeMap oldAttributeMap)
	{
		if ( treeNode != null && !treeNode.isRoot() ) {// change the render value if and only if neither it is null nor a root.
			boolean isFromSourceTree = UIHelper.isDataFromSourceTree(treeNode);
			int sourceYpos = -1;
			AttributeMap newTreeNodeAttribute = null;
			if ( isFromSourceTree ) {
				// Find the Y position for the source for this mappingNode.
				// find the # of pixels hidden. For example : 30
				sourceYpos = calculateScrolledDistanceOnY(mappingPanel.getSourceScrollPane(), treeNode, true);
				// To hide the vertex body from the graph
				newTreeNodeAttribute = UIHelper.getDefaultInvisibleVertexBounds(new Point(0, sourceYpos), true);
			} else {
				sourceYpos = calculateScrolledDistanceOnY(mappingPanel.getTargetScrollPane(), treeNode, true);
				newTreeNodeAttribute = UIHelper.getDefaultInvisibleVertexBounds(new Point(getMaximalXValueOnPane(), sourceYpos), false);
			}
			if ( oldAttributeMap == null ) {// never return null.
				oldAttributeMap = new AttributeMap();
			}
			oldAttributeMap.applyMap(newTreeNodeAttribute);
		}
		return oldAttributeMap;
	}
	/**
	 * Return the number of pixels changed due to scrolling.
	 * 
	 * @param treeScrollPane
	 * @param treeNode
	 * @param reCalculateToNearestParent
	 * @return the number of pixels changed due to scrolling.
	 */
	private int calculateScrolledDistanceOnY(JScrollPane treeScrollPane, DefaultMutableTreeNode treeNode, boolean reCalculateToNearestParent)
	{
		/**
		 * Design rationale: 1) check the given tree node, if it is null or root, set the nodePositionBasedOnTotalPanel to the default root value, i.e., 8; 2)
		 * if the tree node is not root or null, proceed with normal calculation.
		 */
		final int DEFAULT_ROOT_Y_VALUE = 8;
		int nodePositionBasedOnTotalPanel = 0;
		// find the # of pixels hidden. For example : 30
		if ( treeNode == null || treeNode.getParent() == null ) {
			// Log.logInfo(this, (treeNode == null ? "Tree node is null." : "Tree node is the root") + " will use default value.");
			nodePositionBasedOnTotalPanel = DEFAULT_ROOT_Y_VALUE;
		} else {
			// System.out.println("To figure out the value via scroll bar positions.");
			// find the Y coordinate of the node. For example : 300
			TreePath tp = new TreePath(treeNode.getPath());
			JTree tree = ((JTree) treeScrollPane.getViewport().getView());
			int row = tree.getRowForPath(tp);
			Rectangle pathBounds = tree.getPathBounds(tp);
			if ( pathBounds == null ) {
				// Log.logInfo(this, "path bounds is null. tp is '" + tp.toString() + "'.");
				// System.out.println("The path bounds is null! on '" + treeNode + "' of type " + treeNode.getClass().getName());
				if ( reCalculateToNearestParent ) {// escape if not reCal or if the treeNode is the root.
					return calculateScrolledDistanceOnY(treeScrollPane, (DefaultMutableTreeNode) treeNode.getParent(), reCalculateToNearestParent);
				} else {// default to the root
					row = 0;
				}
			}
			if ( row == -1 )// (r==null)
			{
				// Log.logInfo(this, "tp is '" + tp.toString() + "'.");
				// System.out.println("the row value is -1! on '" + treeNode + "' of type " + treeNode.getClass().getName());
				if ( reCalculateToNearestParent && treeNode.getParent() != null ) {// escape if not reCal or if the treeNode is the root.
					return calculateScrolledDistanceOnY(treeScrollPane, (DefaultMutableTreeNode) treeNode.getParent(), reCalculateToNearestParent);
				} else {// default to the root
					// System.out.println("Default set to the root!");
					row = 0;
				}
			}
			// System.out.println("Row value: " + row);
			if ( row > 0 ) {
				Rectangle r = tree.getRowBounds(row);
				Point point = r.getLocation();
				int graphHeightHidden = (int) middlePanel.getGraphScrollPane().getViewport().getViewPosition().getY();
				int treeHeightHidden = (int) treeScrollPane.getViewport().getViewPosition().getY();
				nodePositionBasedOnTotalPanel = (int) point.getY() + (int) r.getHeight() / 2 + graphHeightHidden - treeHeightHidden;
			} else {
				nodePositionBasedOnTotalPanel = DEFAULT_ROOT_Y_VALUE;
			}
			// find the Y coordinate based on the *visible* area.
			// for example : 300 - 30 + 1/2(the node height) = 290
			// Log.logInfo(this, treeNode.toString() + " view position:' " + treeHeightHidden + "'");
			// Log.logInfo(this, treeNode.toString() + " tree node Y:' " + nodePositionBasedOnTotalPanel + "'");
		}
		int newYpos = nodePositionBasedOnTotalPanel;
		if ( newYpos < DEFAULT_ROOT_Y_VALUE ) {// never lower than the NOT_FOUND_VALUE
			newYpos = DEFAULT_ROOT_Y_VALUE;
		}
		// Log.logInfo(this, treeNode.toString() + " new YPos: '" + newYpos + "'.");
		return newYpos;
	}

	/**
	 * Create mapping relation between the source and target nodes.
	 * 
	 * @param sourceNode
	 * @param targetNode
	 * @return if mapping is successfully created.
	 */
	public boolean createMapping(MappableNode sourceNode, MappableNode targetNode)
	{
		boolean result = false;
		// to remember the list of cells, edges, etc. that involve in the mapping.
		List graphCellList = new ArrayList();
		try {
			if ( sourceNode == null || targetNode == null ) {
				String msg = (sourceNode == null) ? "source node is null" : "";
				if ( targetNode == null ) {
					if ( msg.length() > 0 ) {
						msg += " and ";
					}
					msg += "target node is null";
				}
				msg += "!";
				System.out.println(msg);
				result = false;
				return result;
			}
			//			if ( sourceNode instanceof FunctionBoxDefaultPort ) {// drag from FunctionBoxCell
			//				if ( targetNode instanceof FunctionBoxDefaultPort ) {
			//					result = createFunctionBoxPortToFunctionBoxPortMapping((FunctionBoxDefaultPort) sourceNode, (FunctionBoxDefaultPort) targetNode, graphCellList);
			//				} else if ( targetNode instanceof DefaultMutableTreeNode ) {// functional box to tree link
			//					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) targetNode;
			//					result = createTreeToFunctionBoxPortMapping(treeNode, (FunctionBoxDefaultPort) sourceNode, graphCellList);
			//				}
			if ( sourceNode instanceof DefaultMutableTreeNode ) {// drag from tree to middle panel
				// todo: will source tree always stays at left? If not, the implicit logic between source is left and target is right should have been changed.
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) sourceNode;
				//				if ( targetNode instanceof FunctionBoxDefaultPort ) {
				//					result = createTreeToFunctionBoxPortMapping(treeNode, (FunctionBoxDefaultPort) targetNode, graphCellList);
				if ( targetNode instanceof DefaultTargetTreeNode )// targetNode instanceof DefaultMutableTreeNode
				{// mapping between source and target tree node
					result = createTreeToTreeDirectMapping((DefaultSourceTreeNode) sourceNode, (DefaultTargetTreeNode) targetNode, graphCellList);
				} else if ( targetNode instanceof DefaultSourceTreeNode )// targetNode instanceof DefaultMutableTreeNode
				{// mapping between source and target tree node
					// reversed drag and drop
					result = createTreeToTreeDirectMapping((DefaultSourceTreeNode) targetNode, (DefaultTargetTreeNode) sourceNode, graphCellList);
				} else {
					System.out.println("Not a graph cell or tree node, what is it? '" + (targetNode == null ? "null" : targetNode.toString() + " " + targetNode.getClass().getName()) + "'");
				}
				//System.out.println("object is '" + object.getClass().getName() + "'");
			} else {
				System.out.println(sourceNode + " is not accepted by " + getClass().getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			//Log.logException(this, e);
		}
		if ( result ) {// successfully mapped, add to mapping
			DefaultGraphCell temp1 = (DefaultGraphCell) graphCellList.get(0);
			DefaultGraphCell temp2 = (DefaultGraphCell) graphCellList.get(1);
			DefaultEdge edge = (DefaultEdge) graphCellList.get(2);
			DefaultGraphCell sourceCell = null;
			DefaultGraphCell targetCell = null;
			//			if ( sourceNode instanceof FunctionBoxDefaultPort ) {
			//				sourceCell = temp1 == sourceNode ? temp1 : temp2;
			//			} else {
			sourceCell = temp1.getUserObject() == sourceNode ? temp1 : temp2;
			//			}
			//			if ( targetNode instanceof FunctionBoxDefaultPort ) {
			//				targetCell = temp1 == targetNode ? temp1 : temp2;
			//			} else {
			targetCell = temp1.getUserObject() == targetNode ? temp1 : temp2;
			//			}
			MappingViewCommonComponent viewComp = new MappingViewCommonComponent(sourceNode, targetNode, sourceCell, targetCell, edge);
			edge.setUserObject(viewComp);
			//			Log.logInfo(this, "mapped: "+viewComp);
			mappingViewList.add(viewComp);
			setGraphChanged(true);
		}
		return result;
	}

	private boolean createTreeToTreeDirectMapping(DefaultSourceTreeNode sourceNode, DefaultTargetTreeNode targetNode, List graphCellList)
	{
		// boolean result = sourceNode.isMapped() || targetNode.isMapped();
		// no longer need to check anymore.
		boolean result = false;
		if ( !result ) {// neither one has been mapped before
			ConnectionSet cs = new ConnectionSet();
			Map attributes = new Hashtable();
			AttributeMap lineStyle = UIHelper.getDefaultUnmovableEdgeStyle(((ElementMetaLoader.MyTreeObject)sourceNode.getUserObject()).getObj());//.getDefaultUnmovableEdgeStyle();
			Dimension cellDimension = UIHelper.getDefaultSourceOrTargetVertexDimension();
			DefaultGraphCell sourceCell = null;
			DefaultGraphCell targetCell = null;
			DefaultEdge linkEdge = null;
			AttributeMap sourceAttribute = null;
			AttributeMap targetAttribute = null;
			// Find the Y position for the source for this mappingNode.
			// find the # of pixels hidden. For example : 30
			int sourceYpos = calculateScrolledDistanceOnY(mappingPanel.getSourceScrollPane(), sourceNode, false);
			// so the same for the Target side.
			int targetYpos = calculateScrolledDistanceOnY(mappingPanel.getTargetScrollPane(), targetNode, false);
			// if (!(isOutOfBound(sourceYpos) && isOutOfBound(targetYpos)))
			// {
			// process source
			sourceCell = new DefaultGraphCell(sourceNode);
			sourceAttribute = UIHelper.getDefaultInvisibleVertexAttribute(new Point(0, sourceYpos), true);
			// sourceAttribute = (AttributeMap) UIHelper.createBounds(new AttributeMap(), 0 - cellDimension.getWidth(), sourceYpos, cellDimension,
			// UIHelper.DEFAULT_VERTEX_COLOR, false);
			sourceCell.add(new DefaultPort());
			// process target
			targetCell = new DefaultGraphCell(targetNode);
			targetAttribute = UIHelper.getDefaultInvisibleVertexAttribute(new Point(getMaximalXValueOnPane(), targetYpos), false);
			// targetAttribute = (AttributeMap) UIHelper.createBounds(new AttributeMap(), this.middlePanel.getWidth(), targetYpos, cellDimension,
			// UIHelper.DEFAULT_VERTEX_COLOR, false);
			targetCell.add(new DefaultPort());
			// process the edge
			linkEdge = new DefaultEdge();
			attributes.put(sourceCell, sourceAttribute);
			attributes.put(targetCell, targetAttribute);
			attributes.put(linkEdge, lineStyle);
			// return back those being affected.
			graphCellList.add(sourceCell);
			graphCellList.add(targetCell);
			graphCellList.add(linkEdge);
			cs.connect(linkEdge, sourceCell.getChildAt(0), targetCell.getChildAt(0));
			graph.getGraphLayoutCache().insert(new Object[] { sourceCell, targetCell, linkEdge }, attributes, cs, null, null);
			// graph.getGraphLayoutCache().edit(attributes, cs, null, null);
			graph.getGraphLayoutCache().setSelectsAllInsertedCells(false);
			result = true;
			//System.out.println("invisible source bounds: '" + GraphConstants.getBounds(sourceCell.getAttributes()) + "'");
			//System.out.println("invisible target bounds: '" + GraphConstants.getBounds(targetCell.getAttributes()) + "'");
			// }
			// else
			// {
			// result = false;
			// }
		} else {
			result = false;
		}
		if ( result ) {
			sourceNode.setMapStatus(true);
			targetNode.setMapStatus(true);
		}
		return result;
	}


	/**
	 * Called to render mapping (functional-box-driven or direct) after setMappingData() is called. When this is called, it assumes the source and target tree
	 * have been loaded successfully.
	 */
	private synchronized void constructMappingGraph()
	{
		if(mappingData.getLinks() == null){
			mappingData.setLinks(new Mapping.Links());
		}

		List<LinkType> mapList = mappingData.getLinks().getLink();
		int mapSize = mapList.size();
		if ( mapSize == 0 )
		{
			// Log.logInfo(this, "No need to refresh graph.");
			return;
		}
		// render functional box first
		// Log.logInfo(this, "Total function component: '" + functionSize + "'.");
		// render map second
		for (int i = 0; i < mapSize; i++)
		{
			LinkType map = mapList.get(i);
			LinkpointType sourceMapComp = map.getSource();
			LinkpointType targetMapComp = map.getTarget();

			MappableNode sourceNode = null;
			MappableNode targetNode = null;

			sourceNode = getSourceMappableNode(sourceMapComp);
			if (sourceNode == null)
			{
				sourceNode = getTargetMappableNode(sourceMapComp);
				targetNode = getSourceMappableNode(targetMapComp);
			}
			else targetNode = getTargetMappableNode(targetMapComp);

			createMapping(sourceNode, targetNode);
		}
	}

	private MappableNode getSourceMappableNode(LinkpointType sourceMapComp)
	{
		MappableNode sourceNode = null;

		String compId = sourceMapComp.getComponentid();
		String id = sourceMapComp.getId();

		sourceNode = UIHelper.constructMappableNodeObjectXmlPath(mappingPanel.getSourceTree().getModel().getRoot(), id);
		//if (sourceNode == null) System.out.println("QQQQ3-1 :");
		return sourceNode;
	}
	
	private MappableNode getTargetMappableNode(LinkpointType targetMapComp)
	{
		MappableNode targetNode = null;
		String compId = targetMapComp.getComponentid();
		String id = targetMapComp.getId();
		targetNode = UIHelper.constructMappableNodeObjectXmlPath(mappingPanel.getTargetTree().getModel().getRoot(), id);
		return targetNode;
	}

	private int getMaximalXValueOnPane()
	{
		int visibleWidth = (int) this.middlePanel.getGraphScrollPane().getVisibleRect().getWidth();
		int viewPortVisibleWidth = (int) this.middlePanel.getGraphScrollPane().getViewport().getVisibleRect().getWidth();
		int viewPortViewSizeWidth = (int) this.middlePanel.getGraphScrollPane().getViewport().getViewSize().getWidth();
		int viewPortViewRectWidth = (int) this.middlePanel.getGraphScrollPane().getViewport().getViewRect().getWidth();
		int middlePanelWidth = this.middlePanel.getWidth();
		// Log.logInfo(this, "middlePanelWidth='" + middlePanelWidth + "',visibleWidth='" + visibleWidth + "'.");
		// Log.logInfo(this, "viewPortVisibleWidth='" + viewPortVisibleWidth + "',viewPortViewSizeWidth='" + viewPortViewSizeWidth + "'," +
		// "',viewPortViewRectWidth='" + viewPortViewRectWidth + "'.");
		// return viewPortVisibleWidth;// - 23;
		return visibleWidth - 20;
	}
	
	/**
	 * Get mapping relation consolidated.
	 * 
	 * @param refresh
	 *            if true, the underline implementation will refresh data from user's input; otherwise, it will return what it has now, which may not be
	 *            up-to-date;
	 * @return mapping relation consolidated.
	 */
	public Mapping retrieveMappingData(boolean refresh) {
		/**
		 * Design rationale: 1) for each of functions in the graph, create FunctionComponent; 2) for each of direct mapping, create map object associated with
		 * it. Caveat: for simplicity, all previous map information is removed. Therefore, new component may carry different UUID then.
		 */
		if ( !refresh ) {// return what it is now.
			return mappingData;
		}
		if ( mappingData == null ) {
			// mappingData = new MappingImpl();
			throw new IllegalStateException("If refresh is true, the mapping data in " + getClass().getName() + " shall not be null. Please call registerXYZs() to add in soruce and target components.");
		}
		// clear out the data before adding.
		if(mappingData.getLinks() == null){
			mappingData.setLinks(new Mapping.Links());
		}else{
			mappingData.getLinks().getLink().clear();
		}
		for (Iterator it = mappingViewList.iterator(); it.hasNext();) {
			MappingViewCommonComponent comp = (MappingViewCommonComponent) it.next();
			MappableNode sourceNode = comp.getSourceNode();
			MappableNode targetNode = comp.getTargetNode();
			String srcComponentId = ((Component) ((ElementMetaLoader.MyTreeObject) ((DefaultMutableTreeNode) sourceNode).getUserObject()).getRootObj()).getId();
			String tgtComponentId = ((Component) ((ElementMetaLoader.MyTreeObject) ((DefaultMutableTreeNode) targetNode).getUserObject()).getRootObj()).getId();
			String srcPath = UIHelper.getPathStringForNode((DefaultMutableTreeNode) sourceNode);
			String tgtPath = UIHelper.getPathStringForNode((DefaultMutableTreeNode) targetNode);

			MappingFactory.addLink(mappingData, srcComponentId, srcPath, tgtComponentId, tgtPath);
		}
		return mappingData;
	}
	
	private boolean addFunctionInstance(FunctionBoxUserObject functionInstance)
	{
		FunctionDef function = functionInstance.getFunctionDef();
		ViewType viewInfo = functionInstance.getViewMeta();
		Point2D startPoint = new Point(viewInfo.getX().intValue() < 0 ? 25 : viewInfo.getX().intValue(), viewInfo.getY().intValue() < 0 ? 25 : viewInfo.getY().intValue());
		// Construct Vertex with Label
		FunctionBoxCell functionBoxVertex = new FunctionBoxCell(functionInstance);// createDefaultGraphCell(function);
		Dimension functionBoxDimension = new Dimension(viewInfo.getWidth().intValue() <= 0 ? 200 : viewInfo.getWidth().intValue(), viewInfo.getHight().intValue() <= 0 ? 200 : viewInfo.getHight().intValue());
		// Create a Map that holds the attributes for the functionBoxVertex
		// functionBoxVertex.getAttributes().applyMap(createCellAttributes(startPoint, functionBoxDimension));
		//Color backGroundColor = viewInfo.getColor() == null ? UIHelper.DEFAULT_VERTEX_COLOR : viewInfo.getColor();
		Color backGroundColor = UIHelper.DEFAULT_VERTEX_COLOR;
		Map funcBoxAttrbutes = UIHelper.createBounds(new AttributeMap(), startPoint, functionBoxDimension, backGroundColor, true);
		GraphConstants.setSizeable(funcBoxAttrbutes, true);
		// Insert the functionBoxVertex (including child port and attributes)
		Map portAttributes = new Hashtable();
		ParentMap parentMap = new ParentMap();
		int numOfInputs = FunctionBoxViewManager.getInstance().getTotalNumberOfDefinedInputs(function);
		int numOfOutputs = FunctionBoxViewManager.getInstance().getTotalNumberOfDefinedOutputs(function);
		int maximumPorts = Math.max(numOfInputs, numOfOutputs);
		addGraphPorts(function, portAttributes, parentMap, functionBoxVertex, funcBoxAttrbutes, numOfInputs, UIHelper.getDefaultFunctionalBoxInputOrientation(), maximumPorts);
		addGraphPorts(function, portAttributes, parentMap, functionBoxVertex, funcBoxAttrbutes, numOfOutputs, UIHelper.getDefaultFunctionalBoxOutputOrientation(), maximumPorts);
		// Create a Map that holds the attributes for the Vertex
		functionBoxVertex.getAttributes().applyMap(funcBoxAttrbutes);
		graph.getGraphLayoutCache().insert(functionBoxVertex);
		graph.getGraphLayoutCache().insert(functionBoxVertex.getChildren().toArray(), portAttributes, null, parentMap, null);
		setGraphChanged(true);
		return true;
		// EDIT does not work!
		// graph.getGraphLayoutCache().edit(functionBoxVertex.getChildren().toArray(), portAttributes);
		// graph.getGraphLayoutCache().edit(portAttributes);
		// graph.getGraphLayoutCache().insert(new Object[]{functionBoxVertex}, funcBoxAttrbutes, null, parentMap, null);
		// Log.logInfo(this, "functionBoxVertex.getChildren().size(): " + functionBoxVertex.getChildren().size());
		// this.getGraphLayoutCache().insert(functionBoxVertex.getChildren().toArray(), portAttributes, null, parentMap);
		// following received java.lang.ClassCastException
		// graph.getModel().insert(new Object[]{functionBoxVertex}, funcBoxAttrbutes, null, null, null);
		// graph.getModel().edit(portAttributes, null, null, null);
	}

	/**
	 * construct and add graph ports to the given cell with constructed attributes to the map.
	 * 
	 * @param function
	 * @param portAttributes
	 * @param parentMap
	 * @param cell
	 * @param cellAttributes
	 * @param numberOfPorts
	 * @param portOrientation
	 * @param maxPortsOfGivenFunction
	 *            the max number of input and output ports to help figure out the offset of the title area.
	 * @return the map of attributes.
	 */
	protected Map addGraphPorts(FunctionDef function, Map portAttributes, ParentMap parentMap, DefaultGraphCell cell, Map cellAttributes, int numberOfPorts, int portOrientation, int maxPortsOfGivenFunction)
	{
		// Log.logInfo(this, "numOfPorts: " + numberOfPorts + ",orientation=" + portOrientation);
		// key=port, value=its attribute map of portAttributes
//		Rectangle2D bounds = GraphConstants.getBounds(cellAttributes);
		Dimension portDimension = new Dimension(FunctionBoxDefaultPortView.MY_SIZE, FunctionBoxDefaultPortView.MY_SIZE);
		// create ports and need 100 percent unit for relative positioning.
		int unit = GraphConstants.PERMILLE;
		int offsetX = (int) portDimension.getWidth() / 2;
		int offsetY = (int) portDimension.getHeight() / 2;
		int interimFactor = (int) (unit / (numberOfPorts + 1));
		int offsetTitleHeight = ((int) (unit / (maxPortsOfGivenFunction + 1))) - interimFactor / 2;// interimFactor + 10;
		List<FunctionData> paramList = (portOrientation == UIHelper.PORT_LEFT) ? function.getData(): function.getData();
		for (int i = 0; i < numberOfPorts; i++) {
			Map attriMap = new Hashtable();
			DefaultPort port = null;
			if ( portOrientation == UIHelper.PORT_LEFT ) {
				attriMap = UIHelper.getDefaultFunctionBoxPortAttributes(attriMap, portDimension);
				// GraphConstants.setOffset(attriMap, new Point2D.Double(bounds.getX() - offsetX, bounds.getY() + (interimFactor * (i + 1)) - offsetY));
				GraphConstants.setOffset(attriMap, new Point2D.Double(-offsetX, (interimFactor * (i + 1)) - offsetY + offsetTitleHeight));
				// port = new FunctionBoxDefaultPort(paramList.get(i));//UIHelper.getDefaultFunctionalBoxInputCaption() + " " + i);
			} else if ( portOrientation == UIHelper.PORT_RIGHT ) {
				attriMap = UIHelper.getDefaultFunctionBoxPortAttributes(attriMap, portDimension);
				// GraphConstants.setOffset(attriMap, new Point2D.Double(bounds.getX() + bounds.getWidth() - portDimension.getWidth() - offsetX, bounds.getY() +
				// (interimFactor * (i + 1)) - offsetY));
				GraphConstants.setOffset(attriMap, new Point2D.Double(unit + offsetX, (interimFactor * (i + 1)) - offsetY + offsetTitleHeight));
				// port = new FunctionBoxDefaultPort(UIHelper.getDefaultFunctionalBoxOutputCaption() + " " + i);
			}
			port = new FunctionBoxDefaultPort(paramList.get(i));// UIHelper.getDefaultFunctionalBoxInputCaption() + " " + i);
			cell.add(port);
			portAttributes.put(port, attriMap);
			parentMap.addEntry(port, cell);
		}
		// Add one Floating Port
		return portAttributes;
	}
	
	public PropertiesSwitchController getPropertiesSwitchController() {
		if ( propertiesSwitchController == null ) {
			// propertiesSwitchController = new MappingPanelPropertiesSwitchController();
			propertiesSwitchController = new DefaultPropertiesSwitchController();//graph);
		}
		return propertiesSwitchController; // To change body of implemented methods use File | Settings | File Templates.
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.7  2009/01/02 16:05:17  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.6  2008/12/29 22:18:18  linc
 * HISTORY: function UI added.
 * HISTORY:
 * HISTORY: Revision 1.5  2008/12/10 15:43:02  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
 * HISTORY: Revision 1.4  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.3  2008/12/04 21:34:20  linc
 * HISTORY: Drap and Drop support with new Swing.
 * HISTORY:
 * HISTORY: Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
