// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

/**
 * GPSReceiver is the model of a GPS receiver.
 * It displays distance and elevation coordinates.
 * Distance is relative to the glacier's head.
 * Elevation is relative to sea level.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GPSReceiver extends AbstractTool  {

    public GPSReceiver( Point2D position ) {
        super( position );
    }
}
