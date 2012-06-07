package edu.colorado.phet.platetectonics.model.labels;

import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.util.Side;

public class BoundaryLabel {
    public final Boundary boundary;
    public final Side side;

    // TODO: add text labels if necessary

    public BoundaryLabel( Boundary boundary, Side side ) {
        this.boundary = boundary;
        this.side = side;
    }

    public boolean isReversed() {
        return false;
    }
}
