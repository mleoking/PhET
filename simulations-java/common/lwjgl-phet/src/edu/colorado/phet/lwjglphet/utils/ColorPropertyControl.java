package edu.colorado.phet.lwjglphet.utils;

import java.awt.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;

//REVIEW mention why it's necessary to do this in the LWJGL thread. Discussed in lwjgl-implementation-notes.txt, but best to describe close to where doc is needed.

/**
 * Convenience control that handles a color property in the LWJGL thread.
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