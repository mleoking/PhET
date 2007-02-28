/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.control;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * KspSlider
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class KspSlider extends ModelSlider {

    private double minValue = 0;
    private double maxValue = 3E-16;

    public KspSlider( final SolubleSaltsModel model ) {
        super( "Ksp", "", 0, 3E-16, 0 );
        final DecimalFormat kspFormat = new DecimalFormat( "0.00E00" );
        setSliderLabelFormat( kspFormat );
        setTextFieldFormat( kspFormat );
        setNumMajorTicks( 3 );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setKsp( getValue() );
            }
        } );
        model.setKsp( getValue() );
    }
}
