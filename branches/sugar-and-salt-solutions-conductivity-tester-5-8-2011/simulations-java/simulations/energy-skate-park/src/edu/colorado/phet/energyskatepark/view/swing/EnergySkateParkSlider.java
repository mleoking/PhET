// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.Hashtable;

/**
 * Author: Sam Reid
 * Apr 27, 2007, 12:50:01 PM
 */
public class EnergySkateParkSlider extends LinearValueControl {
    public EnergySkateParkSlider( String title, String units, double min, double max, double initialValue ) {
        super( min, max, title, "0.00", units );
        super.setValue( min );
        super.setValue( initialValue );
        setMinorTickSpacing( ( max - min ) / 8.0 );
        setMajorTickSpacing( ( max - min ) / 4.0 );
        setBorder( BorderFactory.createEtchedBorder() );
    }

    public void setModelTicks( double[] doubles ) {
        Hashtable hash = new Hashtable();
        for( int i = 0; i < doubles.length; i++ ) {
            double aDouble = doubles[i];
            DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
            hash.put( new Double( aDouble ), new JLabel( decimalFormat.format( aDouble ) ) );
        }
        super.setTickLabels( hash );
    }

    public void setValue( double value ) {
        if( super.getValue() != value ) {
            super.setValue( value );
        }
    }
}
