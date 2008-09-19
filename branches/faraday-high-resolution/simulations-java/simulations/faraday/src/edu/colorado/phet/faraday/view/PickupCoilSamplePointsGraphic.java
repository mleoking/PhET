/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.model.PickupCoil;

/**
 * SamplePointsGraphic is the graphical representation of the
 * points on the coil where the magnetic field is sampled.
 * This graphic is used for debugging, and is intended to be a child of PickupCoilGraphic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PickupCoilSamplePointsGraphic extends CompositePhetGraphic implements SimpleObserver {
    
    private static final Color POINT_COLOR = Color.YELLOW;
    private static final Shape POINT_SHAPE = new Ellipse2D.Double( -2, 2, 4, 4 );
    
    private PickupCoil _pickupCoilModel;
    
    /**
     * Sole constructor.
     * Creates a set of graphics that represent the sample points.
     * 
     * @param component
     */
    public PickupCoilSamplePointsGraphic( Component component, PickupCoil pickupCoilModel ) {
        super( component );
        _pickupCoilModel = pickupCoilModel;
        _pickupCoilModel.addObserver( this );
    }
    
    /**
     * Updates this graphic when it becomes visible.
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            update();
        }
    }
    
    /**
     * Updates the sample points display when the model changes.
     */
    public void update() {
        if ( isVisible() ) {
            clear(); // remove all child graphics
            final Point2D[] samplePoints = _pickupCoilModel.getSamplePoints();
            Component component = getComponent();
            for ( int i = 0; i < samplePoints.length; i++ ) {
                PhetGraphic samplePointGraphic = createGraphic( component, (int)samplePoints[i].getX(), (int)samplePoints[i].getY() );
                addGraphic( samplePointGraphic );
            }
        }
    }
    
    /*
     * Creates the graphical representation of a sample point.
     */
    private static PhetGraphic createGraphic( Component component, int x, int y ) {
        PhetShapeGraphic g = new PhetShapeGraphic( component );
        g.setShape( POINT_SHAPE );
        g.setColor( POINT_COLOR );
        g.centerRegistrationPoint();
        g.setLocation( x, y );
        return g;
    }
}