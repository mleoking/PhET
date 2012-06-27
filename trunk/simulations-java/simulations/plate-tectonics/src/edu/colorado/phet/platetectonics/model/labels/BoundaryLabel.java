package edu.colorado.phet.platetectonics.model.labels;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.util.Side;

public class BoundaryLabel {
    public final Boundary boundary;
    public final Side side;

    public final Property<Float> minX = new Property<Float>( Float.NEGATIVE_INFINITY );
    public final Property<Float> maxX = new Property<Float>( Float.POSITIVE_INFINITY );

    // TODO: add text labels if necessary

    public BoundaryLabel( Boundary boundary, Side side ) {
        this.boundary = boundary;
        this.side = side;
    }

    public boolean isReversed() {
        return false;
    }

    public Boundary getBoundary() {
        return boundary;
    }
}
