// Copyright 2002-2011, University of Colorado

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

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.QTResources;


/**
 * PositionAxis is the common x-axis for position.
 * This axis displays integer tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PositionAxis extends NumberAxis {

    public PositionAxis() {
        String label = QTResources.getString( "axis.position" ) + " (" + QTResources.getString( "units.position" ) + ")";
        setLabel( label );
        setLabelFont( QTConstants.AXIS_LABEL_FONT );
        setRange( QTConstants.POSITION_RANGE );
        TickUnits xUnits = (TickUnits) NumberAxis.createIntegerTickUnits();
        setStandardTickUnits( xUnits );
    }
}
