// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty, final double alpha ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, stepping, rewinding, timeSpeedScaleProperty ),
               new SunEarth() {{
                   sun.radius *= 50;
                   earth.radius *= 1100;
                   //Make the sun move as it does in the SunEarthMoon mode
                   final int earthMassScaleFactor = 20000;
                   earth.mass *= earthMassScaleFactor;
                   forceScale *= 0.8 / earthMassScaleFactor * 0.75;//to balance increased mass

                   sun.mass = earth.mass * 96;

                   earth.vy = Math.sqrt( GravityAndOrbitsModule.G * sun.mass / earth.getPosition().minus( sun.getPosition() ).getMagnitude() );
                   earth.vx = 0;
               }}, new SunEarthMoon() {{
                    sun.radius *= 50;
                    earth.radius *= 1100;
                    moon.radius *= 800;

                    final int earthMassScaleFactor = 20000;
                    earth.mass *= earthMassScaleFactor;
//                    moon.vx *= alpha*1.5;
                    moon.y = 1.051215E10 * 1;
                    moon.mass *= earthMassScaleFactor;
                    forceScale *= 0.8 / earthMassScaleFactor * 0.75;//to balance increased mass

                    double rEM = earth.getPosition().minus( moon.getPosition() ).getMagnitude();
                    System.out.println( "distBetweenEarthAndMoon = " + rEM );

                    double rES = earth.getPosition().minus( sun.getPosition() ).getMagnitude();
                    System.out.println( "distBetweenEarthAndSun = " + rES );

//                    sun.mass = 1.0 / 12.0 * Math.pow( rES / rEM, 3 ) * earth.mass;

                    earth.mass = sun.mass / ( 1.0 / 12.0 * Math.pow( rES / rEM, 3 ) );

                    earth.vy = Math.sqrt( GravityAndOrbitsModule.G * sun.mass / earth.getPosition().minus( sun.getPosition() ).getMagnitude() );
                    earth.vx = 0;

                    earth.vx = 0;
                    earth.vy = 0;
//
                    moon.vx = -Math.sqrt( GravityAndOrbitsModule.G * earth.mass / earth.getPosition().minus( moon.getPosition() ).getMagnitude() );
                    moon.vy = earth.vy;

                    moon.mass = 1E-6;

                    sun.mass = 1;
                }}, new EarthMoon() {{
                    earth.radius *= 15;
                    moon.radius *= 15;
                }}, new EarthSpaceStation() {{
                    earth.radius *= 0.8;
                    spaceStation.radius *= 8;
                }} );
    }
}
