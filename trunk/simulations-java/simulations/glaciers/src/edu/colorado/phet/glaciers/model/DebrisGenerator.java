/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.glaciers.view.MountainsAndValleyNode;

/**
 * DebrisGenerator generates a 3D position for debris, 
 * based on the current state of the glacier.
 * Points are weighted so that more of them are generated in the cross-section (z=0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebrisGenerator {
    
    private static final int DEBRIS_CROSS_SECTION_RATIO = 5; // 1 out of this many debris are created in the ice cross-section

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
        
        if ( _glacier.getLength() > 0 ) {
            
            // x - distance between valley headwall and ELA (or terminus)
            final double minX = _glacier.getValley().getHeadwallPositionReference().getX();
            Point2D surfaceAtSteadyStateELA = _glacier.getSurfaceAtSteadyStateELAReference();
            double maxX = 0;
            if ( surfaceAtSteadyStateELA != null ) {
                maxX = _glacier.getSurfaceAtSteadyStateELAReference().getX();
            }
            else {
                maxX = _glacier.getTerminusX();
            }
            final double x = minX + _randomDebrisX.nextDouble() * ( maxX - minX );
            
            // y - elevation
            final double minY = _glacier.getValley().getElevation( x ) + 1;
            final double maxY = _glacier.getSurfaceElevation( x ) - 1;
            final double y = minY + _randomDebrisY.nextDouble() * ( maxY - minY );
            
            // z - distance across the width of the valley floor
            double z = 0; // in the cross-section
            if ( _count % DEBRIS_CROSS_SECTION_RATIO != 0 ) {
                // not in the cross-section
                final double valleyWidth = MountainsAndValleyNode.getPerspectiveHeight();
                z = _randomDebrisZ.nextDouble() * valleyWidth;
            }
            pOutput.setLocation( x, y, z );
            
            _count++;
        }
    }
}
