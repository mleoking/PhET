/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import java.util.Hashtable;

import javax.swing.JLabel;

import edu.colorado.phet.common.view.util.SimStrings;


public class EnergyControl extends IntegerSliderControl {

    public EnergyControl( int min, int max, int value ) {
        super( SimStrings.get( "label.energy"), min, max, value );
        
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( min ), new JLabel( SimStrings.get( "label.minEnergy") ) );
        labelTable.put( new Integer( max ), new JLabel( SimStrings.get( "label.maxEnergy") ) );
        getSlider().setLabelTable( labelTable );
    }
}
