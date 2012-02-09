// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.platetectonics.model.PlateMotionPlate;

public abstract class PlateBehavior {
    public final PlateMotionPlate plate;
    public final PlateMotionPlate otherPlate;

    public PlateBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        this.plate = plate;
        this.otherPlate = otherPlate;
    }

    public abstract void stepInTime( float millionsOfYears );

    public PlateMotionPlate getOtherPlate() {
        return otherPlate;
    }

    public PlateMotionPlate getPlate() {
        return plate;
    }
}
