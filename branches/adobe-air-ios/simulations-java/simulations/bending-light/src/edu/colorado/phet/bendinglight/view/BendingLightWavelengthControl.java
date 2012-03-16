// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.bendinglight.BendingLightSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.WavelengthControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.bendinglight.view.LaserColor.WHITE_LIGHT;
import static edu.colorado.phet.common.phetcommon.view.util.VisibleColor.MIN_WAVELENGTH;

/**
 * Piccolo control for changing the wavelength of light.  It has a textual readout as well as a slider with a gradient background.
 *
 * @author Sam Reid
 */
public class BendingLightWavelengthControl extends PNode {
    public BendingLightWavelengthControl( final Property<Double> wavelengthProperty, final Property<LaserColor> laserColor ) {
        final WavelengthControl wavelengthControl = new WavelengthControl( UserComponents.wavelengthControl, false, 150, 27, MIN_WAVELENGTH, 700 ) {{//only go to 700nm because after that the reds are too black
            final PNode wc = this;//to access from within closure below
            setWavelength( wavelengthProperty.get() * 1E9 );//Convert between SI and nanometers

            //When the laser color changes or the wavelength changes, update the readout on the control
            laserColor.addObserver( new SimpleObserver() {
                public void update() {
                    final boolean disabled = laserColor.get() == WHITE_LIGHT;
                    wc.setTransparency( disabled ? 0.3f : 1f );
                    setPickable( !disabled );
                    setChildrenPickable( !disabled );
                    if ( !disabled ) {
                        setWavelength( laserColor.get().getWavelength() * 1E9 );//Convert between SI and nanometers
                    }
                }
            } );

            //Set the wavelength when the user drags the slider
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    wavelengthProperty.set( getWavelength() / 1E9 );//Convert between SI and nanometers
                }
            } );
        }};
        PBounds bounds = wavelengthControl.getFullBounds();
        wavelengthControl.setOffset( -bounds.getX(), -bounds.getY() );
        addChild( wavelengthControl );
    }
}
