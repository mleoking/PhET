/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.dialog.PedigreeChartCanvas;
import edu.colorado.phet.naturalselection.model.Bunny;
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
    public BunnyStatsPanel bunnyStatsPanel;
    private LeftPanel leftPanel;

    // subpanels
    public ClimatePanel climatePanel;
    public SelectionPanel selectionPanel;

    // buttons
    public JButton resetAllButton;
    public JButton showGenerationChartButton;

    // private variables
    private NaturalSelectionModel model;
    private NaturalSelectionModule module;

    private DetachOptionPanel detachPanel;
    private PedigreeChartCanvas pedigreeChart;


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
        createRightPanel();
        leftPanel = new LeftPanel( this.model );
        bunnyStatsPanel = new BunnyStatsPanel( this.model );
        LogoPanel logoPanel = new LogoPanel();
        logoPanel.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );

        pedigreeChart = new PedigreeChartCanvas( model );

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
        detachPanel = new DetachOptionPanel( "Pedigree Chart", pedigreeChart, bunnyStatsPanel );
        //detachPanel = new DetachOptionPanel( "Pedigree Chart", DetachOptionPanel.createExampleCanvas(), new JLabel( "Placeholder" ) );


        //detachPanel.setBorder( new LineBorder( Color.RED ) );
        //pedigreeChart.setBorder( new LineBorder( Color.GREEN ) );
        //clockControlPanel.setBorder( new LineBorder( Color.BLUE ) );

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
        switcherConstraints.anchor = GridBagConstraints.SOUTH;
        add( getSwitcherPanel(), switcherConstraints );
        //add( new JLabel( "test" ), switcherConstraints );

        // color everything with the control panel's background color
        setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        rightPanel.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        selectionPanel.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
    }

    private JPanel getSwitcherPanel() {
        JPanel container = new JPanel( new GridLayout( 2, 1 ) );
        container.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        final JRadioButton radioStats = new JRadioButton( "Placeholder" );
        final JRadioButton radioPedigree = new JRadioButton( "Pedigree" );
        radioStats.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );
        radioPedigree.setBackground( NaturalSelectionApplication.accessibleColor( NaturalSelectionConstants.COLOR_CONTROL_PANEL ) );

        ButtonGroup group = new ButtonGroup();
        group.add( radioStats );
        group.add( radioPedigree );

        radioStats.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                //System.out.println( "Placeholder!" );
                detachPanel.setPlaceholderVisible();
                if ( Bunny.getSelectedBunny() != null ) {
                    Bunny.getSelectedBunny().setSelected( false );
                }
            }
        } );

        radioPedigree.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                //System.out.println( "Pedigree!" );
                detachPanel.setChildVisible();
                if ( pedigreeChart.getLastDisplayedBunny() != null ) {
                    if ( Bunny.getSelectedBunny() == null && pedigreeChart.getLastDisplayedBunny() != null ) {
                        pedigreeChart.getLastDisplayedBunny().setSelected( true );
                    }
                }
            }
        } );

        detachPanel.addListener( new DetachOptionPanel.Listener() {
            public void onDock() {
                radioStats.setEnabled( true );
                radioPedigree.setEnabled( true );
                radioPedigree.setSelected( true );
            }

            public void onUndock() {
                radioStats.setEnabled( false );
                radioPedigree.setEnabled( false );
                radioStats.setSelected( true );
            }

            public void onClose() {
                radioStats.setEnabled( true );
                radioPedigree.setEnabled( true );
            }
        } );

        container.add( radioStats );
        container.add( radioPedigree );

        radioStats.setSelected( true );

        return container;
    }

    public void selectDefaultSelectionFactor() {
        selectionPanel.selectDefaultSelectionFactor();
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

        //showGenerationChartButton = new JButton( NaturalSelectionStrings.GENERATION_CHART );
        //rightPanel.add( showGenerationChartButton );


    }

    public void reset() {
        climatePanel.reset();
        selectDefaultSelectionFactor();
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
}
