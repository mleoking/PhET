// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Shrub model
 *
 * @author Jonathan Olson
 */
public class Shrub {

    private final double backgroundX;
    private final double backgroundY;
    private final double baseScale;

    private final Point3D position;

    /**
     * Creates a shrub with a particular pixel position on the background and scale
     *
     * @param model       The model
     * @param backgroundX Background x of shrub
     * @param backgroundY Background y of shrub
     * @param baseScale   Scale of shrub (1 is regular size)
     */
    public Shrub( NaturalSelectionModel model, double backgroundX, double backgroundY, double baseScale ) {
        this.backgroundX = backgroundX;
        this.backgroundY = backgroundY;
        this.baseScale = baseScale;

        position = model.getLandscape().landscapeToModel( backgroundX, backgroundY );

        // make sure bunnies can get to the shrub. otherwise we have problems when the selection mode is food
        if ( position.getZ() < Landscape.NEARPLANE || position.getZ() > Landscape.FARPLANE ) {
            new RuntimeException( "Shrub z out of range: " + position.getZ() ).printStackTrace();
        }
    }

    public double getBackgroundX() {
        return backgroundX;
    }

    public double getBackgroundY() {
        return backgroundY;
    }

    public double getBaseScale() {
        return baseScale;
    }

    public Point3D getPosition() {
        return position;
    }
}
