/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * DebrisGenerator generates a 3D position for debris, 
 * based on the current state of the glacier.
 * Points are weighted so that more of them are generated in the cross-section (z=0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebrisGenerator {
    
    private static final int DEBRIS_CROSS_SECTION_RATIO = 5; // 1 out of this many debris are created in the ice cross-section
    private static final double MIN_GLACIER_LENGTH = 1; // meter
    private static final double MIN_GLACIER_THICKNESS = 1; // meter

    private final Glacier _glacier;
    private final Random _randomDebrisX, _randomDebrisY, _randomDebrisZ;
    private int _count;
    
    public DebrisGenerator( Glacier glacier ) {
        _glacier = glacier;
        _randomDebrisX = new Random();
        _randomDebrisY = new Random();
        _randomDebrisZ = new Random();
        _count = 0;
    }
    
    public void generateDebrisPosition( Point3D pOutput ) {
        
        // glacier must have some minimum length...
        if ( _glacier.getLength() > MIN_GLACIER_LENGTH ) {
            
            // x - distance, between valley headwall terminus
            final double minX = _glacier.getValley().getHeadwallPositionReference().getX() + ( 0.1 * MIN_GLACIER_LENGTH );
            final double maxX = _glacier.getTerminusX() - ( 0.1 * MIN_GLACIER_LENGTH );
            final double x = minX + _randomDebrisX.nextDouble() * ( maxX - minX );
            
            // if the ice is some minimum thickness at x ...
            final double valleyElevation = _glacier.getValley().getElevation( x );
            final double glacierSurfaceElevation = _glacier.getSurfaceElevation( x );
            if ( glacierSurfaceElevation - valleyElevation > MIN_GLACIER_THICKNESS ) {
                
                // y - elevation, between valley floor and glacier surface
                final double minY = valleyElevation + ( 0.1 * MIN_GLACIER_THICKNESS ); // slightly above valley floor
                final double maxY = glacierSurfaceElevation - ( 0.1 * MIN_GLACIER_THICKNESS ); // slightly below glacier's surface
                final double y = minY + _randomDebrisY.nextDouble() * ( maxY - minY );
                assert ( y > valleyElevation && y < glacierSurfaceElevation );

                // z - distance across the width of the valley floor
                double z = 0; // in the cross-section
                if ( _count % DEBRIS_CROSS_SECTION_RATIO != 0 ) {
                    // not in the cross-section
                    final double perspectiveHeight = Valley.getPerspectiveHeight();
                    z = _randomDebrisZ.nextDouble() * perspectiveHeight;
                }
                pOutput.setLocation( x, y, z );

                _count++;
            }
        }
    }
}
