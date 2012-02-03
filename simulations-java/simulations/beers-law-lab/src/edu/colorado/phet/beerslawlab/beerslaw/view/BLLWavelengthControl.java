// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Dimension;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.WavelengthControl;

/**
 * Adapter class that makes wires piccolo-phet WavelengthControl to a Property<Double> wavelength.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BLLWavelengthControl extends WavelengthControl {

    public BLLWavelengthControl( Dimension trackSize, final Property<Double> wavelength ) {
        super( trackSize.width, trackSize.height );

        // set the model value when the control is changed
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                wavelength.set( getWavelength() );
            }
        } );

        // set the control value when the model is changed
        wavelength.addObserver( new VoidFunction1<Double>() {
            public void apply( Double wavelength ) {
                setWavelength( wavelength );
            }
        });
    }
}
