// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.model.labels;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;

public class RangeLabel {
    public final Property<ImmutableVector3F> top;
    public final Property<ImmutableVector3F> bottom;
    public final String label;
    public final PlateMotionPlate plate;

    private boolean limitToScreen = false;

    public RangeLabel( Property<ImmutableVector3F> top, Property<ImmutableVector3F> bottom, String label, PlateMotionPlate plate ) {
        this.top = top;
        this.bottom = bottom;
        this.label = label;
        this.plate = plate;
    }

    public boolean isLimitToScreen() {
        return limitToScreen;
    }

    public void setLimitToScreen( boolean limitToScreen ) {
        this.limitToScreen = limitToScreen;
    }
}
