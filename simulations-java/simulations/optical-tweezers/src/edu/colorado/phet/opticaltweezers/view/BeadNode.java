/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.FineCrosshairNode;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * BeadNode is visual representation of the dialectric glass bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeadNode extends SphericalNode implements Observer, PropertyChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int ALPHA = 200;
    private static final Color PRIMARY_COLOR = new Color( 200, 200, 0, ALPHA );
    private static final Color HILITE_COLOR = new Color( 255, 255, 0, ALPHA );
    private static final Stroke STROKE = null;
    private static final Paint STROKE_PAINT = Color.BLACK;
    
    private static final double CROSSHAIR_SIZE = 15;
    private static final Stroke CROSSHAIR_STROKE = new BasicStroke( 1f );
    private static final Color CROSSHAIR_COLOR = new Color( 0, 0, 0, 80 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private ModelViewTransform _modelViewTransform;
    private BoundedDragHandler _dragHandler;
    private Point2D _pModel; // reusable point
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a node that is NOT connected to a model.
     * This is useful for drawing a static representation of the bead.
     */
    public BeadNode( double diameter ) {
        super( true /* convertToImage */);
        
        setDiameter( diameter );
        
        Paint paint = new RoundGradientPaint( 0, diameter/6, HILITE_COLOR, new Point2D.Double( diameter/4, diameter/4 ), PRIMARY_COLOR );
        setPaint( paint );
        
        setStroke( STROKE );
        setStrokePaint( STROKE_PAINT );
    }
    
    /**
     * Constructs a node that is connected to a model.
     * 
     * @param bead
     * @param modelViewTransform
     * @param dragBoundsNode
     */
    public BeadNode( Bead bead, ModelViewTransform modelViewTransform, PNode dragBoundsNode ) {
        super( true /* convertToImage */);

        setStroke( STROKE );
        setStrokePaint( STROKE_PAINT );

        // faint crosshair at the bead's center
        addChild( new FineCrosshairNode( CROSSHAIR_SIZE, CROSSHAIR_STROKE, CROSSHAIR_COLOR ) );

        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        _pModel = new Point2D.Double();

        addInputEventListener( new CursorHandler() );

        _dragHandler = new DragHandler( this, dragBoundsNode, bead );
        addInputEventListener( _dragHandler );

        // Update the model when this node is dragged.
        addPropertyChangeListener( this );

        // Default state
        updateDiameter();
        updatePosition();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        if ( _bead != null ) {
            _bead.deleteObserver( this );
        }
    }
    
    //----------------------------------------------------------------------------
    // Property change handlers
    //----------------------------------------------------------------------------
    
    /**
     * Updates the bead model when this node is dragged.
     */
    public void propertyChange( PropertyChangeEvent event ) {
        if ( _bead != null ) {
            if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                Point2D pView = getOffset();
                _modelViewTransform.viewToModel( pView, _pModel );
                _bead.deleteObserver( this );
                _bead.setPosition( _pModel );
                _bead.addObserver( this );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                updatePosition();
            }
            else if ( arg == Bead.PROPERTY_DIAMETER ) {
                updateDiameter();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        Point2D position = _modelViewTransform.modelToView( _bead.getPositionReference() );
        setOffset( position.getX(), position.getY() );
    }
    
    private void updateDiameter() {
        final double diameter = _modelViewTransform.modelToView( _bead.getDiameter() );
        setDiameter( diameter );
        Paint paint = new RoundGradientPaint( 0, diameter/6, HILITE_COLOR, new Point2D.Double( diameter/4, diameter/4 ), PRIMARY_COLOR );
        setPaint( paint );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Drag handler that does the following:
     * 1. constrains dragging to the provided bounds (typically the microscope slide)
     * 2. if the bead is attached to a DNA strand, prevents over-stretching of the strand
     * 3. disabled the influence of Brownian and other forces while the bead is dragged
     */
    private class DragHandler extends BoundedDragHandler {

        /*
         * Maximum that the DNA strand can be stretched, expressed as a percentage
         * of the strand's contour length. As this value gets closer to 1, the 
         * DNA force gets closer to infinity, increasing the likelihood that the 
         * bead will rocket off the screen when it is released.
         */
        private static final double MAX_STRETCH = 0.95;
        
        private Bead _bead;
        private boolean _ignoreDrag;

        public DragHandler( PNode dragNode, PNode boundingNode, Bead bead ) {
            super( dragNode, boundingNode );
            _bead = bead;
            _ignoreDrag = false;
        }

        /*
         * Disables the bead's motion model when the drag starts.
         */
        public void mousePressed( PInputEvent event ) {
            _bead.setMotionEnabled( false );
        }

        /*
         * After dragging, check the DNA strand's extension length.
         * If it exceeds the contour length, then the strand is over-stretched,
         * and we negate the drag operation by moving the bead back to where it
         * was at the start of the drag.
         */
        public void mouseDragged( PInputEvent event ) {
            DNAStrand dnaStrand = _bead.getDNAStrand();
            if ( dnaStrand == null ) {
                super.mouseDragged( event );
            }
            else if ( !_ignoreDrag ) {
                Point2D beadPosition = _bead.getPosition();
                super.mouseDragged( event );
                if ( dnaStrand.getExtension() > ( MAX_STRETCH * dnaStrand.getContourLength() ) ) {
                    // Release the bead when the DNA strand becomes fully stretched.
                    _bead.setPosition( beadPosition );
                    endDrag( event );
                }
            }
        }
        
        /*
         * End a drag before the user has released the mouse button.
         * All subsequent drag events will be ignored until the user does
         * release the mouse button.
         */
        private void endDrag( PInputEvent event ) {
            super.mouseReleased( event );
            _ignoreDrag = true;
            _bead.setMotionEnabled( true );
        }

        /*
         * Enables the bead's motion model when the drag ends.
         * Sets the flag indicating that it's OK to process drag events.
         */
        public void mouseReleased( PInputEvent event ) {
            super.mouseReleased( event );
            _ignoreDrag = false;
            _bead.setMotionEnabled( true );
        }
    }
    
}
