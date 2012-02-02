// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.common.BLLResources;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * A 2cm ruler.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLRulerNode extends RulerNode {

    public BLLRulerNode( double length ) {
        super( length, 40, new String[] { "0", "1", "2" }, Strings.UNITS_CENTIMETERS, 9, 12 );//TODO i18n
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( UserComponents.ruler, UserComponentTypes.sprite ) {
            @Override protected void drag( PInputEvent event ) {
                super.drag( event );
                //TODO constrain to play area
                setOffset( getXOffset() + event.getDelta().getWidth(), getYOffset() + event.getDelta().getHeight() );
            }
        } );
    }
}
