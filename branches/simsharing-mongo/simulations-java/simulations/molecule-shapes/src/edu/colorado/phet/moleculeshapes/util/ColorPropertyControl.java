// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

import java.awt.Color;
import java.awt.Frame;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.jmephet.JMEUtils;

/**
 * Color control for Color properties
 */
public class ColorPropertyControl extends ColorControl {
    public ColorPropertyControl( Frame parentFrame, String labelString, final Property<Color> color ) {
        super( parentFrame, labelString, color.get() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JMEUtils.invoke( new Runnable() {
                    public void run() {
                        color.set( getColor() );
                    }
                } );
            }
        } );
    }
}
