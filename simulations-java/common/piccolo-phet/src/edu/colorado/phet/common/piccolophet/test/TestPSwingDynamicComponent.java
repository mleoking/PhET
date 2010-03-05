/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Demonstrates a PSwing problem with dynamic JComponents.
 * <p>
 * This example shows 2 identical JPanels.
 * The panel on the left uses PSwing.
 * The panel on the right uses pure Swing.
 * <p>
 * The JPanel contain various JComponents whose text can be updated by 
 * typing into JTextFields and pressing the "Update" button.
 * The JPanel managed by PSwing is often rendered incorrectly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingDynamicComponent extends JFrame {
    
    private static final Dimension FRAME_SIZE = new Dimension( 800, 400 );
    private static final int TEXT_FIELD_COLUMNS = 30;
    
    private final ComponentPanel swingPanel, piccoloPanel;
    private final JTextField labelTextField, checkBoxTextField, radioButtonTextField;
    
    public TestPSwingDynamicComponent() {
        super( TestPSwingDynamicComponent.class.getName() );
        setSize( FRAME_SIZE );
        
        // canvas
        PSwingCanvas canvas = new PSwingCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        
        // panel that we'll display using Piccolo
        piccoloPanel = new ComponentPanel();
        PSwing pswing = new PSwing( piccoloPanel );
        canvas.getLayer().addChild( pswing );
        pswing.setOffset( 10, 10 );
        
        // panel that we're display using pure Swing
        swingPanel = new ComponentPanel();
        JPanel jpanel = new JPanel();
        jpanel.setBorder( new LineBorder( Color.BLACK ) );
        jpanel.add( swingPanel );
        
        // text fields, for specifying dynamic text
        labelTextField = new JTextField( swingPanel.label.getText(), TEXT_FIELD_COLUMNS );
        checkBoxTextField = new JTextField( swingPanel.checkBox.getText(), TEXT_FIELD_COLUMNS );
        radioButtonTextField = new JTextField( swingPanel.radioButton.getText(), TEXT_FIELD_COLUMNS );
        
        // Update button, for applying dynamic text
        JButton updateButton = new JButton( "Update" );
        updateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updatePanels();
            }
        } );
        
        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( new LineBorder( Color.BLACK ) );
        controlPanel.setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        // JLabel
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        controlPanel.add( new JLabel( "JLabel text:" ), c );
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( labelTextField, c );
        // JCheckBox
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.EAST;
        controlPanel.add( new JLabel( "JCheckBox text:" ), c );
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( checkBoxTextField, c );
        // JRadioButton
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.EAST;
        controlPanel.add( new JLabel( "JRadioButton text:" ), c );
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( radioButtonTextField, c );
        // Update button
        c.gridx = 1;
        c.gridy++;
        c.anchor = GridBagConstraints.WEST;
        controlPanel.add( updateButton, c );
        
        // main panel
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( jpanel, BorderLayout.EAST );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );
        setContentPane( mainPanel );
    }
    
    // applies the text field values to the components in the panels
    private void updatePanels() {
        
        // Piccolo (PSwing) panel
        piccoloPanel.label.setText( labelTextField.getText() );
        piccoloPanel.checkBox.setText( checkBoxTextField.getText() );
        piccoloPanel.radioButton.setText( radioButtonTextField.getText() );
        
        // Swing panel
        swingPanel.label.setText( labelTextField.getText() );
        swingPanel.checkBox.setText( checkBoxTextField.getText() );
        swingPanel.radioButton.setText( radioButtonTextField.getText() );
    }
    
    // A panel with a few different types of JComponent.
    private static class ComponentPanel extends JPanel {
        
        // allow public access to keep our example code short
        public final JLabel label;
        public final JCheckBox checkBox;
        public final JRadioButton radioButton;

        public ComponentPanel() {
            setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( 5, 14, 5, 14 ) ) );
            setBackground( new Color( 180, 205, 255 ) );
            
            // components
            label = new JLabel( "JLabel" );
            checkBox = new JCheckBox( "JCheckBox" );
            radioButton = new JRadioButton( "JRadioButton" );
            
            // layout
            setLayout( new GridBagLayout() );
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.WEST;
            c.gridx = 0;
            c.gridy = 0;
            add( label, c );
            c.gridy++;
            add( checkBox, c );
            c.gridy++;
            add( radioButton, c );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestPSwingDynamicComponent();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
