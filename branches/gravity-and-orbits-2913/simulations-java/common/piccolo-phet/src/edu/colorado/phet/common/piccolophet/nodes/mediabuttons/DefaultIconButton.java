// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

public class DefaultIconButton extends IconButton {
    protected PhetPPath iconNode;
    private ArrayList listeners = new ArrayList();

    public DefaultIconButton( int buttonHeight, Shape shape ) {
        super( buttonHeight );
        iconNode = new PhetPPath( shape, Color.BLACK, new BasicStroke( 1 ), Color.LIGHT_GRAY );
        addChild( iconNode );

        // this handler ensures that the button won't fire unless the mouse is released while inside the button
        ButtonEventHandler handler = new ButtonEventHandler();
        addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventHandler.ButtonEventAdapter() {
            public void fire() {
                if ( isEnabled() ) {
                    notifyListeners();
                }
            }
        } );
    }

    protected void updateImage() {
        super.updateImage();
        iconNode.setPaint( isEnabled() ? Color.black : Color.gray );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).buttonPressed();
        }
    }

    public static interface Listener {
        void buttonPressed();
    }
}
