// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.view.Scale;

import static edu.colorado.phet.gravityandorbits.GAOStrings.INTRO;

/**
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Scale> scaleProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty, final double alpha ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, scaleProperty, stepping, rewinding, timeSpeedScaleProperty ),
               new SunEarth() {{
                   sun.radius *= 50;
                   earth.radius *= 1100;
               }}, new SunEarthMoon() {{
                    sun.radius *= 50;
                    earth.radius *= 1100;
                    moon.radius *= 100 * 8;

                    final int earthMassScaleFactor = 7500;
                    earth.mass *= earthMassScaleFactor;
                    moon.vx *= 4 * 3.5 * alpha;
                    moon.y = earth.radius * 2;
                    forceScale /= earthMassScaleFactor;//to balance increased mass
                }}, new EarthMoon() {{
                    earth.radius *= 15;
                    moon.radius *= 15;
                }}, new EarthSpaceStation() {{
                    earth.radius *= 0.8;
                    spaceStation.radius *= 8;
                }} );
    }

    public static void main( String[] args ) {
//        SwingUtilities.invokeLater( new Runnable() {
//            public void run() {
        runSim( 0.9530999999999998 );//1.04
//            }
//        } );
    }

    private static void runSim( final double alpha ) {
        JFrame frame = new JFrame() {{
            final GravityAndOrbitsModule intro = new GravityAndOrbitsModule( null, new Property<Boolean>( false ), INTRO, false, new Function1<ModeListParameter, ArrayList<GravityAndOrbitsMode>>() {
                public ArrayList<GravityAndOrbitsMode> apply( ModeListParameter p ) {
                    return new CartoonModeList( p.clockPausedProperty, p.gravityEnabledProperty, p.scaleProperty, p.stepping, p.rewinding, p.timeSpeedScaleProperty, alpha );
                }
            }, 1 );
            intro.getModes().get( 1 ).p.clockPausedProperty.setValue( false );
            intro.getShowPathProperty().setValue( true );
            intro.getModes().get( 1 ).p.timeSpeedScaleProperty.setValue( 2.0 );
            intro.getModes().get( 1 ).getModel().getClock().addClockListener( new ClockAdapter() {
                public void simulationTimeChanged( ClockEvent clockEvent ) {
                    invalidate();
                    validate();
                    doLayout();
                    ArrayList<Body> bodies = intro.getModes().get( 1 ).getModel().getBodies();
                    for ( Body body : bodies ) {
                        if ( body.isCollided() || getBody( bodies, "Planet" ).getPosition().getDistance( getBody( bodies, "Moon" ).getPosition() ) > getBody( bodies, "Sun" ).getRadius() * 2 ) {
                            System.out.println( alpha + "\t" + clockEvent.getSimulationTime() );
                            intro.getModes().get( 1 ).p.clockPausedProperty.setValue( true );
//                            BufferedImage bufferedImage = new BufferedImage( getContentPane().getWidth(), getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB );
//                            final Graphics2D graphics = bufferedImage.createGraphics();
//                            getContentPane().paintAll( graphics );
//                            graphics.dispose();
//                            try {
//                                ImageIO.write( bufferedImage, "PNG", new File( "C:/Users/Sam/Desktop/image_" + alpha + ".png" ) );
//                            }
//                            catch ( IOException e ) {
//                                e.printStackTrace();
//                            }
                            new Thread( new Runnable() {
                                public void run() {
                                    runSim( alpha + 0.0001 );
                                }
                            } ).start();
                        }
                    }
//                final JComponent p = intro.getSimulationPanel();
//                p.paintImmediately( 0, 0, p.getWidth(), p.getHeight() );
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
