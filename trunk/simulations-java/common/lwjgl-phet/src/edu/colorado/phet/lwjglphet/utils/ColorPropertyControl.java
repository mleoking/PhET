package edu.colorado.phet.lwjglphet.utils;

import java.awt.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;

/**
 * Convenience control that handles a color property in the LWJGL thread, while the interface is handled in the Swing EDT. Otherwise, the access
 * to the color property would not be properly synchronized and code from both threads could access/modify it at the same time for undefined behavior.
 * <p/>
 * See lwjgl-implementation-notes.txt for more detailed notes on threading issues.
 */
public class ColorPropertyControl extends ColorControl {
    public ColorPropertyControl( Frame parentFrame, String labelString, final Property<Color> color ) {
        super( parentFrame, labelString, color.get() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                LWJGLUtils.invoke( new Runnable() {
                    public void run() {
                        color.set( getColor() );
                    }
                } );
            }
        } );
    }
}