package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.util.ImagePanel;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

public class NaturalSelectionControlPanel extends JPanel {
    
    private ClimatePanel climatePanel;
    private JRadioButton wolvesButton;
    private JRadioButton foodButton;
    private JRadioButton noneButton;

    public NaturalSelectionControlPanel() {



        GridBagLayout layout = new GridBagLayout();
        this.setLayout( layout );



        ImagePanel mutationPanel = new ImagePanel( "mock-panel.png" );
        mutationPanel.setPreferredSize( new Dimension( 471, 272 ) );

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

        rightPanel.add( climatePanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 20 ) ) );
        rightPanel.add( selectionPanel );
        rightPanel.add( Box.createRigidArea( new Dimension( 0, 20 ) ) );
        rightPanel.add( new JButton( "Generation Chart" ) );

        PopulationCanvas popCanvas = new PopulationCanvas();

        GridBagConstraints mutationPanelConstraints = new GridBagConstraints();
        mutationPanelConstraints.gridx = 0;
        mutationPanelConstraints.gridy = 0;
        mutationPanelConstraints.gridwidth = 1;
        mutationPanelConstraints.gridheight = 1;
        mutationPanelConstraints.anchor = GridBagConstraints.WEST;
        add( mutationPanel, mutationPanelConstraints );

        GridBagConstraints spacerConstraints = new GridBagConstraints();
        spacerConstraints.gridx = 1;
        spacerConstraints.gridy = 0;
        Component spacer = Box.createRigidArea( new Dimension( 200, 0 ) );
        add( spacer, spacerConstraints );

        GridBagConstraints popConstraints = new GridBagConstraints();
        popConstraints.gridx = 2;
        popConstraints.gridy = 0;
        add( popCanvas, popConstraints );

        GridBagConstraints spacerConstraints2 = new GridBagConstraints();
        spacerConstraints2.gridx = 1;
        spacerConstraints2.gridy = 0;
        Component spacer2 = Box.createRigidArea( new Dimension( 20, 0 ) );
        add( spacer2, spacerConstraints2 );

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = 4;
        rightConstraints.gridy = 0;
        rightConstraints.gridwidth = 1;
        rightConstraints.gridheight = 1;
        rightConstraints.anchor = GridBagConstraints.EAST;
        add( rightPanel, rightConstraints );

        setBackground( new Color( 0xC9E5C6 ) );

        spacer.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        spacer2.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        mutationPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        rightPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        selectionPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

    }

    public static void main( String[] args ) {
        JFrame frame=new JFrame( );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new NaturalSelectionControlPanel() );
        frame.pack();
        frame.setVisible( true );
    }

}
