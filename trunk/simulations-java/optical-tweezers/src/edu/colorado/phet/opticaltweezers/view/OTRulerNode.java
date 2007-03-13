/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.RulerNode;
import edu.umd.cs.piccolo.PNode;


public class OTRulerNode extends RulerNode implements Observer {
    
    private static final double HEIGHT = 40;
    private static final int MAJOR_TICK_INTERVAL = 100; // nm
    private static final int MINOR_TICKS_BETWEEN_MAJORS = 1; // 
    private static final int FONT_SIZE = 12;
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    public OTRulerNode( double width, Laser laser, ModelViewTransform modelViewTransform, PNode dragBoundsNode ) {
        super( width, HEIGHT, null, SimStrings.get( "units.position" ), MINOR_TICKS_BETWEEN_MAJORS, FONT_SIZE );
        
        setUnits( "" );//XXX
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        addInputEventListener( new CursorHandler() );
        
//        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );
//        addInputEventListener( new PDragEventHandler() );//XXX
        
        setCanvasWidth( width );
        updatePosition();
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }

    public void setCanvasWidth( double canvasWidth ) {
        
        // Convert canvas width to model coordinates
        double modelCanvasWidth = _modelViewTransform.inverseTransform( canvasWidth );
        
        int numMajorTicks = (int)( 3 * modelCanvasWidth / MAJOR_TICK_INTERVAL );
        if ( numMajorTicks % 2 == 0 ) {
            numMajorTicks++; // must be an odd number, with 0 at middle
        }
        
        double viewDistance = ( numMajorTicks - 1 ) * _modelViewTransform.transform( MAJOR_TICK_INTERVAL );
        setDistanceBetweenFirstAndLastTick( viewDistance );
        
        String[] majorTickLabels = new String[ numMajorTicks ];
        int majorTick = -MAJOR_TICK_INTERVAL * ( numMajorTicks - 1 ) / 2;
        for ( int i = 0; i < majorTickLabels.length; i++ ) {
            majorTickLabels[i] = String.valueOf( majorTick );
            majorTick += MAJOR_TICK_INTERVAL;
        }
        setMajorTickLabels( majorTickLabels );
    }

    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION ) {
                updatePosition();
            }
        }
    }
    
    private void updatePosition() {
        Point2D p = _modelViewTransform.transform( _laser.getPositionRef() );
        setOffset( p.getX() - ( getFullBounds().getWidth() / 2 ), getOffset().getY() );
    }
}
