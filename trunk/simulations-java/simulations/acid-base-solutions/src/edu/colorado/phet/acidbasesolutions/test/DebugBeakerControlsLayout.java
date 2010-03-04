package edu.colorado.phet.acidbasesolutions.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * See Unfuddle #2188.
 * Demonstrates a problem with the Beaker controls panel in acid-base-solutions.
 * This was supposed to demonstrate the problem in isolation.
 * The layout is indeed a messed up when switching between ratio check box labels.
 * But it's not exactly the same type of messed up as in the sim.
 * <p>
 * Behavior differs depending on whether we use HTML text.
 * With HTML, the JCheckBoxes have their text rendered properly, but their position in the JPanel is (sometimes) wrong.
 * Without HTML, the JCheckBoxes have their text truncated (sometimes) with ellipses.
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
        setSize( new Dimension( 500, 500 ) );
        
        final ControlsNode controlsNode = new ControlsNode();
        controlsNode.setOffset( 50, 50 );
        
        PSwingCanvas canvas = new PSwingCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        canvas.getLayer().addChild( controlsNode );
        
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
    private static class ControlsNode extends PhetPNode {
        
        private JCheckBox ratioCheckBox; // when this check box's text is changed, the layout problem occurs
        
        protected ControlsNode() {
            
            // check boxes
            ratioCheckBox = new JCheckBox( TEXT1 );
            JCheckBox countsCheckBox = new JCheckBox( "Counts" );

            // panel
            JPanel panel = new JPanel();
            panel.setBorder( new TitledBorder( "View" ) );
            
            // layout
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            panel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( ratioCheckBox, row++, column );
            layout.addComponent( countsCheckBox, row++, column );
            
            addChild( new PSwing( panel ) );
        }
        
        public void setCheckBox1( String s ) {
            ratioCheckBox.setText( s );
        }
    }
    
    public static void main( String[] args ) {
        JFrame frame = new DebugBeakerControlsLayout();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
