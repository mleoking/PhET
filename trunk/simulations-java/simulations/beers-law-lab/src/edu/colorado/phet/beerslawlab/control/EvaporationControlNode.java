// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.control;

import edu.colorado.phet.beerslawlab.BLLConstants;
import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Evaporation control, appropriated from sugar-and-salt-solutions.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EvaporationControlNode extends ControlPanelNode {

    public EvaporationControlNode( final double maxRate, final Property<Double> evaporationRate ) {
        super( new HBox(

                // Label
                new PText( Strings.EVAPORATION ) {{
                    setFont( new PhetFont( BLLConstants.CONTROL_FONT_SIZE ) );
                }},

                // Slider
                new HSliderNode( 0, maxRate, evaporationRate ) {{

                    // Tick labels
                    PhetFont tickFont = new PhetFont( BLLConstants.TICK_LABEL_FONT_SIZE );
                    addLabel( 0, new PhetPText( Strings.NONE, tickFont ) );
                    addLabel( maxRate, new PhetPText( Strings.LOTS, tickFont ) );

                    // Set rate to zero when slider is released.
                    this.addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mouseReleased( PInputEvent event ) {
                            evaporationRate.set( 0.0 );
                        }
                    } );
                }}
        ) );
    }
}
