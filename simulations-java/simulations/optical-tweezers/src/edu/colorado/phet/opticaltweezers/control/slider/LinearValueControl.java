/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;


public class LinearValueControl extends AbstractValueControl {

    public LinearValueControl( double min, double max, String label, String textFieldPattern, String units ) {
        super( new LinearSlider( min, max ), label, textFieldPattern, units );
    }

}
