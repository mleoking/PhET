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

import javax.swing.*;
import java.text.DecimalFormat;
import java.awt.*;

/**
 * CurrentSlider
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CurrentSlider extends JSlider {
//public class CurrentSlider extends ModelSlider {
    private double maxCurrent;
    JSlider slider;

    public CurrentSlider( double maxCurrent ) {
        super( 0, (int)maxCurrent, 0 );
//        super( SimStrings.get( "Controls.ElectronProductionRate" ),
//               SimStrings.get( "Controls.ElectronProductionRateUnits" ),
//               0, 100, 0,
//               new DecimalFormat( "#" ),
//               new DecimalFormat( "#" ) );
        System.out.println("maxCurrent = " + maxCurrent);
        this.maxCurrent = maxCurrent;

        setMajorTickSpacing( 25 );
//        setNumMinorTicksPerMajorTick( 1 );
        setPaintLabels( true );
//        setPreferredSliderWidth( 150 );

//        setTextFieldVisible( false );
        setPaintLabels( false );
        setPaintTicks(false );
        setBorder( null);
        setPreferredSize( new Dimension( 150, 30));
    }

    public void setMaxCurrent( double maxCurrent ) {
        this.maxCurrent = maxCurrent;
    }

    public double getPctMax() {
        return getValue() / maxCurrent;
    }
    
//    public int getValue() {
//        return (int)(super.getValue() * maxCurrent / 100);
//    }
//    public double getValue() {
//        return super.getValue() * maxCurrent / 100;
//    }

//    public void  setValue( double value ) {
//        System.out.println("value = " + value);
//        super.setValue( (int)value );
//    }
}
