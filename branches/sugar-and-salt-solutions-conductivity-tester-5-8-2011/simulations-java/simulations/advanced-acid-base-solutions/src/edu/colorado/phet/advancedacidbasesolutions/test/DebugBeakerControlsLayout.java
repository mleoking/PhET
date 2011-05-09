// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.test;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Demonstrates a PSwing layout problem with the Beaker control panel in the "Acid Base Solutions" simulation.
 * The controls (check boxes) in this panel have dynamic labels.
 * When the labels are changed, the panel layout is messed up.
 * <p>
 * This example reduces the problem to one JCheckBox with dynamic text and one with static text.
 * Use the 2 radio buttons at the bottom of the frame to change the dynamic text.
 * <p>
 * Behavior differs depending on whether we use HTML text.
 * With HTML, the JCheckBoxes have their text rendered properly, but their position in the JPanel is (sometimes) wrong.
 * Without HTML, the JCheckBoxes have their text truncated (sometimes) with ellipses.
 * <p>
 * See Unfuddle #2188.
 *  
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugBeakerControlsLayout extends JFrame {
    
    private static final boolean USE_HTML = false;
    
    // strings used for the top check box
    private static final String TEXT1 = USE_HTML ? "<html>H<i>A</i>/<i>A</i><sup>-</sup> ratio</html>" : "HA/A- ratio";
    private static final String TEXT2 = USE_HTML ? "<html>C<sub>5</sub>H<sub>5</sub>N/C<sub>5</sub>H<sub>5</sub>NH<sup>+</sup> ratio </html>" : "C5H5N/C5H5NH ratio";
    
    public DebugBeakerControlsLayout() {
        super( DebugBeakerControlsLayout.class.getName() );
        setSize( new Dimension( 500, 250 ) );
        
        // Piccolo canvas
        PSwingCanvas canvas = new PSwingCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        
        // Swing control panel wrapped in PSwing
        final ControlPanel controlsNode = new ControlPanel();
        PSwing pswing = new PSwing( controlsNode );
        pswing.setOffset( 50, 50 );
        canvas.getLayer().addChild( pswing );
        
        final JRadioButton rb1 = new JRadioButton( TEXT1 );
        rb1.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( rb1.isSelected() ) {
                    controlsNode.setCheckBox1( rb1.getText() );
                }
            }
        });
        
        final JRadioButton rb2 = new JRadioButton( TEXT2 );
        rb2.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( rb2.isSelected() ) {
                    controlsNode.setCheckBox1( rb2.getText() );
                }
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add( rb1 );
        group.add( rb2 );
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( new LineBorder( Color.BLACK ) );
        controlPanel.add( rb1 );
        controlPanel.add( rb2 );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.SOUTH );
        setContentPane( mainPanel );
        
        // default statue
        rb1.setSelected( true );
        controlsNode.setCheckBox1( rb1.getText() );
    }
    
    // a simplified version of BeakerControlsNode
    private static class ControlPanel extends JPanel {
        
        private JCheckBox ratioCheckBox; // when this check box's text is changed, the problem occurs
        
        protected ControlPanel() {
            
            ratioCheckBox = new JCheckBox( TEXT1 );
            
            // another JCheckBox, so we can see how it's mangled
            JCheckBox countsCheckBox = new JCheckBox( "Counts" );

            setBorder( new TitledBorder( "View" ) );
            setLayout( new GridBagLayout() );
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.WEST;
            add( ratioCheckBox, c );
            c.gridy++;
            add( countsCheckBox, c );
        }
        
        public void setCheckBox1( String s ) {
            ratioCheckBox.setText( s );
        }
    }
    
    public static void main( String[] args ) {
        JFrame frame = new DebugBeakerControlsLayout();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
