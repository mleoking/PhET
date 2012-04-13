// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.FILL;
import static edu.colorado.phet.fluidpressureandflow.watertower.view.WaterTowerCanvas.FLOATING_BUTTON_FONT_SIZE;

/**
 * Button to fill the water tank, enabled if the tank is not already full.
 *
 * @author Sam Reid
 */
public class FillTankButton extends TextButtonNode {
    public FillTankButton( ObservableProperty<Boolean> full, final VoidFunction0 fill, final Property<ImmutableVector2D> panelOffset ) {
        super( FILL, new PhetFont( FLOATING_BUTTON_FONT_SIZE ), FluidPressureControlPanel.BACKGROUND );
        setUserComponent( UserComponents.fillButton );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fill.apply();
            }
        } );

        //Disable the button if the tank is full
        //But only if it won't immediately become non-full, which would also disarm the button and make repeated clicks not work
        full.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean full ) {
                boolean doorOpen = panelOffset.get().getY() > 0;
                if ( isEnabled() && full && !doorOpen ) {
                    setEnabled( false );
                }
                else if ( !isEnabled() && !full ) {
                    setEnabled( true );
                }
            }
        } );
    }
}