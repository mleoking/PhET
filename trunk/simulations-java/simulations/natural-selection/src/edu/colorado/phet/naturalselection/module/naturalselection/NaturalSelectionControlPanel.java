package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

import edu.colorado.phet.naturalselection.util.ImagePanel;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

public class NaturalSelectionControlPanel extends JPanel implements ActionListener {
    
    private ClimatePanel climatePanel;
    private JRadioButton wolvesButton;
    private JRadioButton foodButton;
    private JRadioButton noneButton;
    private JButton resetAllButton;
    private TraitCanvas traitCanvas;

    public NaturalSelectionControlPanel() {



        GridBagLayout layout = new GridBagLayout();
        this.setLayout( layout );

        traitCanvas = new TraitCanvas();

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.Y_AXIS ) );

        climatePanel = new ClimatePanel();

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

        PopulationCanvas popCanvas = new PopulationCanvas();

        GridBagConstraints traitCanvasConstraints = new GridBagConstraints();
        traitCanvasConstraints.gridx = 0;
        traitCanvasConstraints.gridy = 0;
        traitCanvasConstraints.gridwidth = 1;
        traitCanvasConstraints.gridheight = 1;
        traitCanvasConstraints.anchor = GridBagConstraints.WEST;
        traitCanvasConstraints.insets = new Insets( 10, 10, 10, 10 );
        traitCanvasConstraints.weightx = 1.0;
        add( traitCanvas, traitCanvasConstraints );

        GridBagConstraints popConstraints = new GridBagConstraints();
        popConstraints.gridx = 2;
        popConstraints.gridy = 0;
        add( popCanvas, popConstraints );

        GridBagConstraints spacerConstraints2 = new GridBagConstraints();
        spacerConstraints2.gridx = 1;
        spacerConstraints2.gridy = 0;
        Component spacer2 = Box.createRigidArea( new Dimension( 40, 0 ) );
        add( spacer2, spacerConstraints2 );

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = 4;
        rightConstraints.gridy = 0;
        rightConstraints.gridwidth = 1;
        rightConstraints.gridheight = 1;
        rightConstraints.anchor = GridBagConstraints.EAST;
        rightConstraints.insets = new Insets( 10, 10, 10, 10 );
        add( rightPanel, rightConstraints );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        spacer2.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        traitCanvas.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        rightPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        selectionPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        wolvesButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        foodButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        noneButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

    }

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == resetAllButton ) {
            traitCanvas.reset();
            climatePanel.reset();
            noneButton.setSelected( true );
        }
    }

    public static void main( String[] args ) {
        JFrame frame=new JFrame( );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new NaturalSelectionControlPanel() );
        frame.pack();
        frame.setVisible( true );
    }

}
