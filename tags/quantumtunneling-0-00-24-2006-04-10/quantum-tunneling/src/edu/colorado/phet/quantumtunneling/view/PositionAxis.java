/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * PositionAxis is the common x-axis for position.
 * This axis displays integer tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PositionAxis extends NumberAxis {

    public PositionAxis() {
        String label = SimStrings.get( "axis.position" ) + " (" + SimStrings.get( "units.position" ) + ")";
        setLabel( label );
        setLabelFont( QTConstants.AXIS_LABEL_FONT );
        setRange( QTConstants.POSITION_RANGE );
        TickUnits xUnits = (TickUnits) NumberAxis.createIntegerTickUnits();
        setStandardTickUnits( xUnits );
    }
}
