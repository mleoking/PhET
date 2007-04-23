/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;


public class LogarithmicValueControl extends AbstractValueControl {

    public LogarithmicValueControl( double min, double max, String label, String textFieldPattern, String units ) {
        super( new LogarithmicSlider( min, max ), label, textFieldPattern, units );
    }
    
}
