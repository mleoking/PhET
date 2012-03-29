// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.piccolophet.nodes.ToggleButtonNode;

/**
 * Minimize/maximize button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MinimizeMaximizeButtonNode extends ToggleButtonNode {

    public MinimizeMaximizeButtonNode( final IUserComponent userComponent, final Property<Boolean> onProperty ) {
        super( userComponent, onProperty, PhetCommonResources.getImage( "buttons/minimizeButton.png" ), PhetCommonResources.getImage( "buttons/maximizeButton.png" ), new Property<Boolean>( true ),
               PhetCommonResources.getImage( "buttons/minimizeButton.png" ), //TODO use a real disabled image
               PhetCommonResources.getImage( "buttons/maximizeButton.png" ) ); //TODO use a real disabled image
    }
}
