/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.clock.StopwatchPanel;

/**
 * BSStopwatchPanel is the stopwatch panel for this simulation.
 * It adds no new functionality; it simply sets some visual attributes
 * that make the stop watch look better with this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSStopwatchPanel extends StopwatchPanel {

    public BSStopwatchPanel( IClock clock, String timeUnits, double scaleFactor, NumberFormat timeFormat ) {
        super( clock, timeUnits, scaleFactor, timeFormat );
        
        setBackground( BSConstants.STOPWATCH_BACKGROUND );
        
        Border border = new CompoundBorder( BorderFactory.createEmptyBorder(2,0,2,0), 
                BorderFactory.createLineBorder( Color.BLACK, 1 ) );
        setBorder( border );
    }
}
