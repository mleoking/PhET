package edu.colorado.phet.acidbasesolutions.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * 
 * Demonstrates a layout problem with HTML check boxes that have dynamic labels.
 * Click the radio buttons in the control panel a few times to see the issue.
 * The panel in the play area will become wider to accommodate the wider label,
 * but the label will be wrapped to the panel's previous width.
 * 
 * Observed on Mac OS 10.5.6 with Java 1.5.0_16, untested on Windows.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestJComboBoxUpdate extends JFrame {
    
    public TestJComboBoxUpdate() {
        
        /*
         * Swing control panel embedded in a Piccolo canvas using PSwing.
         * The red check box will have it's label changed dynamically.
         */
        
        JPanel playAreaPanel = new JPanel();
        playAreaPanel.setLayout( new BoxLayout( playAreaPanel, BoxLayout.Y_AXIS ) );
        TitledBorder border = new TitledBorder( new LineBorder( Color.BLACK, 1 ), "View controls" );
        playAreaPanel.setBorder( border );
        
        final JCheckBox dynamicCheckBox = new JCheckBox();
        dynamicCheckBox.setForeground( Color.RED );
        playAreaPanel.add( dynamicCheckBox );
        playAreaPanel.add( new JCheckBox( "red" ) );
        playAreaPanel.add( new JCheckBox( "blue" ) );
        playAreaPanel.add( new JCheckBox( "green" ) );
        PSwing wrapperNode = new PSwing( playAreaPanel );
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        canvas.getLayer().addChild( wrapperNode );
        wrapperNode.setOffset( 100, 100 );
        
        /*
         * Swing control panel outside of Piccolo.
         * Selecting one of the radio buttons changes the label on the 
         * red check box in the play area.
         */
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );
        TitledBorder controlPanelBorder = new TitledBorder( new LineBorder( Color.BLACK, 1 ), "Set the red check box label to:" );
        controlPanelBorder.setTitleColor( Color.RED );
        controlPanel.setBorder( controlPanelBorder );
        
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( e.getSource() instanceof JRadioButton ) {
                    dynamicCheckBox.setText( ( (JRadioButton) e.getSource() ).getText() );
                }
            }
        };
        
        JRadioButton rb1 = new JRadioButton( "<html>short label</html>" );
        rb1.addActionListener( listener );
        controlPanel.add( rb1 );
        
        JRadioButton rb2 = new JRadioButton( "<html>a much much much much longer label</html>" );
        rb2.addActionListener( listener );
        controlPanel.add( rb2 );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( rb1 );
        buttonGroup.add( rb2 );
        rb1.setSelected( true );
        dynamicCheckBox.setText( rb1.getText() );
        
        /*
         * Main layout, play area on the left, control panel on the right.
         */
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( controlPanel, BorderLayout.EAST );
        mainPanel.add( canvas, BorderLayout.CENTER );
        
        setContentPane( mainPanel );
        pack();
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestJComboBoxUpdate();
        frame.setVisible( true );
    }

}
