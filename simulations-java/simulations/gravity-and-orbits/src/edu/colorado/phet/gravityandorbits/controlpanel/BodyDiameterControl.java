package edu.colorado.phet.gravityandorbits.controlpanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.gravityandorbits.model.Body;

/**
 * @author Sam Reid
 */
public class BodyDiameterControl extends JPanel {
    public BodyDiameterControl( final Body body ) {
        //todo: add icon for the object
        final LinearValueControl control = new LinearValueControl( 0, 100, body.getDiameter(), body.getName(), "0.00", "" );
        body.getDiameterProperty().addObserver( new SimpleObserver() {
            public void update() {
                control.setValue( body.getDiameter() );
            }
        } );
        control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                body.setDiameter( control.getValue() );
            }
        } );
        control.setTextFieldVisible( false );
        add( control );
    }
}
