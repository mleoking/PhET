/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.glaciers.GlaciersConstants;

/**
 * DebrisGenerator generates a 3D position for debris, 
 * based on the current state of the glacier.
 * Points are weighted so that more of them are generated in the cross-section (z=0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebrisGenerator {
    
    private static final int DEBRIS_CROSS_SECTION_RATIO = 5; // 1 out of this many debris are created in the ice cross-section
    
    private static final double MIN_GLACIER_LENGTH = 300; // meters
    private static final double MIN_DISTANCE_DOWNVALLEY = 10; // meters
    private static final double MIN_DISTANCE_FROM_TERMINUS = 1; // meters
    
    private static final double MIN_GLACIER_THICKNESS = 30; // meters
    private static final double MIN_ELEVATION_ABOVE_VALLEY_FLOOR = 10; // meters
    private static final double MIN_ELEVATION_BELOW_SURFACE = 1; // meters

    private final Glacier _glacier;
    private final Random _randomDebrisX, _randomDebrisY, _randomDebrisZ;
    private int _count;
    
    public DebrisGenerator( Glacier glacier ) {
        
        // sanity check on constants
        assert( MIN_GLACIER_LENGTH > ( MIN_DISTANCE_DOWNVALLEY + MIN_DISTANCE_FROM_TERMINUS ) );
        assert( MIN_GLACIER_THICKNESS > ( MIN_ELEVATION_ABOVE_VALLEY_FLOOR + MIN_ELEVATION_BELOW_SURFACE ) );
        
        _glacier = glacier;
        _randomDebrisX = new Random();
        _randomDebrisY = new Random();
        _randomDebrisZ = new Random();
        _count = 0;
    }
    
    public Point3D generateDebrisPosition( Point3D pOutput ) {
        
        Point3D p = null;
        
        // glacier must have some minimum length...
        if ( _glacier.getLength() > MIN_GLACIER_LENGTH ) {
            
            // x: distance, between valley headwall terminus
            final double minX = _glacier.getHeadwallX() + MIN_DISTANCE_DOWNVALLEY;
            final double maxX = _glacier.getTerminusX() - MIN_DISTANCE_FROM_TERMINUS;
            assert( maxX > minX );
            final double x = minX + _randomDebrisX.nextDouble() * ( maxX - minX );
            assert( x > minX && x < maxX );
            
            // if the ice is some minimum thickness at x ...
            final double valleyElevation = _glacier.getValley().getElevation( x );
            final double glacierSurfaceElevation = _glacier.getSurfaceElevation( x );
            if ( glacierSurfaceElevation - valleyElevation > MIN_GLACIER_THICKNESS ) {
                
                // y: elevation, between valley floor and glacier surface
                final double minY = valleyElevation + MIN_ELEVATION_ABOVE_VALLEY_FLOOR; // slightly above valley floor
                final double maxY = glacierSurfaceElevation - MIN_ELEVATION_BELOW_SURFACE; // slightly below glacier's surface
                assert( maxY > minY );
                final double y = minY + ( _randomDebrisY.nextDouble() * ( maxY - minY ) );
                assert ( y > minY && y < maxY );

                // z: distance across the width of the valley floor
                double z = 0; // in the cross-section
                if ( _count % DEBRIS_CROSS_SECTION_RATIO != 0 ) {
                    // not in the cross-section
                    z = _randomDebrisZ.nextDouble() * GlaciersConstants.PERSPECTIVE_HEIGHT;
                    assert( z >= 0 && z <= GlaciersConstants.PERSPECTIVE_HEIGHT );
                }
                
                p = pOutput;
                p.setLocation( x, y, z );

                _count++;
            }
        }
        
        return p;
    }
}
