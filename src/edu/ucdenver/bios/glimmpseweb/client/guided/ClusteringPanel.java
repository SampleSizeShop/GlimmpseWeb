/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TreeItemAction;
import edu.ucdenver.bios.glimmpseweb.client.TreeItemIterator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;

/**
 * Panel which allows the user to enter information about clustering
 * in the study design
 * 
 * @author VIJAY AKULA
 *
 */
public class ClusteringPanel extends WizardStepPanel implements ChangeHandler
{
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    // indicates whether the user had added clustering or not
    protected boolean hasClustering = false;
    // tree describing the clustering hierarchy
    protected Tree clusteringTree = new Tree();
    // number of items in the tree
    protected int itemCount = 0;
    // pointer to the lower most leaf in the tree
    protected TreeItem currentLeaf = null;
    // actions performed when iterating over the tree of clustering nodes
    // action to determine if the screen is complete
    TreeItemAction checkCompleteAction = new TreeItemAction() {
        @Override
        public void execute(TreeItem item, int visitCount, int parentVisitCount)
        {
            updateComplete((ClusteringPanelSubPanel) item.getWidget());
        }
    };
    // action to build the clustering domain object
    TreeItemAction buildClusteringObjectAction = new TreeItemAction() {
        @Override
        public void execute(TreeItem item, int visitCount, int parentVisitCount)
        {
            buildClusteringNode((ClusteringPanelSubPanel) item.getWidget(), visitCount, parentVisitCount);
        }
    };
    
    /* buttons */
    // button to initiate entering of clustering information
    protected Button addClusteringButton = 
        new Button(GlimmpseWeb.constants.clusteringPanelAddClusteringButton(),
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            addSubgroup();
            toggleClustering();
        }
    });
    // button to remove clustering information
    protected Button removeClusteringButton = 
        new Button(GlimmpseWeb.constants.clusteringPanelRemoveClusteringButton(),
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            reset();
            toggleClustering();
        }
    });
    //  button for adding a subgroups
    protected Button addSubgroupButton = 
        new Button(GlimmpseWeb.constants.clusteringPanelAddSubgroupButton(),
                new ClickHandler() {
        public void onClick(ClickEvent event) {
            addSubgroup();
        }
    });
    //  button for removing subgroups
    protected Button removeSubgroupButton = 
        new Button(GlimmpseWeb.constants.clusteringPanelRemoveSubgroupButton(), 
                new ClickHandler() {
        public void onClick(ClickEvent event) {
            removeSubgroup();
        }
    });
    // list of clustering node domain objects
    protected ArrayList<ClusterNode> clusteringNodeList = new ArrayList<ClusterNode>();
    
    protected String buttonText = "Add Clustering";
    protected String description1;


    protected HorizontalPanel buttonPanel = new HorizontalPanel();


    
    private static final String BUTTON_STYLE = "buttonStyle";




    /**
     * Constructor for the Clustering Panel Class
     */
    public ClusteringPanel(WizardContext context)
    {
        super(context, "Clustering");
        notifyComplete(); // user can proceed forward by default
        VerticalPanel panel = new VerticalPanel();
        // title and header text
        HTML title = new HTML(GlimmpseWeb.constants.clusteringPanelTitle());
        HTML header = new HTML(GlimmpseWeb.constants.clusteringPanelDescription());

        panel.add(title);
        panel.add(header);
        panel.add(addClusteringButton);
        panel.add(removeClusteringButton);
        buttonPanel.add(addSubgroupButton);
        buttonPanel.add(removeSubgroupButton);
        panel.add(buttonPanel);
        panel.add(clusteringTree);

        // show/hide the appropriate buttons
        addClusteringButton.setVisible(!hasClustering);
        removeClusteringButton.setVisible(hasClustering);
        addSubgroupButton.setVisible(hasClustering);
        removeSubgroupButton.setVisible(hasClustering);


        // Setting Styles 
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        title.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
       // description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        addSubgroupButton.setStyleName(BUTTON_STYLE);
        removeSubgroupButton.setStyleName(BUTTON_STYLE);

        /******** REMOVE THIS */
        ArrayList<ClusterNode> clusterNodeArrayList = new ArrayList<ClusterNode>();
        ClusterNode root = new ClusterNode();
        root.setGroupeName("Foo");
        root.setGroupeSize(10);
        root.setNode(1);
        root.setParent(0);
        clusterNodeArrayList.add(root);
        ClusterNode child = new ClusterNode();
        child.setGroupeName("Bar");
        child.setGroupeSize(20);
        child.setNode(2);
        child.setParent(1);
        clusterNodeArrayList.add(child);

        studyDesignContext.setClustering(this, clusterNodeArrayList);
        onWizardContextLoad();
        /********** END REMOVE THIS */

        // Initializing widget
        initWidget(panel);
    }

    /**
     * Add a Clustering Panel Sub Panel class to the tree.
     */
    private void addSubgroup() 
    {
        ClusteringPanelSubPanel subpanel = new ClusteringPanelSubPanel();	
        subpanel.addChangeHandler(this);
        TreeItem newLeaf = new TreeItem(subpanel);
        if (currentLeaf == null)
        {
            clusteringTree.addItem(newLeaf);
            newLeaf.setState(true);
        }
        else
        {
            currentLeaf.addItem(newLeaf);
            currentLeaf.setState(true);
            if(itemCount == 2)
            {
                addSubgroupButton.setVisible(false);
            }			
        }
        itemCount++;
        currentLeaf = newLeaf;
        checkComplete();
    }

    /**
     * Click Handler method to remove the last instance of the Clustering Panel Sub Panel class added to the Tree
     */
    private void removeSubgroup() 
    {
        GWT.log("count=" + itemCount + " removing subgroup " + (currentLeaf != null));
        if (currentLeaf != null)
        {
            TreeItem parent = currentLeaf.getParentItem();
            currentLeaf.remove();
            itemCount--;
            currentLeaf = parent;
            if (itemCount <= 0)
            {
                toggleClustering();
            }
            else if (itemCount == 2)
            {
                addSubgroupButton.setVisible(true);
            }
        }
        checkComplete();
    };


    /**
     * change() method is to check the text on the Button and perform the necessary actions
     */
    public void toggleClustering()
    {
        hasClustering = !hasClustering;
        addClusteringButton.setVisible(!hasClustering);
        removeClusteringButton.setVisible(hasClustering);
        addSubgroupButton.setVisible(hasClustering);
        removeSubgroupButton.setVisible(hasClustering);
        checkComplete();
    }

