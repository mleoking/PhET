/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * PositionAxis is the common x-axis for position.
 * This axis displays integer tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSPositionAxis extends NumberAxis {

    public BSPositionAxis() {
        String label = SimStrings.get( "axis.position" ) + " (" + SimStrings.get( "units.position" ) + ")";
        setLabel( label );
        setLabelFont( BSConstants.AXIS_LABEL_FONT );
        setRange( BSConstants.POSITION_VIEW_RANGE );
        TickUnits xUnits = (TickUnits) NumberAxis.createIntegerTickUnits();
        setStandardTickUnits( xUnits );
    }
}
