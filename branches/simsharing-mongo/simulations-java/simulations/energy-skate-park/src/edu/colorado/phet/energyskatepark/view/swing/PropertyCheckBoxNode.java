// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.simsharing.SimSharingPropertyCheckBox;
import edu.colorado.phet.energyskatepark.basics.EnergySkateParkBasicsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author John Blanco
 */

public class PropertyCheckBoxNode extends PNode {

    private static final Font LABEL_FONT = EnergySkateParkBasicsModule.CONTROL_FONT;

    public PropertyCheckBoxNode( IUserComponent userComponent, String text, final BooleanProperty property ) {

        // Create the check box, which is a Swing component.
        SimSharingPropertyCheckBox checkBox = new SimSharingPropertyCheckBox( userComponent, text, property ) {{
            setFont( LABEL_FONT );
        }};

        // Wrap the check box in a node and add it.
        addChild( new PSwing( checkBox ) );
    }
}
