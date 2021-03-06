/*
 * User Interface for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 * 
 * Copyright (C) 2011 Regents of the University of Colorado.  
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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ExplanationButton;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

/**
 * Hypothesis selection panel
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class HypothesisPanel extends WizardStepPanel {
    // radio group for hypothesis type selection
    private static final String HYPOTHESIS_RADIO_GROUP = "hypothesisRadioGroup";
    // indices in the deck panel for hypothesis subpanels
    private static final int GRAND_MEAN_INDEX = 0;
    private static final int MAIN_EFFECT_INDEX = 1;
    private static final int TREND_INDEX = 2;
    private static final int INTERACTION_INDEX = 3;
    private static final int BLANK_INDEX = 4;

    // context object
    protected StudyDesignContext studyDesignContext = (StudyDesignContext) context;

    // subpanels for each type of hypothesis
    protected GrandMeanHypothesisPanel grandMeanHypothesisPanel =
        new GrandMeanHypothesisPanel(studyDesignContext, this);
    protected MainEffectHypothesisPanel mainEffectHypothesisPanel =
        new MainEffectHypothesisPanel(studyDesignContext, this);
    protected InteractionHypothesisPanel interactionHypothesisPanel =
        new InteractionHypothesisPanel(studyDesignContext, this);
    protected TrendHypothesisPanel trendHypothesisPanel =
        new TrendHypothesisPanel(studyDesignContext, this);

    /* hypothesis type buttons */
    protected RadioButton grandMeanRadioButton = 
        new RadioButton(HYPOTHESIS_RADIO_GROUP,
                GlimmpseWeb.constants.hypothesisPanelGrandMean());
    protected RadioButton mainEffectRadioButton = 
        new RadioButton(HYPOTHESIS_RADIO_GROUP,
                GlimmpseWeb.constants.hypothesisPanelMainEffect());
    protected RadioButton trendRadioButton = 
        new RadioButton(HYPOTHESIS_RADIO_GROUP,
                GlimmpseWeb.constants.hypothesisPanelTrend());
    protected RadioButton interactionRadioButton = 
        new RadioButton(HYPOTHESIS_RADIO_GROUP,
                GlimmpseWeb.constants.hypothesisPanelInteraction());

    // containers for the radio button and explanation button
    protected HorizontalPanel grandMeanSelectContainer = 
        new HorizontalPanel();
    protected HorizontalPanel mainEffectSelectContainer = 
        new HorizontalPanel();
    protected HorizontalPanel trendSelectContainer = 
        new HorizontalPanel();
    protected HorizontalPanel interactionSelectContainer = 
        new HorizontalPanel();

    // Deck panel to organize the hypothesis sub screens
    protected DeckPanel deckPanel = new DeckPanel();

    /**
     * Constructor
     * @param context wizard context
     */
    public HypothesisPanel(WizardContext context)
    {		
        super(context, GlimmpseWeb.constants.navItemHypothesis(),
                WizardStepPanelState.NOT_ALLOWED);

        VerticalPanel panel = new VerticalPanel();

        // descriptive text
        HTML title = new HTML(
                GlimmpseWeb.constants.hypothesisTitle());
        HTML description = new HTML(
                GlimmpseWeb.constants.hypothesisDescription());

        // hypothesis type selection panel
        HorizontalPanel typeContainer = new HorizontalPanel();
        // grand mean selection
        grandMeanSelectContainer.add(grandMeanRadioButton);
        grandMeanSelectContainer.add(new ExplanationButton("",
                GlimmpseWeb.constants.hypothesisPanelGrandMeanExplanation()));
        typeContainer.add(grandMeanSelectContainer);
        grandMeanRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateHypothesisType(HypothesisTypeEnum.GRAND_MEAN);
            }   
        });
        // main effect selection
        mainEffectSelectContainer.add(mainEffectRadioButton);
        mainEffectSelectContainer.add(new ExplanationButton("",
                GlimmpseWeb.constants.hypothesisPanelMainEffectExplanation()));
        typeContainer.add(mainEffectSelectContainer);
        mainEffectRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateHypothesisType(HypothesisTypeEnum.MAIN_EFFECT);
            }   
        });
        // trend selection
        trendSelectContainer.add(trendRadioButton);
        trendSelectContainer.add(new ExplanationButton("",
                GlimmpseWeb.constants.hypothesisPanelTrendExplanation()));
        typeContainer.add(trendSelectContainer);
        trendRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateHypothesisType(HypothesisTypeEnum.TREND);
            }   
        });
        // interaction
        interactionSelectContainer.add(interactionRadioButton);
        interactionSelectContainer.add(new ExplanationButton("",
                GlimmpseWeb.constants.hypothesisPanelInteractionExplanation()));
        typeContainer.add(interactionSelectContainer);
        interactionRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateHypothesisType(HypothesisTypeEnum.INTERACTION);
            }   
        });

        // fill the deck panel with widgets for each type of hypothesis
        deckPanel.add(grandMeanHypothesisPanel);
        deckPanel.add(mainEffectHypothesisPanel);
        deckPanel.add(trendHypothesisPanel);
        deckPanel.add(interactionHypothesisPanel);
        // add a blank so we can hide all of the panels
        deckPanel.add(new VerticalPanel());
        // show the blank by default
        deckPanel.showWidget(BLANK_INDEX);

        VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.add(deckPanel);

        // layout panel
        panel.add(title);
        panel.add(description);
        panel.add(typeContainer);
        panel.add(contentPanel);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        title.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        typeContainer.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DECK_PANEL_BAR);
        contentPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DECK_PANEL_CONTENTS);

        // initialize
        updateHypothesisOptions();
        initWidget(panel);
    }

    /**
     * Reset the panel
     */
    @Override
    public void reset()
    {
        changeState(WizardStepPanelState.NOT_ALLOWED);
    }

    /**
     * Respond to a change in the context object
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        switch(((StudyDesignChangeEvent) e).getType()) {
        case RESPONSES_LIST:
            grandMeanHypothesisPanel.loadResponseList(
                    studyDesignContext.getStudyDesign().getResponseList());
            updateHypothesisOptions();
            loadHypothesisFromContext();
            break;
        case BETWEEN_PARTICIPANT_FACTORS:
            List<BetweenParticipantFactor> factorList = 
                studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
            mainEffectHypothesisPanel.loadBetweenParticipantFactors(factorList);
            interactionHypothesisPanel.loadBetweenParticipantFactors(factorList);
            trendHypothesisPanel.loadBetweenParticipantFactors(factorList);
            updateHypothesisOptions();
            loadHypothesisFromContext();
            break;
        case REPEATED_MEASURES:
            List<RepeatedMeasuresNode> rmNodeList = 
                studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
            mainEffectHypothesisPanel.loadRepeatedMeasures(rmNodeList);
            interactionHypothesisPanel.loadRepeatedMeasures(rmNodeList);
            trendHypothesisPanel.loadRepeatedMeasures(rmNodeList);
            updateHypothesisOptions();
            loadHypothesisFromContext();
            break;
        case HYPOTHESIS:
            if (e.getSource() != this) {
                loadFromContext();
            }
        }

        checkComplete();
    };

    /**
     * Show or hide specific hypothesis classes depending on the 
     * number of factors, etc.
     */
    private void updateHypothesisOptions() {
        // get the number of between and within factors, and response variables
        int totalValidFactors = 
            studyDesignContext.getValidBetweenParticipantFactorCount() +
            studyDesignContext.getValidRepeatedMeasuresFactorCount();
        // hide or show the appropriate hypothesis options
        mainEffectSelectContainer.setVisible(totalValidFactors > 0);
        trendSelectContainer.setVisible(totalValidFactors > 0);
        interactionSelectContainer.setVisible(totalValidFactors > 1);

        // make sure we are not showing a subpanel for a hidden radio button
        switch (deckPanel.getVisibleWidget()) {
        case MAIN_EFFECT_INDEX:
            if (!mainEffectSelectContainer.isVisible()) {
                mainEffectRadioButton.setValue(false);
                deckPanel.showWidget(BLANK_INDEX);
            }
            break;
        case TREND_INDEX:
            if (!trendSelectContainer.isVisible()) {
                trendRadioButton.setValue(false);
                deckPanel.showWidget(BLANK_INDEX);
            }
            break;
        case INTERACTION_INDEX:
            if (!interactionSelectContainer.isVisible()) {
                interactionRadioButton.setValue(false);
                deckPanel.showWidget(BLANK_INDEX);
            }
            break;
        }
    }

    /**
     * Update the screen when the predictors or repeated measures change
     */
    @Override
    public void onWizardContextLoad() 
    {
        loadFromContext();
        checkComplete();
    }

    /**
     * Load the hypothesis panel from the context
     */
    private void loadFromContext() {
        // load the responses
        List<ResponseNode> responsesList = 
            studyDesignContext.getStudyDesign().getResponseList();
        grandMeanHypothesisPanel.loadResponseList(responsesList);
        // load the between participant factors
        List<BetweenParticipantFactor> factorList = 
            studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
        mainEffectHypothesisPanel.loadBetweenParticipantFactors(factorList);
        interactionHypothesisPanel.loadBetweenParticipantFactors(factorList);
        trendHypothesisPanel.loadBetweenParticipantFactors(factorList);
        // load the repeated measures
        List<RepeatedMeasuresNode> rmNodeList = 
            studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
        mainEffectHypothesisPanel.loadRepeatedMeasures(rmNodeList);
        interactionHypothesisPanel.loadRepeatedMeasures(rmNodeList);
        trendHypothesisPanel.loadRepeatedMeasures(rmNodeList);
        // set the available hypotheses
        updateHypothesisOptions();  
        // load the hypothesis into the appropriate subpanel
        loadHypothesisFromContext();
    }

    /**
     * load the appropriate subpanel from the hypothesis in the
     * study design context
     */
    private void loadHypothesisFromContext() {
        Hypothesis hypothesis = studyDesignContext.getPrimaryHypothesis();
        if (hypothesis != null && hypothesis.getType() != null) {
            switch(hypothesis.getType()) {
            case GRAND_MEAN:
                grandMeanHypothesisPanel.loadHypothesis(
                        studyDesignContext.getStudyDesign().getNamedMatrix(
                                GlimmpseConstants.MATRIX_THETA));
                grandMeanRadioButton.setValue(true);
                deckPanel.showWidget(GRAND_MEAN_INDEX);
                break;
            case MAIN_EFFECT:
                mainEffectHypothesisPanel.loadHypothesis(hypothesis);
                mainEffectRadioButton.setValue(true);
                deckPanel.showWidget(MAIN_EFFECT_INDEX);
                break;
            case TREND:
                trendHypothesisPanel.loadHypothesis(hypothesis);
                trendRadioButton.setValue(true);
                deckPanel.showWidget(TREND_INDEX);
                break;
            case INTERACTION:
                interactionHypothesisPanel.loadHypothesis(hypothesis);
                interactionRadioButton.setValue(true);
                deckPanel.showWidget(INTERACTION_INDEX);
                break;
            }
        }
    }

    /**
     * Check if the hypothesis is completely specified
     */
    public void checkComplete() {
        if (studyDesignContext.getValidResponseVariableCount() > 0) {
            // get the currently visible widget
            if ((grandMeanRadioButton.getValue() &&
                        grandMeanHypothesisPanel.checkComplete()) ||
                        (mainEffectRadioButton.getValue() &&
                                mainEffectHypothesisPanel.checkComplete()) ||
                                (trendRadioButton.getValue() &&
                                        trendHypothesisPanel.checkComplete()) ||
                                        (interactionRadioButton.getValue() &&
                                                interactionHypothesisPanel.checkComplete())) {
                changeState(WizardStepPanelState.COMPLETE);
            } else {
                changeState(WizardStepPanelState.INCOMPLETE);
            }
        } else {
            changeState(WizardStepPanelState.NOT_ALLOWED);
        }
    }

    /**
     * Save the new hypothesis type to the context
     * @param type
     */
    private void updateHypothesisType(HypothesisTypeEnum type) {
        studyDesignContext.setHypothesisType(this, type);
        switch(type) {
        case GRAND_MEAN:
            deckPanel.showWidget(GRAND_MEAN_INDEX);
            grandMeanHypothesisPanel.syncStudyDesign();
            break;
        case MAIN_EFFECT:
            deckPanel.showWidget(MAIN_EFFECT_INDEX);
            mainEffectHypothesisPanel.syncStudyDesign();
            break;
        case TREND:
            deckPanel.showWidget(TREND_INDEX);
            trendHypothesisPanel.syncStudyDesign();
            break;
        case INTERACTION:
            deckPanel.showWidget(INTERACTION_INDEX);
            interactionHypothesisPanel.syncStudyDesign();
            break;
        }

        checkComplete();
    }

}
