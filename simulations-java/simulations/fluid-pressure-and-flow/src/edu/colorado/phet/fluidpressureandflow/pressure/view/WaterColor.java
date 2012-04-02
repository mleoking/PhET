// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Utilities that use piecewise linear functions to interpolate between gas, water and honey colors, with darker and more opaque near the bottom.
 *
 * @author Sam Reid
 */
public class WaterColor {

    public static final Property<Color> waterColor = new Property<Color>( new Color( 100, 214, 247 ) );

    private static final double GAS_DENSITY = 719;//si
    public static final double WATER_DENSITY = 1000;
    private static final double HONEY_DENSITY = 1360;
    private static final Color HONEY_COLOR = new Color( 255, 191, 0 );
    private static final Color GAS_COLOR = Color.gray;

    public static final boolean DEBUG_COLOR_CHOICE = false;

    static {
        if ( DEBUG_COLOR_CHOICE ) {//debug color choice
            new JFrame() {{
                final JColorChooser contentPane = new JColorChooser() {{
                    getSelectionModel().addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            waterColor.set( getColor() );
                        }
                    } );
                }};
                setContentPane( contentPane );
                pack();
            }}.setVisible( true );
        }
    }

    private static Function1<Double, Boolean> lessThan( final double x ) {
        return new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble < x;
            }
        };
    }

    private static Function1<Double, Boolean> greaterEqual( final double x ) {
        return new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble >= x;
            }
        };
    }

    private static Function1<Double, Double> linear( final Function.LinearFunction linearFunction ) {
        return new Function1<Double, Double>() {
            public Double apply( Double aDouble ) {
                return linearFunction.evaluate( aDouble );
            }
        };
    }

    private static PiecewiseFunction getColorComponent( Function1<Color, Integer> component ) {
        final Function.LinearFunction rLow = new Function.LinearFunction( GAS_DENSITY, WATER_DENSITY, component.apply( GAS_COLOR ), component.apply( waterColor.get() ) );
        final Function.LinearFunction rHigh = new Function.LinearFunction( WATER_DENSITY, HONEY_DENSITY, component.apply( waterColor.get() ), component.apply( HONEY_COLOR ) );

        return new PiecewiseFunction( new PiecewiseFunction.Piece[] {
                new PiecewiseFunction.Piece( lessThan( WATER_DENSITY ), linear( rLow ) ),
                new PiecewiseFunction.Piece( greaterEqual( WATER_DENSITY ), linear( rHigh ) )
        } );
    }

    public static Color getTopColor( double density ) {
        return newColor( getColorComponent( new Function1<Color, Integer>() {
            public Integer apply( Color color ) {
                return color.getRed();
            }
        } ).evaluate( density ) + 20, getColorComponent( new Function1<Color, Integer>() {
            public Integer apply( Color color ) {
                return color.getGreen();
            }
        } ).evaluate( density ) + 20, getColorComponent( new Function1<Color, Integer>() {
            public Integer apply( Color color ) {
                return color.getBlue();
            }
        } ).evaluate( density ) + 20, getColorComponent( new Function1<Color, Integer>() {
            public Integer apply( Color color ) {
                return 120;
            }
        } ).evaluate( density ) );
    }

    //Make the color more opaque and darker at the bottom to indicate that the pressure is greater there
    public static Color getBottomColor( double density ) {
        return newColor( getColorComponent( new Function1<Color, Integer>() {
            public Integer apply( Color color ) {
                return color.getRed();
            }
        } ).evaluate( density ) - 20, getColorComponent( new Function1<Color, Integer>() {
            public Integer apply( Color color ) {
                return color.getGreen();
            }
        } ).evaluate( density ) - 20, getColorComponent( new Function1<Color, Integer>() {
            public Integer apply( Color color ) {
                return color.getBlue();
            }
        } ).evaluate( density ) - 20, getColorComponent( new Function1<Color, Integer>() {
            public Integer apply( Color color ) {
                return 200;
            }
        } ).evaluate( density ) );
    }

    private static Color newColor( int r, int g, int b, int a ) {
        return new Color( clamp( r ), clamp( g ), clamp( b ), clamp( a ) );
    }

    private static int clamp( int r ) {
        return clamp( 0, r, 255 );
    }

    private static int clamp( int min, int value, int max ) {
        return MathUtil.clamp( min, value, max );
    }
}