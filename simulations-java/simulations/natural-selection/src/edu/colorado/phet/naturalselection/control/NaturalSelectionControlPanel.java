package edu.colorado.phet.naturalselection.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModule;

public class NaturalSelectionControlPanel extends JPanel implements ActionListener {

    public ClimatePanel climatePanel;
    public JRadioButton wolvesButton;
    public JRadioButton foodButton;
    public JRadioButton noneButton;
    private JButton resetAllButton;
    public TraitCanvas traitCanvas;
    private TimeDisplayPanel timePanel;
    private NaturalSelectionModel model;
    private NaturalSelectionModule module;
    private PopulationCanvas popCanvas;
    private PiccoloClockControlPanel clockControlPanel;

    public NaturalSelectionControlPanel( NaturalSelectionModule _module, NaturalSelectionModel _model ) {
        model = _model;
        module = _module;

        GridBagLayout layout = new GridBagLayout();
        this.setLayout( layout );

        traitCanvas = new TraitCanvas();

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.Y_AXIS ) );

        climatePanel = new ClimatePanel( model );

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout( new BoxLayout( selectionPanel, BoxLayout.Y_AXIS ) );
        selectionPanel.add( new JLabel( "Selection Factor" ) );
        wolvesButton = new JRadioButton( "Wolves" );
        selectionPanel.add( wolvesButton );
        foodButton = new JRadioButton( "Food" );
        selectionPanel.add( foodButton );
        noneButton = new JRadioButton( "None" );
        noneButton.setSelected( true );
        selectionPanel.add( noneButton );

        ButtonGroup group = new ButtonGroup();
        group.add( wolvesButton );
        group.add( foodButton );
        group.add( noneButton );

        resetAllButton = new JButton( "Reset All" );
        resetAllButton.addActionListener( this );

        rightPanel.add( climatePanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        rightPanel.add( selectionPanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        rightPanel.add( new JButton( "Generation Chart" ) );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
        rightPanel.add( resetAllButton );

        timePanel = new TimeDisplayPanel( model );
        popCanvas = new PopulationCanvas( model );
        JPanel timePopulationPanel = new JPanel();
        timePopulationPanel.setLayout( new GridBagLayout() );
        GridBagConstraints simpleConstraint = new GridBagConstraints();
        simpleConstraint.gridx = 0;
        simpleConstraint.gridy = 0;
        timePopulationPanel.add( timePanel, simpleConstraint );
        simpleConstraint.gridx = 0;
        simpleConstraint.gridy = 1;
        simpleConstraint.anchor = GridBagConstraints.SOUTHEAST;
        timePopulationPanel.add( popCanvas, simpleConstraint );

        clockControlPanel = new PiccoloClockControlPanel( module.getClock() );
        //clockControlPanel.setTimeDisplayVisible( true );
        //clockControlPanel.setUnits( "Units" );
        //clockControlPanel.setTimeColumns( 10 );

        int column = 0;

        GridBagConstraints traitCanvasConstraints = new GridBagConstraints();
        traitCanvasConstraints.gridx = column++;
        traitCanvasConstraints.gridy = 0;
        traitCanvasConstraints.gridwidth = 1;
        traitCanvasConstraints.gridheight = 1;
        traitCanvasConstraints.anchor = GridBagConstraints.WEST;
        traitCanvasConstraints.insets = new Insets( 10, 10, 10, 10 );
        //traitCanvasConstraints.weightx = 1.0;
        //traitCanvasConstraints.gridheight = 2;
        add( traitCanvas, traitCanvasConstraints );

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
        //spacerConstraints2.gridheight = 2;
        Component spacer2 = Box.createRigidArea( new Dimension( 40, 0 ) );
        add( spacer2, spacerConstraints2 );

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = column++;
        rightConstraints.gridy = 0;
        rightConstraints.gridwidth = 1;
        rightConstraints.gridheight = 1;
        rightConstraints.anchor = GridBagConstraints.EAST;
        rightConstraints.insets = new Insets( 10, 10, 10, 10 );
        //rightConstraints.gridheight = 2;
        add( rightPanel, rightConstraints );

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

    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == resetAllButton ) {
            traitCanvas.reset();
            climatePanel.reset();
            noneButton.setSelected( true );
            popCanvas.reset();
            module.reset();
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new NaturalSelectionControlPanel( null, null ) );
        frame.pack();
        frame.setVisible( true );
    }

}
