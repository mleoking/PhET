// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Tree model
 *
 * @author Jonathan Olson
 */
public class Tree {

    private final double backgroundX;
    private final double backgroundY;
    private final double baseScale;

    private final Point3D position;

    /**
     * Creates a tree with a particular pixel position on the background and scale
     *
     * @param model       The model
     * @param backgroundX Background x of shrub
     * @param backgroundY Background y of shrub
     * @param baseScale   Scale of tree (1 is regular size)
     */
    public Tree( NaturalSelectionModel model, double backgroundX, double backgroundY, double baseScale ) {
        this.backgroundX = backgroundX;
        this.backgroundY = backgroundY;
        this.baseScale = baseScale;

        position = model.getLandscape().landscapeToModel( backgroundX, backgroundY );

        if ( position.getZ() < Landscape.NEARPLANE || position.getZ() > Landscape.FARPLANE ) {
            // don't cause an error, we can have trees that bunnies can't reach
            //new RuntimeException( "Tree z out of range: " + position.getZ() ).printStackTrace();
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