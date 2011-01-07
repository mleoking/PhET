// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * TestDraggableControlPanel demonstrates how to make a Piccolo-based
 * control panel that is entirely draggable, while still maintaining
 * proper drag behavior for Swing controls (eg, JSlider).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDraggableControlPanel extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 500, 300 );
    
    public TestDraggableControlPanel() {
        super();
        
        final ControlPanelNode controlPanelNode = new ControlPanelNode();
        double x = ( FRAME_SIZE.getWidth() - controlPanelNode.getFullBounds().getWidth() ) / 2;
        double y = ( FRAME_SIZE.getHeight() - controlPanelNode.getFullBounds().getHeight() ) / 2;
        controlPanelNode.setOffset( x, y );
        
        PhetPCanvas canvas = new PhetPCanvas( FRAME_SIZE );
        canvas.getLayer().addChild( controlPanelNode );
        
        setContentPane( canvas );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    private static class ControlPanelNode extends PNode {

        private JSlider _slider;
        private JLabel _label;
        
        public ControlPanelNode() {
            
            // Slider
            _slider = new JSlider( 0, 100 );
            _slider.setMajorTickSpacing( 50 );
            _slider.setMinorTickSpacing( 10 );
            _slider.setPaintTicks( true );
            _slider.setPaintLabels( true );
            _slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    _label.setText( String.valueOf( _slider.getValue() ) );
                }
            });
            final PSwing sliderWrapper = new PSwing( _slider );
            
            // Label that displays slider value
            _label = new JLabel();
            _label.setText( String.valueOf( _slider.getValue() ) );
            _label.setFont( new PhetFont( 24 ) );
            PSwing labelWrapper = new PSwing( _label );
            
            // Background rectangle
            double w = sliderWrapper.getFullBounds().getWidth() * 2;
            double h = sliderWrapper.getFullBounds().getHeight() * 2;
            PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
            backgroundNode.setPaint( Color.LIGHT_GRAY );
            
            // Layering
            addChild( backgroundNode );
            addChild( sliderWrapper );
            addChild( labelWrapper );
            
            // Positioning
            double x = ( backgroundNode.getFullBounds().getWidth() - sliderWrapper.getFullBounds().getWidth() ) / 2;
            double y = ( backgroundNode.getFullBounds().getHeight() - sliderWrapper.getFullBounds().getHeight() ) / 2;
            sliderWrapper.setOffset( x, y );
            x = sliderWrapper.getFullBounds().getMaxX() + 20;
            y = ( backgroundNode.getFullBounds().getHeight() - labelWrapper.getFullBounds().getHeight() ) / 2;
            labelWrapper.setOffset( x, y );
            
            // Drag handler
            final PNode thisNode = this;
            PDragEventHandler dragHandler = new PDragEventHandler() {
                protected void startDrag( PInputEvent event ) {
                    super.startDrag( event );
                    // if the dragged node is not the slider, then move the entire control panel
                    if ( getDraggedNode() != sliderWrapper ) {
                        setDraggedNode( thisNode );
                    }
                }
            };
            backgroundNode.addInputEventListener( dragHandler );
            labelWrapper.addInputEventListener( dragHandler );
            
            // Cursors
            backgroundNode.addInputEventListener( new CursorHandler() );
            labelWrapper.addInputEventListener( new CursorHandler() );
        }
    }
    
    public static void main( String[] args ) {
        JFrame testFrame = new TestDraggableControlPanel();
        testFrame.show();
    }
}
