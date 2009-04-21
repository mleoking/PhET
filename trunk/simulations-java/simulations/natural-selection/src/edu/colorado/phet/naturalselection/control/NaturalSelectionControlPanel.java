/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModule;

/**
 * Main control panel for Natural Selection
 * <p/>
 * Main panels are in the following order:
 * <p/>
 * trait panel: allows the user to add mutations, select the dominant trait, and view trait distributions
 * time panel: allows the user to start / stop the clock
 * time / population panel: shows the month and generation on top, and a bar chart of the number of bunnies below
 * right panel: contains the climate panel, selection panel, generation chart button and reset all button
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionControlPanel extends JPanel {

    // main panels
    public TraitCanvas traitCanvas;
    private TimeDisplayPanel timePanel;
    private JPanel timePopulationPanel;
    private JPanel rightPanel;

    // subpanels
    public ClimatePanel climatePanel;
    private PopulationCanvas popCanvas;
    private JPanel selectionPanel;

    // buttons
    public JRadioButton wolvesButton;
    public JRadioButton foodButton;
    public JRadioButton noneButton;
    public JButton resetAllButton;
    public JButton generationChartButton;

    // private variables
    private NaturalSelectionModel model;
    private NaturalSelectionModule module;

    /**
     * Constructor
     *
     * @param module The Natural Selection module
     * @param model  The corresponding model
     */
    public NaturalSelectionControlPanel( NaturalSelectionModule module, NaturalSelectionModel model ) {
        this.model = model;
        this.module = module;

        GridBagLayout layout = new GridBagLayout();
        this.setLayout( layout );

        // build all of the panels
        traitCanvas = new TraitCanvas();
        climatePanel = new ClimatePanel( this.model );
        PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( this.module.getClock() );
        createSelectionPanel();
        createRightPanel();
        createTimePopulationPanel();

        // keep track of the column for the gridbaglayout
        int column = 0;

        // the uglier layout code
        GridBagConstraints traitCanvasConstraints = new GridBagConstraints();
        traitCanvasConstraints.gridx = column++;
        traitCanvasConstraints.gridy = 0;
        traitCanvasConstraints.anchor = GridBagConstraints.WEST;
        traitCanvasConstraints.insets = new Insets( 10, 10, 10, 10 );
        add( traitCanvas, traitCanvasConstraints );

        // space will be padded equally on each side of the clock
        GridBagConstraints clockPanelConstraints = new GridBagConstraints();
        clockPanelConstraints.gridx = column++;
        clockPanelConstraints.gridy = 0;
        clockPanelConstraints.weightx = 1.0;
        add( clockControlPanel, clockPanelConstraints );

        GridBagConstraints popConstraints = new GridBagConstraints();
        popConstraints.gridx = column++;
        popConstraints.gridy = 0;
        add( timePopulationPanel, popConstraints );

        GridBagConstraints spacerConstraints2 = new GridBagConstraints();
        spacerConstraints2.gridx = column++;
        spacerConstraints2.gridy = 0;
        Component spacer2 = Box.createRigidArea( new Dimension( 40, 0 ) );
        add( spacer2, spacerConstraints2 );

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = column++;
        rightConstraints.gridy = 0;
        rightConstraints.anchor = GridBagConstraints.EAST;
        rightConstraints.insets = new Insets( 10, 10, 10, 10 );
        add( rightPanel, rightConstraints );

        // color everything with the control panel's background color
        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        spacer2.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        traitCanvas.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        rightPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        selectionPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        wolvesButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        foodButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        noneButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        timePopulationPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

    }

    /**
     * Create the panel that shows the population, current month and current generation
     */
    private void createTimePopulationPanel() {
        timePanel = new TimeDisplayPanel( model );
        popCanvas = new PopulationCanvas( model );
        timePopulationPanel = new JPanel();
        timePopulationPanel.setLayout( new GridBagLayout() );
        GridBagConstraints simpleConstraint = new GridBagConstraints();
        simpleConstraint.gridx = 0;
        simpleConstraint.gridy = 0;
        timePopulationPanel.add( timePanel, simpleConstraint );
        simpleConstraint.gridx = 0;
        simpleConstraint.gridy = 1;
        simpleConstraint.anchor = GridBagConstraints.SOUTHEAST;
        timePopulationPanel.add( popCanvas, simpleConstraint );
    }

    /**
     * Created the panel that allows the user to choose which selection factor they want
     */
    public void createSelectionPanel() {
        selectionPanel = new JPanel();
        selectionPanel.setLayout( new BoxLayout( selectionPanel, BoxLayout.Y_AXIS ) );
        selectionPanel.add( new JLabel( "Selection Factor" ) );
        wolvesButton = new JRadioButton( "Wolves" );
        selectionPanel.add( wolvesButton );
        foodButton = new JRadioButton( "Food" );
        selectionPanel.add( foodButton );
        noneButton = new JRadioButton( "None" );
        selectionPanel.add( noneButton );

        ButtonGroup group = new ButtonGroup();
        group.add( wolvesButton );
        group.add( foodButton );
        group.add( noneButton );

        selectDefaultSelectionFactor();
    }

    public void selectDefaultSelectionFactor() {
        int selection = NaturalSelectionDefaults.DEFAULT_SELECTION_FACTOR;
        switch( selection ) {
            case NaturalSelectionModel.SELECTION_NONE:
                noneButton.setSelected( true );
                break;
            case NaturalSelectionModel.SELECTION_FOOD:
                foodButton.setSelected( true );
                break;
            case NaturalSelectionModel.SELECTION_WOLVES:
                wolvesButton.setSelected( true );
                break;
        }
    }

    /**
     * Create the right-most panel that includes the climate panel (user can select the climate), the selection panel,
     * a button to show the generation chart, and the reset all button.
     */
    public void createRightPanel() {
        rightPanel = new JPanel();
        rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.Y_AXIS ) );
        rightPanel.add( climatePanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        rightPanel.add( selectionPanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );

        generationChartButton = new JButton( "Generation Chart" );

        rightPanel.add( generationChartButton );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );

        resetAllButton = new JButton( "Reset All" );

        rightPanel.add( resetAllButton );
    }

    public void reset() {
        traitCanvas.reset();
        climatePanel.reset();
        popCanvas.reset();
        selectDefaultSelectionFactor();
    }

    /**
     * Test main() function, probably won't work anymore
     *
     * @param args
     */
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new NaturalSelectionControlPanel( null, null ) );
        frame.pack();
        frame.setVisible( true );
    }

}
