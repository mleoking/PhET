// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;

/**
 * Model for the "Slope" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeModel extends LineFormsModel {

    public final Property<DoubleRange> x1Range, y1Range, x2Range, y2Range; // ranges of things that the user can manipulate

    public SlopeModel() {
        this( new Line( 1, 2, 3, 4, LGColors.INTERACTIVE_LINE ) );
    }

    private SlopeModel( Line interactiveLine ) {
        super( interactiveLine );

        // ranges
        x1Range = new Property<DoubleRange>( new DoubleRange( graph.xRange ) );
        y1Range = new Property<DoubleRange>( new DoubleRange( graph.yRange ) );
        x2Range = new Property<DoubleRange>( new DoubleRange( graph.xRange ) );
        y2Range = new Property<DoubleRange>( new DoubleRange( graph.yRange ) );

        //NOTE: Unlike slope-intercept and point-slope, ranges do not need to be dynamically adjusted, because the points are free ranging.
    }
}
