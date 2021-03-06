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
package edu.ucdenver.bios.glimmpseweb.client.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;

/**
 * Matrix mode panel for entering the covariance of random errors
 * 
 * @author Sarah Kreidler
 *
 */
public class SigmaErrorMatrixPanel extends WizardStepPanel
{
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	
    protected ResizableMatrixPanel sigmaError = 
    	new ResizableMatrixPanel(GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P, true, true, true, true); 
    
    public SigmaErrorMatrixPanel(WizardContext context)
    {
    	// regardless of input, forward navigation is allowed from this panel
		super(context, GlimmpseWeb.constants.navItemSigmaErrorMatrix(), 
				WizardStepPanelState.COMPLETE);
		
        HTML header = new HTML(GlimmpseWeb.constants.sigmaErrorTitle());
        HTML description = new HTML(GlimmpseWeb.constants.sigmaErrorDescription());
		VerticalPanel panel = new VerticalPanel();
		
        panel.add(header);
        panel.add(description);
		panel.add(sigmaError);        
        
		// disable resize
		sigmaError.setEnabledColumnDimension(false);
		sigmaError.setEnabledRowDimension(false);
		
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
    }
    
	@Override
	public void reset()
	{
		sigmaError.reset(GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_P);
		changeState(WizardStepPanelState.COMPLETE);
	}

	/**
	 * Respond to a context change - resize the matrix to conform to the 
	 * beta matrix
	 */
	@Override
	public void onWizardContextChange(WizardContextChangeEvent e)
	{
    	StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
    	switch (changeEvent.getType())
    	{
    	case COVARIATE:
    		if (studyDesignContext.getStudyDesign().isGaussianCovariate())
    			changeState(WizardStepPanelState.SKIPPED);
    		else
    			changeState(WizardStepPanelState.COMPLETE);    			
    		break;
    	case BETA_MATRIX:
    		int betaColumns = 
    			studyDesignContext.getStudyDesign().getNamedMatrix(GlimmpseConstants.MATRIX_BETA).getColumns();
			sigmaError.setRowDimension(betaColumns);
    		break;
    	}
	}

	/**
	 * Load the sigma error matrix from the context
	 */
	@Override
	public void onWizardContextLoad()
	{
    	NamedMatrix contextSigmaError = 
    		studyDesignContext.getStudyDesign().getNamedMatrix(GlimmpseConstants.MATRIX_SIGMA_ERROR);
    	sigmaError.loadFromNamedMatrix(contextSigmaError);
	}

	/**
	 * Set the sigma error matrix in the context
	 */
    @Override 
    public void onExit()
    {
    	studyDesignContext.setSigmaCovariate(this, 
    			sigmaError.toNamedMatrix(GlimmpseConstants.MATRIX_SIGMA_ERROR));
    }
	

}
