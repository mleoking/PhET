/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.util.SimStrings;

import java.text.DecimalFormat;

/**
 * CurrentSlider
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CurrentSlider extends ModelSlider {
    private double maxCurrent;

    public CurrentSlider( double maxCurrent ) {
        super( SimStrings.get( "Controls.ElectronProductionRate" ),
               SimStrings.get( "Controls.ElectronProductionRateUnits" ),
               0, 100, 0,
               new DecimalFormat( "#" ),
               new DecimalFormat( "#" ) );
        this.maxCurrent = maxCurrent;

        setMajorTickSpacing( 25 );
        setNumMinorTicksPerMajorTick( 1 );
        setPaintLabels( true );
        setPreferredSliderWidth( 150 );
    }

    public void setMaxCurrent( double maxCurrent ) {
        this.maxCurrent = maxCurrent;
    }

    public double getValue() {
        return super.getValue() * maxCurrent / 100;
    }
}
