// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;

/**
 * Class that represents a 2D space that can come into contact with other
 * thermal areas.  This is basically just a shape and a flag that indicates
 * whether immersion can occur (such as when
 *
 * @author John Blanco
 */
public class ThermalContactArea {

    private final Rectangle2D bounds = new Rectangle2D.Double();

    public ThermalContactArea( Rectangle2D bounds, double initialEnergy ) {
        this.bounds.setFrame( bounds );
    }
}
