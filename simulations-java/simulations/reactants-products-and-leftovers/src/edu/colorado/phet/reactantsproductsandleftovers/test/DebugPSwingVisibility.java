package edu.colorado.phet.reactantsproductsandleftovers.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * See #2133.
 * Investigate problem with PSwing visibility in Piccolo2D 1.3 rc1.
 * Architecturally similar to reactants-products-and-leftovers.
 * 
 * Problems noted in this example:
 * o spinner is only usable the first time it becomes visible. After that, it doesn't work.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugPSwingVisibility extends JFrame {
    
    public DebugPSwingVisibility() {
        setResizable( false );
        setSize( new Dimension( 400, 200 ) );
        
        // canvas
        PCanvas canvas = new PSwingCanvas();
        canvas.setBorder( new LineBorder( Color.BLACK ) );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        
        // Value that can be toggled between read-only and editable.
        final ValueNode valueNode = new ValueNode( 0 );
        canvas.getLayer().addChild( valueNode );
        valueNode.setOffset( 50, 50 );
        
        // Check box for setting whether the value is editable.
        final JCheckBox editableCheckBox = new JCheckBox( "editable", valueNode.isEditable() );
        editableCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                valueNode.setEditable( editableCheckBox.isSelected() );
            }
        } );
        
        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( new LineBorder( Color.BLACK ) );
        controlPanel.add( editableCheckBox );
        
        // layout, control panel on right
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.EAST );
        setContentPane( panel );
    }
    
    /**
     * Display a value in either read-only or editable form.
     */
    public static class ValueNode extends PNode {

        private final JSpinner spinner;
        private final PSwing editableNode;
        private final PText readOnlyNode;
        
        public ValueNode( int value ) {
            
            // editable value
            SpinnerModel model = new SpinnerNumberModel( value, 0, 100, 1 );
            spinner = new JSpinner( model );
            editableNode = new PSwing( spinner );
            editableNode.scale( 1.5 );
            spinner.addChangeListener( new ChangeListener() {
                // sync read-only value to editable value
                public void stateChanged( ChangeEvent e ) {
                   readOnlyNode.setText( String.valueOf( ((Integer) spinner.getValue() ).intValue() ) );
                }
            });
            
            // read-only value
            readOnlyNode = new PText( String.valueOf( value ) );
            readOnlyNode.setFont( new Font( readOnlyNode.getFont().getFontName(), Font.PLAIN, 22 ) );
            
            // read-only by default
            addChild( readOnlyNode );
        }
        
        public boolean isEditable() {
            return ( indexOfChild( editableNode ) != -1 );
        }
        
        public void setEditable( boolean editable ) {
            if ( editable != isEditable() ) {
                editableNode.setPickable( editable );
                if ( editable ) {
                    removeChild( readOnlyNode );
                    addChild( editableNode );
                }
                else {
                    removeChild( editableNode );
                    addChild( readOnlyNode );
                }
            }
        }
    }
    
    public static void main( String[] args ) {
        JFrame frame = new DebugPSwingVisibility();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        // center on the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        int x = (int) ( tk.getScreenSize().getWidth() / 2 - frame.getWidth() / 2 );
        int y = (int) ( tk.getScreenSize().getHeight() / 2 - frame.getHeight() / 2 );
        frame.setLocation( x, y );
        frame.setVisible( true );
    }
}
