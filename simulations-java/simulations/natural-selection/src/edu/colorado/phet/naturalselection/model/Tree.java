package edu.colorado.phet.naturalselection.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;

public class Tree {

    private final double backgroundX;
    private final double backgroundY;
    private final double baseScale;

    private final Point3D position;

    public Tree( NaturalSelectionModel model, double backgroundX, double backgroundY, double baseScale ) {
        this.backgroundX = backgroundX;
        this.backgroundY = backgroundY;
        this.baseScale = baseScale;

        position = model.getLandscape().landscapeToModel( backgroundX, backgroundY );

        if ( position.getZ() < Landscape.NEARPLANE || position.getZ() > Landscape.FARPLANE ) {
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