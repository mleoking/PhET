// Copyright 2002-2011, University of Colorado

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
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.DragNotificationHandler;
import edu.colorado.phet.common.piccolophet.event.DragNotificationHandler.DragNotificationListener;
import edu.colorado.phet.common.piccolophet.nodes.FineCrosshairNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.PNode;

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
    private OTModelViewTransform _modelViewTransform;
    private Point2D _pModel; // reusable point
    private DragNotificationHandler _dragNotificationHandler;
    
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
    public BeadNode( Bead bead, OTModelViewTransform modelViewTransform, PNode dragBoundsNode ) {
        super( true /* convertToImage */);

        setStroke( STROKE );
        setStrokePaint( STROKE_PAINT );

        // faint crosshair at the bead's center
        addChild( new FineCrosshairNode( CROSSHAIR_SIZE, CROSSHAIR_STROKE, CROSSHAIR_COLOR ) );

        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        _pModel = new Point2D.Double();
        
        final double diameter = _modelViewTransform.modelToView( _bead.getDiameter() );
        setDiameter( diameter );
        Paint paint = new RoundGradientPaint( 0, diameter/6, HILITE_COLOR, new Point2D.Double( diameter/4, diameter/4 ), PRIMARY_COLOR );
        setPaint( paint );

        CursorHandler cursorHandler = new CursorHandler();
        addInputEventListener( cursorHandler );
        
        BeadDragHandler dragHandler = new BeadDragHandler( this, dragBoundsNode, cursorHandler );
        addInputEventListener( dragHandler );
        _dragNotificationHandler = new DragNotificationHandler( this );
        addInputEventListener( _dragNotificationHandler );

        // Update the model when this node is dragged.
        addPropertyChangeListener( this );

        // Default state
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
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public Bead getBead() {
        return _bead;
    }
    
    public void addDragNotificationListener( DragNotificationListener listener ) {
        _dragNotificationHandler.addDragNotificationListener( listener );
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
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        _modelViewTransform.modelToView( _bead.getPositionReference(), _pModel );
        removePropertyChangeListener( this );
        setOffset( _pModel );
        addPropertyChangeListener( this );
    }
}
