// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.control;

import edu.colorado.phet.beerslawlab.BLLConstants;
import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLSimSharing.Parameters;
import edu.colorado.phet.beerslawlab.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.model.Evaporator;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragSequenceEventHandler.DragFunction;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * Evaporation control, appropriated from sugar-and-salt-solutions.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EvaporationControlNode extends ControlPanelNode {

    public EvaporationControlNode( final Evaporator evaporator ) {
        super( new HBox(

                // Label
                new PText( Strings.EVAPORATION ) {{
                    setFont( new PhetFont( BLLConstants.CONTROL_FONT_SIZE ) );
                }},

                // Slider
                new HSliderNode( 0, evaporator.maxEvaporationRate, evaporator.evaporationRate, evaporator.enabled ) {{

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

                    // sim-sharing
                    getDragHandler().setStartEndDragFunction( new DragFunction() {
                        public void apply( UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                            SimSharingManager.sendUserMessage( UserComponents.evaporationSlider, action, param( Parameters.evaporationRate, evaporator.evaporationRate.get() ) );
                        }
                    } );
                }}
        ) );
    }
}
