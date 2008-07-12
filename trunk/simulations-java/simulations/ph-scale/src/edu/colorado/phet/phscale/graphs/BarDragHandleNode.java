/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarDragHandleNode extends PPath {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final float ARROW_SCALE = 24f; // change this to make the arrow bigger or smaller
    
    private static final Color DEFAULT_NORMAL_COLOR = new Color( 255, 255, 255, 200 ); // translucent white
    private static final Color DEFAULT_HILITE_COLOR = Color.YELLOW;
    private static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Color _normalColor;
    private Color _hiliteColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BarDragHandleNode() {
        super();
        
        _normalColor = DEFAULT_NORMAL_COLOR;
        _hiliteColor = DEFAULT_HILITE_COLOR;

        // Drag handle representation
        Shape shape = createArrowShape( ARROW_SCALE );
        setPathTo( shape );
        setPaint( DEFAULT_NORMAL_COLOR );
        setStroke( DEFAULT_STROKE );
        setStrokePaint( DEFAULT_STROKE_COLOR );
        
        // Cursor behavior
        addInputEventListener( new CursorHandler() );

        // Make drag handle hilite when the mouse is over it or it's being dragged.
        addInputEventListener( new HiliteHandler() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Shape for the drag handle.
    //----------------------------------------------------------------------------
    
    /*
     * Gets the shape for an "arrow" drag handles, with a specified scale.
     * The shape is a double-headed arrow.
     * A scale of 1 will create an arrow who largest dimension is 1 pixel.
     * Origin is at the geometric center of the shape.
     * 
     * @param scale
     * @return
     */
    private static Shape createArrowShape( float scale ) {
        
        // double-headed arrow, pointing up & down, origin at tip of arrow that points up
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( .25f * scale, .33f * scale );
        path.lineTo( .08f * scale, .33f * scale );
        path.lineTo( .08f * scale, .67f * scale );
        path.lineTo( .25f * scale, .67f * scale );
        path.lineTo( 0 * scale, 1 * scale );
        path.lineTo( -.25f * scale, .67f * scale );
        path.lineTo( -.08f * scale, .67f * scale );
        path.lineTo( -.08f * scale, .33f * scale );
        path.lineTo( -.25f * scale, .33f * scale );
        path.closePath();
        
        // move origin to geometric center (so we don't have to modify the above code)
        AffineTransform transform = new AffineTransform();
        transform.translate( 0, -scale / 2 );
        Shape shape = transform.createTransformedShape( path );
        
        return shape;
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Hilites the handle while dragging the handle, 
     * or while the mouse is inside the handle.
     */
    private class HiliteHandler extends PBasicInputEventHandler {
        
        private boolean _mouseIsPressed;
        private boolean _mouseIsInside;

        public HiliteHandler() {
            super();
        }
        
        public void mousePressed( PInputEvent event ) {
            _mouseIsPressed = true;
            setPaint( _hiliteColor );
        }

        public void mouseReleased( PInputEvent event ) {
            _mouseIsPressed = false;
            if ( !_mouseIsInside ) {
                setPaint( _normalColor );
            }
        }

        public void mouseEntered( PInputEvent event ) {
            _mouseIsInside = true;
            setPaint( _hiliteColor );
        }

        public void mouseExited( PInputEvent event ) {
            _mouseIsInside = false;
            if ( !_mouseIsPressed ) {
                setPaint( _normalColor );
            }
        }
    }
}
