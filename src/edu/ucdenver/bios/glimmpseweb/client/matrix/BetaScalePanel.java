/*
 * Web Interface for the GLIMMPSE Software System.  Allows
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
package edu.ucdenver.bios.glimmpseweb.client.matrix;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListEntryPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListValidator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;

/**
 * Matrix Mode panel which allows entry of beta-scale factors
 *
 */
public class BetaScalePanel extends WizardStepPanel
implements ListValidator
{
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	
   	// list of per group sample sizes
    protected ListEntryPanel betaScaleListPanel =
    	new ListEntryPanel(GlimmpseWeb.constants.betaScaleTableColumn(), this);
    
	public BetaScalePanel(WizardContext context)
	{
		super(context, GlimmpseWeb.constants.navItemBetaScale());
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(GlimmpseWeb.constants.betaScaleTitle());
        HTML description = new HTML(GlimmpseWeb.constants.betaScaleDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(betaScaleListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		betaScaleListPanel.reset();
	}

    /**
     * Skip this panel if the user is solving for detectable difference
     */
	@Override
	public void onWizardContextChange(WizardContextChangeEvent e)
	{
    	StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
    	switch (changeEvent.getType())
    	{
    	case SOLVING_FOR:
    		if (SolutionTypeEnum.DETECTABLE_DIFFERENCE == 
    			studyDesignContext.getStudyDesign().getSolutionTypeEnum())
    		{
    			changeState(WizardStepPanelState.SKIPPED);
    		}
    		else
    		{
    		    checkComplete();
    		}
    		break;
    	case BETA_SCALE_LIST:
    		if (this != e.getSource())
    		{
    			loadFromContext();
    		}
    		break;
    	}
	}
	
	/**
	 * Load the beta scale list from the context
	 */
	@Override
	public void onWizardContextLoad()
	{
		loadFromContext();
	}
	
    /**
     * Load the beta scale panel from the study design context information
     */
	public void loadFromContext()
	{
	    List<BetaScale> contextBetaScaleList = studyDesignContext.getStudyDesign().getBetaScaleList();
	    betaScaleListPanel.reset();
	    if (contextBetaScaleList != null) {
	        for(BetaScale scale: contextBetaScaleList)
	        {
	            betaScaleListPanel.add(Double.toString(scale.getValue()));
	        }
	    }
	    if (SolutionTypeEnum.DETECTABLE_DIFFERENCE == 
	            studyDesignContext.getStudyDesign().getSolutionTypeEnum()) {
	        changeState(WizardStepPanelState.SKIPPED);
	    } else {
	        checkComplete();
	    }
	}

	/**
	 * Add a beta scale to the study design
	 */
    @Override
    public void onAdd(String value) throws IllegalArgumentException {
        try
        {
            double betaScale = TextValidation.parseDouble(value, 
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
            studyDesignContext.addBetaScale(this, betaScale);
            changeState(WizardStepPanelState.COMPLETE);
        }
        catch (NumberFormatException nfe)
        {
            throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidNumber());
        }
    }

    /**
     * Delete a beta scale from the study design
     */
    @Override
    public void onDelete(String value, int index) {
        double betaScale = TextValidation.parseDouble(value, 
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
        studyDesignContext.deleteBetaScale(this, betaScale, index);
        checkComplete();
    }

    /**
     * Check if the panel is complete
     */
    private void checkComplete() {
        if (betaScaleListPanel.getValidRowCount() > 0)
            changeState(WizardStepPanelState.COMPLETE);
        else
            changeState(WizardStepPanelState.INCOMPLETE);
    }
    
}
