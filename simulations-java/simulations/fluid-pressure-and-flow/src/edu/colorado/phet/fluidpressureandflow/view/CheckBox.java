// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class CheckBox extends JCheckBox {
    public CheckBox( String label, final Property<Boolean> property ) {
        super( label, property.getValue() );
        setBackground( FluidPressureControlPanel.BACKGROUND );
        setForeground( FluidPressureControlPanel.FOREGROUND );
        setFont( FluidPressureControlPanel.CONTROL_FONT );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                property.setValue( isSelected() );
            }
        } );
        property.addObserver( new SimpleObserver() {
            public void update() {
                setSelected( property.getValue() );
            }
        } );
    }
}
