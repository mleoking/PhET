// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;

/**
 * Swing check box that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJCheckBox extends JCheckBox {

    private final IUserComponent userComponent;

    public SimSharingJCheckBox( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, Icon icon ) {
        super( icon );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, Icon icon, boolean selected ) {
        super( icon, selected );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, String text ) {
        super( text );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, Action a ) {
        super( a );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, String text, boolean selected ) {
        super( text, selected );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, String text, Icon icon ) {
        super( text, icon );
        this.userComponent = userComponent;
    }

    public SimSharingJCheckBox( IUserComponent userComponent, String text, Icon icon, boolean selected ) {
        super( text, icon, selected );
        this.userComponent = userComponent;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserMessage( userComponent,
                                           pressed,
                                           componentType( ComponentTypes.checkBox ).param( ParameterKeys.isSelected, isSelected() ) );
        super.fireActionPerformed( event );
    }
}
