// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIcon;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Pressing this icon sets a property value.
 * This is useful for icons that are associated with Swing controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertyIcon<T> extends SimSharingIcon {

    public PropertyIcon( final IUserComponent userComponent, Icon icon, final Property<T> property, final T value ) {
        super( userComponent, icon,
               new VoidFunction0() {
                   public void apply() {
                       property.set( value );
                   }
               } );
    }
}
