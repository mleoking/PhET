package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.dialog.PedigreeChartCanvas;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
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
    private JButton resetAllButton;

    /**
     * Constructor
     *
     * @param module The Natural Selection module
     * @param model  The corresponding model
     */
    public NaturalSelectionControlPanel( NaturalSelectionModule module, NaturalSelectionModel model ) {

        GridBagLayout layout = new GridBagLayout();
        this.setLayout( layout );

        // build all of the panels
        climatePanel = new ClimatePanel();
        selectionPanel = new SelectionPanel();
        PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( module.getClock() );
        generationProgressPanel = new GenerationProgressPanel();
        clockControlPanel.addBetweenTimeDisplayAndButtons( generationProgressPanel );
        createRightPanel();
        leftPanel = new LeftPanel();
        bunnyStatsPanel = new BunnyStatsPanel( model );
        LogoPanel logoPanel = new LogoPanel();
        logoPanel.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        pedigreeChart = new PedigreeChartCanvas( model );
        detachPanel = new DetachOptionPanel( NaturalSelectionStrings.PEDIGREE_CHART, pedigreeChart, bunnyStatsPanel );
        switcherPanel = new SwitcherPanel();

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
        rightConstraints.gridheight = 1;
        rightConstraints.insets = new Insets( 10, 10, 10, 10 );
        add( rightPanel, rightConstraints );

        GridBagConstraints logoConstraints = new GridBagConstraints();
        logoConstraints.gridx = 3;
        logoConstraints.gridy = 1;
        logoConstraints.anchor = GridBagConstraints.SOUTHEAST;
        logoConstraints.gridheight = 1;
        logoConstraints.insets = new Insets( 5, 5, 5, 5 );
        add( logoPanel, logoConstraints );

        // space will be padded equally on each side of the clock
        GridBagConstraints clockPanelConstraints = new GridBagConstraints();
        clockPanelConstraints.gridx = 1;
        clockPanelConstraints.gridy = 1;
        clockPanelConstraints.weightx = 1.0;
        clockPanelConstraints.weighty = 0.0;
        clockPanelConstraints.anchor = GridBagConstraints.SOUTH;
        add( clockControlPanel, clockPanelConstraints );

        GridBagConstraints switcherConstraints = new GridBagConstraints();
        switcherConstraints.gridx = 2;
        switcherConstraints.gridy = 1;
        switcherConstraints.weightx = 0.0;
        switcherConstraints.weighty = 0.0;
        switcherConstraints.anchor = GridBagConstraints.EAST;
        add( switcherPanel, switcherConstraints );

        // color everything with the control panel's background color
        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        rightPanel.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        selectionPanel.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );

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
        rightPanel.add( climatePanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        rightPanel.add( selectionPanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );

        resetAllButton = new JButton( NaturalSelectionStrings.RESET_ALL );
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

    public JButton getResetAllButton() {
        return resetAllButton;
    }

    public GenerationProgressPanel getGenerationProgressPanel() {
        return generationProgressPanel;
    }
}
