/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.WaveInterferenceModelUnits;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: May 22, 2006
 * Time: 6:22:32 PM
 * Copyright (c) May 22, 2006 by Sam Reid
 */

public class WaveInterferenceScreenUnits {
    private WaveInterferenceModelUnits modelUnits;
    private LatticeScreenCoordinates latticeScreenCoordinates;

    public WaveInterferenceScreenUnits( WaveInterferenceModelUnits modelUnits, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.modelUnits = modelUnits;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
    }

    public String getDistanceUnits() {
        return modelUnits.getDistanceUnits();
    }

    public Dimension getGridSize() {
        return latticeScreenCoordinates.getGridSize();
    }

    //the mapping from cell to physical distance is
    double cellsToPhysicalX( int cells ) {
        return ( (double)cells ) / getGridSize().width * getPhysicalWidth();
    }

    private double getPhysicalWidth() {
        return modelUnits.getPhysicalWidth();
    }

    double cellsToPhysicalY( int cells ) {
        return ( (double)cells ) / latticeScreenCoordinates.getGridSize().height * modelUnits.getPhysicalHeight();
    }

    public Hashtable toHashtable( int[] doubles, double scale ) {//a hack for distance separation of sources.
        //since the distance is from the midline.
        Hashtable table = new Hashtable();
        DecimalFormat decimalFormat = new DecimalFormat( "#.#" );
        for( int i = 0; i < doubles.length; i++ ) {
            int aDouble = doubles[i];
            String str = decimalFormat.format( cellsToPhysicalX( aDouble ) * scale );
            if( i == 0 ) {
                str += " " + getDistanceUnits();
            }
            table.put( new Double( aDouble ), new JLabel( str ) );
        }
        return table;
    }

    public Hashtable toHashtable( int[] doubles ) {
        Hashtable table = new Hashtable();
        DecimalFormat decimalFormat = new DecimalFormat( "#.#" );
        for( int i = 0; i < doubles.length; i++ ) {
            int aDouble = doubles[i];
            String str = decimalFormat.format( cellsToPhysicalX( aDouble ) );
            if( i == 0 ) {
                str += " " + getDistanceUnits();
            }
            table.put( new Double( aDouble ), new JLabel( str ) );
        }
        return table;
    }
}
