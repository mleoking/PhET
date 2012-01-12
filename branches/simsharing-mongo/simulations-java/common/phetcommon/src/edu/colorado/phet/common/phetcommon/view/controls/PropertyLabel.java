// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;

/**
 * Pressing this label sets a property value.
 * This is useful for icons that are associated with Swing controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertyLabel<T> extends JLabel {

    public PropertyLabel( final IUserComponent simSharingObject, Icon icon, final Property<T> property, final T value ) {
        super( icon );
        addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                SimSharingManager.sendUserMessage( simSharingObject, UserActions.pressed );
                property.set( value );
            }
        } );
    }
}
