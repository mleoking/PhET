// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Frame;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;

import com.jme3.math.ColorRGBA;

/**
 * Color control for ColorRGBA properties
 */
public class ColorRGBAPropertyControl extends ColorControl {
    public ColorRGBAPropertyControl( Frame parentFrame, String labelString, final Property<ColorRGBA> color ) {
        super( parentFrame, labelString, JMEUtils.convertColor( color.get() ) );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JMEUtils.invoke( new Runnable() {
                    public void run() {
                        color.set( JMEUtils.convertColor( getColor() ) );
                    }
                } );
            }
        } );
    }
}
