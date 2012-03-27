// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIcon;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Property check box with associated icon.
 * Clicking either the check box or icon toggles the property.
 * Data-collection message when clicking the icon identifies the component as userComponent.icon.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertyCheckBoxWithIcon extends JPanel {

    public PropertyCheckBoxWithIcon( IUserComponent userComponent, final String text, final PhetFont font, Image image, final SettableProperty<Boolean> property ) {
        add( new PropertyCheckBox( userComponent, text, property ) {{
            setFont( font );
        }} );
        add( new SimSharingIcon( UserComponentChain.chain( userComponent, "icon" ), new ImageIcon( image ), new VoidFunction0() {
            public void apply() {
                property.set( !property.get() );
            }
        } ) );
    }
}
