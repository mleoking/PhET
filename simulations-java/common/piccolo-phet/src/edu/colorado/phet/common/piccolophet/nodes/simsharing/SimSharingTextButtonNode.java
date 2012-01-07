// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.simsharing;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;

/**
 * Text button node that also sends sim sharing messages.
 *
 * @author Sam Reid
 */
public class SimSharingTextButtonNode extends TextButtonNode {
    private final UserComponent userComponent;

    public SimSharingTextButtonNode( UserComponent userComponent, String text ) {
        super( text );
        this.userComponent = userComponent;
    }

    public SimSharingTextButtonNode( UserComponent userComponent, String text, final Font font ) {
        super( text, font );
        this.userComponent = userComponent;
    }

    public SimSharingTextButtonNode( UserComponent userComponent, String text, Font font, Color background ) {
        super( text, font, background );
        this.userComponent = userComponent;
    }

    @Override protected void notifyActionPerformed() {
        SimSharingManager.sendUserEvent( userComponent, UserActions.pressed );
        super.notifyActionPerformed();
    }
}