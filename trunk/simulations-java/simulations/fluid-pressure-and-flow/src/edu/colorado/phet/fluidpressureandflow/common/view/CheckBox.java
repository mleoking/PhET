// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureControlPanel;

/**
 * @author Sam Reid
 */
public class CheckBox extends JCheckBox {
    public CheckBox( String label, final Property<Boolean> property ) {
        super( label, property.getValue() );
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
