/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Demonstrates a PSwing problem with dynamic Components.
 * In this example, a JPanel contains 2 JLabels.
 * When the JLabels' text is updated, the JPanel is often rendered incorrectly.
 * <p>
 * To use this example, type text into the text fields, then press the update button.
 * The light blue panel on the left is PSwing, the one on the right is pure Swing for comparison.
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
        swingPanel.add( swingLabelPanel );
        
        // panel that we'll display using Piccolo
        final LabelPanel piccoloLabelPanel = new LabelPanel();
        PSwing pswing = new PSwing( piccoloLabelPanel );
        pswing.scale( 1.5 );
        pswing.setOffset( 50, 50 );
        PhetPCanvas canvas = new PhetPCanvas( FRAME_SIZE );
        canvas.addWorldChild( pswing );
        
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
        EasyGridBagLayout controlPanelLayout = new EasyGridBagLayout( controlPanel );
        controlPanel.setLayout( controlPanelLayout );
        int row = 0;
        int column = 0;
        controlPanelLayout.addComponent( new JLabel( "top text:" ), row, column++, 1, 1, GridBagConstraints.EAST );
        controlPanelLayout.addComponent( topTextField, row, column++ );
        column = 0;
        row++;
        controlPanelLayout.addComponent( new JLabel( "bottom text:" ), row, column++, 1, 1, GridBagConstraints.EAST );
        controlPanelLayout.addComponent( bottomTextField, row++, column );
        column = 1;
        row++;
        controlPanelLayout.addComponent( updateButton, row, column );
        
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
            
            EasyGridBagLayout labelPanelLayout = new EasyGridBagLayout( this );
            this.setLayout( labelPanelLayout );
            int row = 0;
            int column = 0;
            labelPanelLayout.addComponent( topLabel, row++, column );
            labelPanelLayout.addComponent( bottomLabel, row++, column );
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
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
