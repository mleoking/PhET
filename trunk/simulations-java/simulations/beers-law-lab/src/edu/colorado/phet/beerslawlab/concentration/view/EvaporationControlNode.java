// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.concentration.model.Evaporator;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Evaporation control, adapted from sugar-and-salt-solutions.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class EvaporationControlNode extends ControlPanelNode {

    public EvaporationControlNode( final Evaporator evaporator ) {
        super( new HBox(

                // Label
                new PText( Strings.EVAPORATION ) {{
                    setFont( new PhetFont( BLLConstants.CONTROL_FONT_SIZE ) );
                }},

                // Slider
                new HSliderNode( UserComponents.evaporationSlider, 0, evaporator.maxEvaporationRate, evaporator.evaporationRate, evaporator.enabled ) {{

                    // Tick labels
                    PhetFont tickFont = new PhetFont( BLLConstants.TICK_LABEL_FONT_SIZE );
                    addLabel( 0, new PhetPText( Strings.NONE, tickFont ) );
                    addLabel( evaporator.maxEvaporationRate, new PhetPText( Strings.LOTS, tickFont ) );

                    // Set rate to zero when slider is released.
                    this.addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mouseReleased( PInputEvent event ) {
                            evaporator.evaporationRate.set( 0.0 );
                        }
                    } );
                }}
        ) );
    }
}
