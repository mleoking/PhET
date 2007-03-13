/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.piccolo.event.BoundedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.RulerNode;
import edu.umd.cs.piccolo.PNode;


public class OTRulerNode extends RulerNode implements Observer {
    
    private static final double DEFAULT_WIDTH = 600;
    private static final double HEIGHT = 40;
    private static final int MAJOR_TICK_INTERVAL = 100; // nm
    private static final int MINOR_TICKS_BETWEEN_MAJORS = 1; // 
    private static final int FONT_SIZE = 12;
    
    private Laser _laser;
    private ModelWorldTransform _modelWorldTransform;
    
    public OTRulerNode( Laser laser, ModelWorldTransform modelWorldTransform, PNode dragBoundsNode ) {
        super( DEFAULT_WIDTH, HEIGHT, null, SimStrings.get( "units.position" ), MINOR_TICKS_BETWEEN_MAJORS, FONT_SIZE );
        
        setUnits( "" );//XXX
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelWorldTransform = modelWorldTransform;
        
        addInputEventListener( new CursorHandler() );
        
//        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );
//        addInputEventListener( new PDragEventHandler() );//XXX
        
        setWorldWidth( DEFAULT_WIDTH );
        updatePosition();
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }

    public void setWorldWidth( double worldWidth ) {
        
        // Convert canvas width to model coordinates
        double modelCanvasWidth = _modelWorldTransform.worldToModel( worldWidth );
        
        int numMajorTicks = (int)( 3 * modelCanvasWidth / MAJOR_TICK_INTERVAL );
        if ( numMajorTicks % 2 == 0 ) {
            numMajorTicks++; // must be an odd number, with 0 at middle
        }
        
        double viewDistance = ( numMajorTicks - 1 ) * _modelWorldTransform.modelToWorld( MAJOR_TICK_INTERVAL );
        setDistanceBetweenFirstAndLastTick( viewDistance );
        
        String[] majorTickLabels = new String[ numMajorTicks ];
        int majorTick = -MAJOR_TICK_INTERVAL * ( numMajorTicks - 1 ) / 2;
        for ( int i = 0; i < majorTickLabels.length; i++ ) {
            majorTickLabels[i] = String.valueOf( majorTick );
            majorTick += MAJOR_TICK_INTERVAL;
        }
        setMajorTickLabels( majorTickLabels );
        
        updatePosition();
    }

    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION ) {
                updatePosition();
            }
        }
    }
    
    private void updatePosition() {
        double xModel = _laser.getPositionRef().getX();
        double xView = _modelWorldTransform.modelToWorld( xModel ) - ( getFullBounds().getWidth() / 2 );
        double yView = getOffset().getY();
        setOffset( xView, yView );
    }
}
