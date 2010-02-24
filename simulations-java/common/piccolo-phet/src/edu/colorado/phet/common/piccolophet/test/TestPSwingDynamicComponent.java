/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Demonstrates a PSwing problem with dynamic JComponents.
 * <p>
 * This example shows 2 identical JPanels, 1 pure Swing and 1 drawn using PSwing.
 * The JPanels contain 2 JLabels, which can be updated by typing into JTextFields 
 * and pressing the JButton labeled Update.
 * The JPanel managed by PSwing is often rendered incorrectly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingDynamicComponent extends JFrame {
    
    private static final Dimension FRAME_SIZE = new Dimension( 600, 400 );
    
    public TestPSwingDynamicComponent() {
        super( TestPSwingDynamicComponent.class.getName() );
        setSize( FRAME_SIZE );
        
        // panel that we're display using pure Swing
        final LabelPanel swingLabelPanel = new LabelPanel();
        JPanel swingPanel = new JPanel();
        swingPanel.setBorder( new LineBorder( Color.BLACK ) );
        swingPanel.add( swingLabelPanel );
        
        // panel that we'll display using Piccolo
        final LabelPanel piccoloLabelPanel = new LabelPanel();
        PSwing pswing = new PSwing( piccoloLabelPanel );
        pswing.setOffset( 50, 50 );
        PSwingCanvas canvas = new PSwingCanvas();
        canvas.getLayer().addChild( pswing );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        
        // text fields
        final JTextField topTextField = new JTextField( swingLabelPanel.getTopText() );
        topTextField.setColumns( 40 );
        final JTextField bottomTextField = new JTextField( swingLabelPanel.getBottomText() );
        bottomTextField.setColumns( 40 );
        
        // Update button
        JButton updateButton = new JButton( "Update" );
        updateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                swingLabelPanel.setTopText( topTextField.getText() );
                swingLabelPanel.setBottomText( bottomTextField.getText() );
                piccoloLabelPanel.setTopText( topTextField.getText() );
                piccoloLabelPanel.setBottomText( bottomTextField.getText() );
            }
        } );
        
        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( new LineBorder( Color.BLACK ) );
        controlPanel.setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 0;
        controlPanel.add( new JLabel( "top text:" ), c );
        c.anchor = GridBagConstraints.WEST;
        c.gridx++;
        controlPanel.add( topTextField, c );
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy++;
        controlPanel.add( new JLabel( "bottom text:" ), c );
        c.anchor = GridBagConstraints.WEST;
        c.gridx++;
        controlPanel.add( bottomTextField, c );
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        c.gridy++;
        controlPanel.add( updateButton, c );
        
        // main panel
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( swingPanel, BorderLayout.EAST );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );
        setContentPane( mainPanel );
    }
    
    /**
     * A panel with 2 JLabels. 
     * The JLabels' text can be changed.
     */
    private static class LabelPanel extends JPanel {
        
        private final JLabel topLabel, bottomLabel;

        public LabelPanel() {
            setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( 5, 14, 5, 14 ) ) );
            setBackground( new Color( 180, 205, 255 ) );
            
            topLabel = new JLabel( "top" );
            bottomLabel = new JLabel( "bottom" );
            
            setLayout( new GridBagLayout() );
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.WEST;
            c.gridx = 0;
            c.gridy = 0;
            add( topLabel, c );
            c.gridy++;
            add( bottomLabel, c );
        }
        
        public void setTopText( String s ) {
            topLabel.setText( s );
        }
        
        public String getTopText() {
            return topLabel.getText();
        }
        
        public void setBottomText( String s ) {
            bottomLabel.setText( s );
        }
        
        public String getBottomText() {
            return bottomLabel.getText();
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestPSwingDynamicComponent();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
