// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

import java.awt.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;

/**
 * Color control for Color properties
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
