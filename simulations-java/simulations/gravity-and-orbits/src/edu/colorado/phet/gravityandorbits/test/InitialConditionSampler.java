// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.test;

import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.module.CartoonModeList;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.colorado.phet.gravityandorbits.module.ModeListParameterList;

import static edu.colorado.phet.gravityandorbits.GAOStrings.CARTOON;

/**
 * This class can help fine tune parameters for use in the cartoon mode, to help make sure you have a stable orbit.
 *
 * @author Sam Reid
 */
public class InitialConditionSampler {

    public static void main( String[] args ) {
        int numSamples = 10;
        double delta = 0.01;
        double startValue = 1 - numSamples / 2 * delta;
        runSim( startValue, delta );
    }

    private static void runSim( final double alpha, final double delta ) {
        JFrame frame = new JFrame() {{
            final GravityAndOrbitsModule intro = new GravityAndOrbitsModule( null, new Property<Boolean>( false ), CARTOON, false, new Function1<ModeListParameterList, ArrayList<GravityAndOrbitsMode>>() {
                public ArrayList<GravityAndOrbitsMode> apply( ModeListParameterList p ) {
                    return new CartoonModeList( p.clockPaused, p.gravityEnabled, p.stepping, p.rewinding, p.timeSpeedScale );
                }
            }, 1, true );
            intro.getModes().get( 1 ).p.clockPaused.setValue( false );
            intro.showPathProperty.setValue( true );
            intro.getModes().get( 1 ).p.timeSpeedScale.setValue( 2.0 );
            intro.getModes().get( 1 ).getModel().getClock().addClockListener( new ClockAdapter() {
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    invalidate();
                    validate();
                    doLayout();
                    ArrayList<Body> bodies = intro.getModes().get( 1 ).getModel().getBodies();
                    for ( Body body : bodies ) {
                        if ( body.isCollided() || getBody( bodies, "Planet" ).getPosition().getDistance( getBody( bodies, "Moon" ).getPosition() ) > getBody( bodies, "Star" ).getRadius() * 2 ) {
                            System.out.println( alpha + "\t" + clockEvent.getSimulationTime() );
                            intro.getModes().get( 1 ).p.clockPaused.setValue( true );
                            new Thread( new Runnable() {
                                public void run() {
                                    runSim( alpha + delta, delta );
                                }
                            } ).start();
                        }
                    }
                }
            } );
            setContentPane( intro.getSimulationPanel() );
            setSize( 1024, 768 );
            setVisible( true );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }

    private static Body getBody( ArrayList<Body> bodies, String name ) {
        for ( Body body : bodies ) {
            if ( body.getName().equalsIgnoreCase( name ) ) {
                return body;
            }
        }
        return null;
    }
}
