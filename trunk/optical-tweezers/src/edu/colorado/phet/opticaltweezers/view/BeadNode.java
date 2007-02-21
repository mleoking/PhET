/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.util.RoundGradientPaint;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;


public class BeadNode extends SphericalNode implements Observer {

    private static final int ALPHA = 200;
    private static final Color PRIMARY_COLOR = new Color( 200, 200, 0, ALPHA );
    private static final Color HILITE_COLOR = new Color( 255, 255, 0, ALPHA );
    private static final Stroke STROKE = new BasicStroke( 0.5f );
    private static final Paint STROKE_PAINT = Color.BLACK;
    
    private Bead _bead;
    private ModelViewTransform _modelViewTransform;
    private ConstrainedDragHandler _dragHandler;
    
    public BeadNode( Bead bead, ModelViewTransform modelViewTransform ) {
        super( true /* convertToImage */ );
        
        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        setStroke( STROKE );
        setStrokePaint( STROKE_PAINT );
        
        addInputEventListener( new CursorHandler() );
        
        _dragHandler = new ConstrainedDragHandler();
        _dragHandler.setTreatAsPointEnabled( false );
        addInputEventListener( _dragHandler  );
        
        // Default state
        handleDiameterChange();
        handlePositionChange();
    }
    
    public void cleanup() {
        _bead.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    /**
     * Sets the drag bounds.
     * 
     * @param dragBound drag bounds, in global coordinates
     */
    public void setGlobalDragBounds( Rectangle2D dragBounds ) {
        _dragHandler.setDragBounds( dragBounds );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                handlePositionChange();
            }
            if ( arg == Bead.PROPERTY_DIAMETER ) {
                handleDiameterChange();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Property change handlers
    //----------------------------------------------------------------------------
    
    private void handlePositionChange() {
        Point2D position = _modelViewTransform.transform( _bead.getPositionRef() );
        setOffset( position.getX(), position.getY() );
    }
    
    private void handleDiameterChange() {
        final double diameter = _modelViewTransform.transform( _bead.getDiameter() );
        setDiameter( diameter );
        Paint paint = new RoundGradientPaint( 0, diameter/6, HILITE_COLOR, new Point2D.Double( diameter/4, diameter/4 ), PRIMARY_COLOR );
        setPaint( paint );
    }
    
}
