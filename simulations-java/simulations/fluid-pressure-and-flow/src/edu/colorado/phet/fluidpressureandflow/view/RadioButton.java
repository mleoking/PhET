package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
* @author Sam Reid
*/
public class RadioButton extends JRadioButton {
    public RadioButton( String label, final Property<Boolean> property ) {
        super( label, property.getValue() );
        setBackground( FluidPressureControlPanel.BACKGROUND );
        setForeground( FluidPressureControlPanel.FOREGROUND );
        setFont( new PhetFont( 16, true ) );
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
