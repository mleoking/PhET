/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;


public class DielectricConstantControl extends JPanel {

    private final LinearValueControl slider;
    
    public DielectricConstantControl() {
        setBorder( new EtchedBorder() );
        
        double min = CLConstants.DIELECTRIC_CONSTANT_RANGE.getMin();
        double max = CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax();
        double value = CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault();
        String label = CLStrings.LABEL_DIELECTRIC_CONSTANT;
        String textFieldPattern = "#0.000";
        String units = "";
        slider = new LinearValueControl( min, max, label, textFieldPattern, units );
        slider.setValue( value );
        slider.setTickPattern( "0" );
        
        add( slider );
    }
}
