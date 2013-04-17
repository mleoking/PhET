// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsSimSharing.UserComponents;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerAndShakerCanvas.CONTROL_FONT;

/**
 * Piccolo control for setting and viewing the evaporation rate, in a white control panel and appearing beneath the beaker.
 *
 * @author Sam Reid
 */
public class EvaporationSlider extends WhiteControlPanelNode {
    public EvaporationSlider( final SettableProperty<Double> evaporationRate, final DoubleProperty waterVolume, ObservableProperty<Boolean> clockRunning ) {
        super( new HBox(

                //Add a label
                new PText( EVAPORATION ) {{setFont( CONTROL_FONT );}},

                //Add the slider
                new HSliderNode( UserComponents.evaporationSlider, 0, 100, evaporationRate, waterVolume.greaterThan( 0.0 ).and( clockRunning ) ) {{
                    this.addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mouseReleased( PInputEvent event ) {
                            evaporationRate.set( 0.0 );
                        }
                    } );

                    //Show labels for "none" and "lots"
                    addLabel( 0.0, new PhetPText( NONE, CONTROL_FONT ) );
                    addLabel( 100.0, new PhetPText( LOTS, CONTROL_FONT ) );
                }}
        ) );
    }
}