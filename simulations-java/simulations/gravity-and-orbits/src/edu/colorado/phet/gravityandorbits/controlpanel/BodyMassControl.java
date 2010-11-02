package edu.colorado.phet.gravityandorbits.controlpanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.gravityandorbits.model.Body;

/**
 * @author Sam Reid
 */
public class BodyMassControl extends VerticalLayoutPanel {

    public BodyMassControl( final Body body, double min, double max ) {
        final Function.LinearFunction modelToView = new Function.LinearFunction( min, max, 0, 100 );

        //todo: add icon for the object
        add( new JLabel( body.getName() ) {{
            setFont( GravityAndOrbitsControlPanel.CONTROL_FONT );
            setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
            setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
        }} );
        setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
        setBackground( GravityAndOrbitsControlPanel.BACKGROUND );

        add( new JSlider() {{
            setMinorTickSpacing( 0 );
            setMajorTickSpacing( 0 );
            setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
            setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
            body.getDiameterProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setValue( (int) modelToView.evaluate( body.getMass() ) );//todo: will this clamp create problems?
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    body.setMass( modelToView.createInverse().evaluate( getValue() ) );
                }
            } );
        }} );
    }
}
