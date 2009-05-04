/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModule;

/**
 * Main control panel for Natural Selection
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionControlPanel extends JPanel {

    // main panels
    private JPanel rightPanel;
    public GenerationChartCanvas generationCanvas;
    private LeftPanel leftPanel;

    // subpanels
    public ClimatePanel climatePanel;
    private JPanel selectionPanel;

    // buttons
    public JRadioButton wolvesButton;
    public JRadioButton foodButton;
    public JRadioButton noneButton;
    public JButton resetAllButton;

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
        PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( this.module.getClock() );
        createSelectionPanel();
        createRightPanel();
        leftPanel = new LeftPanel( this.model );
        generationCanvas = new GenerationChartCanvas( this.model );


        // keep track of the column for the gridbaglayout
        int column = 0;

        // the uglier layout code
        GridBagConstraints geneConstraints = new GridBagConstraints();
        geneConstraints.gridx = column++;
        geneConstraints.gridy = 0;
        geneConstraints.gridheight = 2;
        geneConstraints.anchor = GridBagConstraints.NORTHWEST;
        add( leftPanel, geneConstraints );

        GridBagConstraints generationConstraints = new GridBagConstraints();
        generationConstraints.gridx = column++;
        generationConstraints.gridy = 0;
        //generationConstraints.fill = GridBagConstraints.BOTH;
        generationConstraints.anchor = GridBagConstraints.NORTH;
        generationConstraints.weightx = 1.0;
        add( generationCanvas, generationConstraints );

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = column++;
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
        clockPanelConstraints.weighty = 1.0;
        clockPanelConstraints.anchor = GridBagConstraints.SOUTH;
        add( clockControlPanel, clockPanelConstraints );

        // color everything with the control panel's background color
        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        rightPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        selectionPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        wolvesButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        foodButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        noneButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    /**
     * Created the panel that allows the user to choose which selection factor they want
     */
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

        resetAllButton = new JButton( NaturalSelectionStrings.RESET_ALL );

        rightPanel.add( resetAllButton );
    }

    public void reset() {
        climatePanel.reset();
        selectDefaultSelectionFactor();
        generationCanvas.reset();
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
