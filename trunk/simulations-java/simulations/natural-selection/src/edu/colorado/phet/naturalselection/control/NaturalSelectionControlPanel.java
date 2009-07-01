/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;

/**
 * Main control panel for Natural Selection
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionControlPanel extends JPanel {

    // main panels
    private JPanel rightPanel;
    //public GenerationChartCanvas generationCanvas;
    //public GenerationChartPanel generationPanel;
    public BunnyStatsPanel bunnyStatsPanel;
    private LeftPanel leftPanel;

    // subpanels
    public ClimatePanel climatePanel;
    public SelectionPanel selectionPanel;
    //private JPanel selectionPanel;

    // buttons
    //public JRadioButton wolvesButton;
    //public JRadioButton foodButton;
    //public JRadioButton noneButton;
    public JButton resetAllButton;
    public JButton showGenerationChartButton;

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
        climatePanel = new ClimatePanel( this.model );
        selectionPanel = new SelectionPanel( this.model );
        PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( this.module.getClock() );
        clockControlPanel.addBetweenTimeDisplayAndButtons( new GenerationProgressPanel( model ) );
        //createSelectionPanel();
        createRightPanel();
        leftPanel = new LeftPanel( this.model );
        //generationCanvas = new GenerationChartCanvas( this.model );
        //generationPanel = new GenerationChartPanel( this.model );
        bunnyStatsPanel = new BunnyStatsPanel( this.model );
        LogoPanel logoPanel = new LogoPanel();
        logoPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );


        // keep track of the column for the gridbaglayout
        int column = 0;

        // the uglier layout code
        GridBagConstraints geneConstraints = new GridBagConstraints();
        geneConstraints.gridx = column++;
        geneConstraints.gridy = 0;
        geneConstraints.gridheight = 2;
        geneConstraints.anchor = GridBagConstraints.NORTHWEST;
        add( leftPanel, geneConstraints );

        GridBagConstraints statsConstraints = new GridBagConstraints();
        statsConstraints.gridx = column++;
        statsConstraints.gridy = 0;
        statsConstraints.fill = GridBagConstraints.BOTH;
        statsConstraints.anchor = GridBagConstraints.NORTH;
        statsConstraints.weightx = 1.0;
        add( bunnyStatsPanel, statsConstraints );

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = column++;
        rightConstraints.gridy = 0;
        rightConstraints.anchor = GridBagConstraints.NORTHEAST;
        rightConstraints.gridheight = 1;
        rightConstraints.insets = new Insets( 10, 10, 10, 10 );
        add( rightPanel, rightConstraints );

        GridBagConstraints logoConstraints = new GridBagConstraints();
        logoConstraints.gridx = column - 1;
        logoConstraints.gridy = 1;
        logoConstraints.anchor = GridBagConstraints.SOUTHEAST;
        logoConstraints.gridheight = 2;
        logoConstraints.insets = new Insets( 5, 5, 5, 5 );
        add( logoPanel, logoConstraints );

        // space will be padded equally on each side of the clock
        GridBagConstraints clockPanelConstraints = new GridBagConstraints();
        clockPanelConstraints.gridx = 1;
        clockPanelConstraints.gridy = 1;
        clockPanelConstraints.weightx = 1.0;
        clockPanelConstraints.weighty = 1.0;
        clockPanelConstraints.anchor = GridBagConstraints.SOUTH;
        add( clockControlPanel, clockPanelConstraints );

        // color everything with the control panel's background color
        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        rightPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        selectionPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        //wolvesButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        //foodButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        //noneButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    /**
     * Created the panel that allows the user to choose which selection factor they want
     */
    /*
    public void createSelectionPanel() {
        selectionPanel = new JPanel();
        selectionPanel.setLayout( new BoxLayout( selectionPanel, BoxLayout.Y_AXIS ) );
        selectionPanel.add( new JLabel( NaturalSelectionStrings.SELECTION_FACTOR ) );
        wolvesButton = new JRadioButton( NaturalSelectionStrings.WOLVES );
        selectionPanel.add( wolvesButton );
        foodButton = new JRadioButton( NaturalSelectionStrings.FOOD );
        selectionPanel.add( foodButton );
        noneButton = new JRadioButton( NaturalSelectionStrings.NONE );
        selectionPanel.add( noneButton );

        ButtonGroup group = new ButtonGroup();
        group.add( wolvesButton );
        group.add( foodButton );
        group.add( noneButton );

        selectDefaultSelectionFactor();
    }
    */
    public void selectDefaultSelectionFactor() {
        selectionPanel.selectDefaultSelectionFactor();
        /*
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
        */
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

        resetAllButton = new JButton( NaturalSelectionStrings.RESET_ALL );
        rightPanel.add( resetAllButton );

        rightPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );

        showGenerationChartButton = new JButton( NaturalSelectionStrings.GENERATION_CHART );
        rightPanel.add( showGenerationChartButton );


    }

    public void reset() {
        climatePanel.reset();
        selectDefaultSelectionFactor();
        //generationPanel.reset();
        bunnyStatsPanel.reset();
        leftPanel.reset();
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
