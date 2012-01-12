// Copyright 2002-2011, University of Colorado

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
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.BSResources;


/**
 * BSPositionAxis is the common x-axis for position.
 * This axis displays integer tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSPositionAxis extends NumberAxis {

    public BSPositionAxis() {
        
        setRange( BSConstants.POSITION_VIEW_RANGE );
        
        String label = BSResources.getString( "axis.position" ) + " (" + BSResources.getString( "units.position" ) + ")";
        setLabel( label );
        setLabelFont( BSConstants.AXIS_LABEL_FONT );
        setTickLabelFont( BSConstants.AXIS_TICK_LABEL_FONT );
        
        // Tick units
        TickUnits tickUnits = new TickUnits();
        tickUnits.add( new NumberTickUnit( BSConstants.POSITION_TICK_SPACING, BSConstants.POSITION_TICK_FORMAT ) );
        setStandardTickUnits( tickUnits );
        setAutoTickUnitSelection( true );
    }
}
