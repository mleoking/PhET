// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.nodes.WavelengthControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Piccolo control for changing the wavelength of light.  It has a textual readout as well as a slider with a gradient background.
 *
 * @author Sam Reid
 */
public class BendingLightWavelengthControl extends PNode {
    public BendingLightWavelengthControl( final Property<Double> wavelengthProperty, final Property<LaserColor> laserColor ) {
        final WavelengthControl wavelengthControl = new WavelengthControl( 150, 27, VisibleColor.MIN_WAVELENGTH, 700 ) {{//only go to 700nm because after that the reds are too black
            final PNode wc = this;
            setWavelength( wavelengthProperty.getValue() * 1E9 );
            laserColor.addObserver( new SimpleObserver() {
                public void update() {
                    final boolean disabled = laserColor.getValue() == LaserColor.WHITE_LIGHT;
                    wc.setTransparency( disabled ? 0.3f : 1f );
                    setPickable( !disabled );
                    setChildrenPickable( !disabled );
                    if ( !disabled ) {
                        setWavelength( laserColor.getValue().getWavelength() * 1E9 );
                    }
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    wavelengthProperty.setValue( getWavelength() / 1E9 );
                }
            } );
        }};
        PBounds bounds = wavelengthControl.getFullBounds();
        wavelengthControl.setOffset( -bounds.getX(), -bounds.getY() );
        addChild( wavelengthControl );
    }
}