//    /**
//     *  Add Clustering Method initiates the Tree by adding a root node to the tree.
//     */
//    public void addClustering() 
//    {	
//        addSubgroup();
//        buttonPanel.setVisible(true);
//    }

    /**
     * checkComplete() method is to check if all the instances of Clustering Panel Sub Panel Class added to the tree are
     * validated or not and then activate the Next button in the Wizard Setup Panel 
     */
    private void checkComplete() 
    {
        // initialize to true
        complete = true;
        // set back to false if any subpanels are incomplete
        TreeItemIterator.traverseDepthFirst(clusteringTree, checkCompleteAction);
        if (complete)
            notifyComplete();
        else
            notifyInProgress();
        GWT.log("clustering screen is complete? " + complete);
//        
//        if (itemCount <= 0)
//        {
//            // no clustering, so user may continue
//            notifyComplete();
//        }
//        else
//        {
//            boolean complete = true;
//
//            for(int i = 0; i < itemCount; i++)
//            {
//                TreeItem item = clusteringTree.getItem(i);
//                GWT.log("count=" + itemCount + ", got item " + i);
//                if (item != null)
//                {
//                    ClusteringPanelSubPanel subpanel = (ClusteringPanelSubPanel) item.getWidget();
//                    if (!subpanel.checkComplete()) 
//                    {
//                        complete = false;
//                        break;
//                    }
//                }
//            }
//            if (complete)
//                notifyComplete();
//            else 
//                notifyInProgress();
//            GWT.log(System.currentTimeMillis() + " " + complete);
//        }
    }

    /**
     * Called when any of the subpanels change.  Allows us to check
     * if the panel is complete and the user can/cannot proceed 
     * forward
     */
    public void onChange(ChangeEvent e) 
    {
        checkComplete();
    }

    /**
     * Called when a user clicks in one of the subpanels.  No response required
     */
    public void onClick() 
    {
        // no action here
    }

    /**
     * Determine the overall status of the screen completion by checking
     * each subpanel.  If all subpanels are complete, then the screen is complete,
     * but if any subpanel is incomplete, then the screen is incomplete 
     * 
     * @param subpanel
     */
    private void updateComplete(ClusteringPanelSubPanel subpanel)
    {
        boolean subpanelComplete = subpanel.checkComplete();
        GWT.log("subpanel complete? " + subpanelComplete);
        if (complete)
        {
            complete = subpanelComplete;
        }
    }

    /**
     * Determine the overall status of the screen completion by checking
     * each subpanel.  If all subpanels are complete, then the screen is complete,
     * but if any subpanel is incomplete, then the screen is incomplete 
     * 
     * @param subpanel
     */
    private void buildClusteringNode(ClusteringPanelSubPanel subpanel, int nodeId, int parentId)
    {
        clusteringNodeList.add(subpanel.toClusterNode(nodeId, parentId));
    }
    
    /**
     * Clear the panel and reset to the "no clustering" state
     */
    @Override
    public void reset() 
    {
        clusteringTree.removeItems();
        clusteringNodeList.clear();
        if (hasClustering) toggleClustering();
        currentLeaf = null;
        itemCount = 0;
    }
    
    /**
     * Traverse the tree of cluster sub panels to build the clustering
     * domain object, and set the clustering object to the context
     */
    public void onExit()
    {
        clusteringNodeList.clear();
        TreeItemIterator.traverseDepthFirst(clusteringTree, buildClusteringObjectAction);
        studyDesignContext.setClustering(this, clusteringNodeList);
    }

    /**
     * This function is called when a study design is uploaded
     */
    public void onWizardContextLoad()
    {
        loadFromContext();
    }

    /**
     * This populates the screens based on the data in the study design uploaded.
     */
    public void loadFromContext()
    {
        reset();
        List<ClusterNode> clusterNodeArrayList = studyDesignContext.getStudyDesign().getClusteringTree();
        if (clusterNodeArrayList != null)
        {
            buttonPanel.setVisible(true);
            boolean first = true;
            for(ClusterNode clusterNode: clusterNodeArrayList)
            {
                if (first)
                {
                    addSubgroup();
                    toggleClustering();
                    first = false;
                }
                else
                {
                    addSubgroup();
                }

                ClusteringPanelSubPanel currentPanel = (ClusteringPanelSubPanel) currentLeaf.getWidget();
                currentPanel.setGroupingName(clusterNode.getGroupeName());
                currentPanel.setNumberOfGroups(clusterNode.getGroupeSize());
            }
        }
        checkComplete();
    }
}