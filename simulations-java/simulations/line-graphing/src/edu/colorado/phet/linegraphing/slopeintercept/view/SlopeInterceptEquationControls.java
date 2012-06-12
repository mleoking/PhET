// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.EquationControls;

/**
 * Control panel for interacting with a line's equation in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationControls extends EquationControls {

    /**
     * Constructor
     * @param interactiveLine the line that can be manipulated by the user
     * @param savedLines lines that have been saved by the user
     * @param maximized is the control panel maximized (true) or minimized (false)?
     * @param linesVisible are lines visible on the graph?
     * @param riseRange
     * @param runRange
     * @param yInterceptRange
     */
    public SlopeInterceptEquationControls( final Property<StraightLine> interactiveLine,
                                           final ObservableList<StraightLine> savedLines,
                                           final Property<Boolean> maximized,
                                           final Property<Boolean> linesVisible,
                                           Property<DoubleRange> riseRange,
                                           Property<DoubleRange> runRange,
                                           Property<DoubleRange> yInterceptRange ) {
        super( MessageFormat.format( "{0} = {1}{2} + {3}", /* y = mx + b */
                                     Strings.SYMBOL_Y, Strings.SYMBOL_SLOPE, Strings.SYMBOL_X, Strings.SYMBOL_INTERCEPT ),
               interactiveLine, savedLines, maximized, linesVisible,
               new SlopeInterceptEquationNode( interactiveLine, riseRange, runRange, yInterceptRange ) );
    }
}
