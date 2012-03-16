// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.opticaltweezers.test;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * TestDragProblem isolates a problem I'm having in the Optical Tweezers simulations
 * when using a BoundedDragHandler and PhetPCanvas.addWorldChild.
 * <p>
 * This example has a horizontal bar that is similar to the ruler in Optical Tweezers.
 * When the canvas size is changed, the bar dynamically resizes so that its width is
 * always 75% of the canvas width. The bar can be dragged vertically, but it's 
 * horizontal position is locked. Horizontal position can only be adjusted using
 * the slider at the bottom of the frame.
 * <p>
 * A position (x,y) determines the location of the bar's upper left corner.
 * For x>=0, dragging works fine. But for x<0, it becomes possible to move
 * the bar horizontally.  As -x becomes larger, the amount of horizontal 
 * movement increases.
 * <p>
 * This problem seems to be unique to using PhetPCanvas.addWorldChild.
 * The problem does not occur when adding nodes using PhetPCanvas.getLayer().addChild;
 * to demonstrate, set USE_WORLD_CHILD=false.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDragProblem extends JFrame {
    
    private static final boolean USE_WORLD_CHILD = true;

    private static final Dimension FRAME_SIZE = new Dimension( 1024, 768 ); // screen coordinates
    private static final Dimension WORLD_SIZE = new Dimension( 750, 750 ); // world coordinates
    private static final double MIN_X_POSITION = -700; // world coordinates
    private static final double MAX_X_POSITION = +700; // world coordinates
    private static final double INITIAL_X_POSITION = 100; // world coordinates
    
    public static void main( String[] args ) {
        TestDragProblem frame = new TestDragProblem();
        frame.show();
    }
    
    public TestDragProblem() {
        super( "TestDragProblem" );
        
        /*--------------------------- Canvas ---------------------------*/
        
        PPath dragBoundsNode = new PPath(); // shape will be determined by HorizontalBarNode
        dragBoundsNode.setStrokePaint( Color.RED ); // so we can see the bounds
        
        final HorizontalBarNode horizontalBarNode = new HorizontalBarNode( INITIAL_X_POSITION, dragBoundsNode );

        final PhetPCanvas canvas = new PhetPCanvas( WORLD_SIZE );
        if ( USE_WORLD_CHILD ) {
            canvas.addWorldChild( horizontalBarNode );
            canvas.addWorldChild( dragBoundsNode );
        }
        else {
            canvas.getLayer().addChild( horizontalBarNode );
            canvas.getLayer().addChild( dragBoundsNode );
        }
        
        // when the canvas size changes, update the horizontal bar
        canvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Dimension2D worldSize = canvas.getWorldSize();
                if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
                    horizontalBarNode.setWorldSize( worldSize );
                }
            }
        } );
        
        /*--------------------------- Control Panel ---------------------------*/
        
        final JLabel xPositionLabel = new JLabel( String.valueOf( INITIAL_X_POSITION ) );
        
        final JSlider xPositionSlider = new JSlider();
        xPositionSlider.setMinimum( (int) MIN_X_POSITION );
        xPositionSlider.setMaximum( (int) MAX_X_POSITION);
        xPositionSlider.setValue( (int) INITIAL_X_POSITION );
        xPositionSlider.addChangeListener( new ChangeListener() {
            // when the slider is changed, update the horizontal bar's position
            public void stateChanged( ChangeEvent event ) {
                double x = xPositionSlider.getValue();
                xPositionLabel.setText( String.valueOf( x ) );
                horizontalBarNode.setHorizontalPosition( x );
            }
        } );
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );
        controlPanel.add( xPositionSlider );
        controlPanel.add( xPositionLabel );
        
        /*--------------------------- Frame ---------------------------*/
        
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.add( canvas );
        panel.add( controlPanel );
        
        setContentPane( panel );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    /*
     * HorizontalBarNode is a horizontal bar with a dynamic width and fixed height.
     * The width is adjusted dynamically so that it is always 75% of the canvas width.
     * The bar can be vertically dragged, but dragging cannot change it's horizontal position.
     * Horizontal position can be changed only via the slider in the control panel.
     */
    private class HorizontalBarNode extends PNode {
        
        private static final double HEIGHT = 20;

        private double _xPosition;
        private Dimension2D _worldSize;
        private PPath _dragBoundsNode;
        
        public HorizontalBarNode( double x, PPath dragBoundsNode ) {
            super();
            _worldSize = new PDimension();
            _dragBoundsNode = dragBoundsNode;
            addInputEventListener( new BoundedDragHandler( this, _dragBoundsNode ) );
            addInputEventListener( new CursorHandler() );
            setHorizontalPosition( x );
        }
        
        public void setHorizontalPosition( double x ) {
            _xPosition = x;
            updateOffset();
        }
        
        public void setWorldSize( Dimension2D worldSize ) {
            _worldSize.setSize( worldSize );
            updateWidth();
        }
        
        private void updateOffset() {
            setOffset( _xPosition, getOffset().getY() );
            updateDragBounds();
        }
        
        private void updateDragBounds() {
            Rectangle2D rect = new Rectangle2D.Double( getFullBounds().getX(), 0, getFullBounds().getWidth(), _worldSize.getHeight() );
            _dragBoundsNode.setPathTo( rect );
            _dragBoundsNode.setOffset( 0, 0 );
        }
        
        private void updateWidth() {
            removeAllChildren();
            
            // gray rectangle that is as wide as the canvas
            Rectangle2D rect = new Rectangle2D.Double( 0, 0, 0.75 * _worldSize.getWidth(), HEIGHT );
            PPath pathNode = new PPath( rect );
            pathNode.setStroke( new BasicStroke( 1f ) );
            pathNode.setStrokePaint( Color.BLACK );
            pathNode.setPaint( Color.GRAY );
            pathNode.setOffset( 0, 0 );
            addChild( pathNode );
            
            updateOffset();
        }
    }
    
}
