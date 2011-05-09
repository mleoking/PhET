// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.dialog.PedigreeChartCanvas;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;

/**
 * Main control panel for Natural Selection
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionControlPanel extends JPanel {

    // main panels
    private JPanel rightPanel;
    private BunnyStatsPanel bunnyStatsPanel;
    private LeftPanel leftPanel;
    private DetachOptionPanel detachPanel;
    private PedigreeChartCanvas pedigreeChart;
    private SwitcherPanel switcherPanel;
    private ClimatePanel climatePanel;
    private SelectionPanel selectionPanel;
    private GenerationProgressPanel generationProgressPanel;

    // buttons
    private ResetAllButton resetAllButton;

    /**
     * Constructor
     *
     * @param module The Natural Selection module
     */
    public NaturalSelectionControlPanel( NaturalSelectionModule module ) {

        GridBagLayout layout = new GridBagLayout();
        this.setLayout( layout );

        // build all of the panels
        switcherPanel = new SwitcherPanel();
        climatePanel = new ClimatePanel();
        selectionPanel = new SelectionPanel();
        PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( module.getClock() );
        clockControlPanel.setEnableStepWhileRunning( true );
        generationProgressPanel = new GenerationProgressPanel();
        clockControlPanel.addBetweenTimeDisplayAndButtons( generationProgressPanel );
        createRightPanel();
        leftPanel = new LeftPanel();
        bunnyStatsPanel = new BunnyStatsPanel();
        LogoPanel logoPanel = new LogoPanel();
        logoPanel.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        pedigreeChart = new PedigreeChartCanvas();
        detachPanel = new DetachOptionPanel( NaturalSelectionStrings.PEDIGREE_CHART, pedigreeChart, bunnyStatsPanel );

        // the uglier layout code

        GridBagConstraints geneConstraints = new GridBagConstraints();
        geneConstraints.gridx = 0;
        geneConstraints.gridy = 0;
        geneConstraints.gridheight = 2;
        geneConstraints.anchor = GridBagConstraints.NORTHWEST;
        add( leftPanel, geneConstraints );

        GridBagConstraints statsConstraints = new GridBagConstraints();
        statsConstraints.gridx = 1;
        statsConstraints.gridy = 0;
        statsConstraints.gridwidth = 2;
        statsConstraints.fill = GridBagConstraints.BOTH;
        statsConstraints.anchor = GridBagConstraints.NORTH;
        statsConstraints.weightx = 1.0;
        statsConstraints.weighty = 1.0;
        add( detachPanel, statsConstraints );

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = 3;
        rightConstraints.gridy = 0;
        rightConstraints.anchor = GridBagConstraints.NORTHEAST;
        rightConstraints.gridheight = 2;
        rightConstraints.insets = new Insets( 10, 10, 10, 10 );
        add( rightPanel, rightConstraints );

        // space will be padded equally on each side of the clock
        GridBagConstraints clockPanelConstraints = new GridBagConstraints();
        clockPanelConstraints.gridx = 1;
        clockPanelConstraints.gridy = 1;
        clockPanelConstraints.weightx = 1.0;
        clockPanelConstraints.weighty = 0.0;
        clockPanelConstraints.anchor = GridBagConstraints.SOUTH;
        add( clockControlPanel, clockPanelConstraints );

        GridBagConstraints logoConstraints = new GridBagConstraints();
        logoConstraints.gridx = 2;
        logoConstraints.gridy = 1;
        logoConstraints.weightx = 0.0;
        logoConstraints.weighty = 0.0;
        logoConstraints.anchor = GridBagConstraints.EAST;
        add( logoPanel, logoConstraints );

        // color everything with the control panel's background color
        setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        rightPanel.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        selectionPanel.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );

        // make sure that if the pedigree chart is resized, that it recenters itself
        pedigreeChart.addComponentListener( new ComponentListener() {
            public void componentResized( ComponentEvent componentEvent ) {
                pedigreeChart.setCenterPoint( 0 );
            }

            public void componentMoved( ComponentEvent componentEvent ) {

            }

            public void componentShown( ComponentEvent componentEvent ) {

            }

            public void componentHidden( ComponentEvent componentEvent ) {

            }
        } );
    }

    public void selectDefaultSelectionFactor() {
        selectionPanel.selectDefaultSelectionFactor();
    }

    /**
     * Create the right-most panel that includes the climate panel (user can select the climate), the selection panel,
     * a button to show the generation chart, and the reset all button.
     */
    private void createRightPanel() {
        rightPanel = new JPanel();
        rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.Y_AXIS ) );
        rightPanel.add( selectionPanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        rightPanel.add( climatePanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        rightPanel.add( switcherPanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );

        resetAllButton = new ResetAllButton( this );
        rightPanel.add( resetAllButton );

        rightPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );
    }

    public void reset() {
        climatePanel.reset();
        selectDefaultSelectionFactor();
        bunnyStatsPanel.reset();
        leftPanel.reset();
        pedigreeChart.reset();
    }

    public void load( NaturalSelectionConfig config ) {
        leftPanel.load( config );
        climatePanel.load( config );
        selectionPanel.load( config );
    }

    public void save( NaturalSelectionConfig config ) {
        leftPanel.save( config );
    }

    public PedigreeChartCanvas getPedigreeChart() {
        return pedigreeChart;
    }

    public MutationPanel getMutationPanel() {
        return leftPanel.getMutationPanel();
    }

    public GenePanel getGenePanel() {
        return leftPanel.getGenePanel();
    }

    public DetachOptionPanel getDetachPanel() {
        return detachPanel;
    }

    public SwitcherPanel getSwitcherPanel() {
        return switcherPanel;
    }

    public BunnyStatsPanel getBunnyStatsPanel() {
        return bunnyStatsPanel;
    }

    public ClimatePanel getClimatePanel() {
        return climatePanel;
    }

    public SelectionPanel getSelectionPanel() {
        return selectionPanel;
    }

    public ResetAllButton getResetAllButton() {
        return resetAllButton;
    }

    public GenerationProgressPanel getGenerationProgressPanel() {
        return generationProgressPanel;
    }
}
