// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.model.Pool;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PoolNode extends PNode {
    private final Property<Double> liquidDensity;

    public PoolNode( final ModelViewTransform transform2D, final Pool pool, Property<Double> liquidDensity ) {
        this.liquidDensity = liquidDensity;
        final PhetPPath path = new PhetPPath( transform2D.modelToView( pool.getShape() ), createPaint( transform2D, pool ) ) {{
            waterColor.addObserver( new SimpleObserver() {
                public void update() {
                    setPaint( createPaint( transform2D, pool ) );
                }
            } );
        }};
        addChild( path );
        setPickable( false );
        setChildrenPickable( false );
        liquidDensity.addObserver( new SimpleObserver() {
            public void update() {
                path.setPaint( createPaint( transform2D, pool ) );
            }
        } );
    }

    //must be transparent so objects can submerge

    private GradientPaint createPaint( ModelViewTransform transform2D, Pool pool ) {
        Color topColor = getTopColor( liquidDensity.getValue() );
        Color bottomColor = getBottomColor( liquidDensity.getValue() );
        double yBottom = transform2D.modelToViewY( -pool.getHeight() );//fade color halfway down
        double yTop = transform2D.modelToViewY( 0 );
        return new GradientPaint( 0, (float) yTop, topColor, 0, (float) yBottom, bottomColor );
    }

    /*
     * The rest of this file uses piecewise linear functions to interpolate between gas, water and honey colors, with darker and more opaque near the bottom.
     */
    private static final double GAS_DENSITY = 719;//si
    private static final double WATER_DENSITY = 1000;
    private static final double HONEY_DENSITY = 1360;
    private static final Color honeyColor = new Color( 255, 191, 0 );
    private static final Color gasColor = Color.gray;
    public static final Property<Color> waterColor = new Property<Color>( new Color( 100, 214, 247 ) );

    {
        if ( false ) {//debug color choice
            new JFrame() {{
                final JColorChooser contentPane = new JColorChooser() {{
                    getSelectionModel().addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            waterColor.setValue( getColor() );
                        }
                    } );
                }};
                setContentPane( contentPane );
                pack();
            }}.setVisible( true );
        }
    }

    public static class PiecewiseFunction {
        private List<Piece> pieces = new ArrayList<Piece>();

        static class Piece {
            Function1<Double, Boolean> condition;
            Function1<Double, Double> function;

            Piece( Function1<Double, Boolean> condition, Function1<Double, Double> function ) {
                this.condition = condition;
                this.function = function;
            }
        }

        public PiecewiseFunction( Piece[] pieces ) {
            this.pieces = Arrays.asList( pieces );
        }

        public int evaluate( double x ) {
            for ( Piece entry : pieces ) {
                if ( entry.condition.apply( x ) ) {
                    return (int) ( entry.function.apply( x ).doubleValue() );
                }
            }
            throw new RuntimeException( "value out of domain: " + x );
        }
    }

    public static Function1<Double, Boolean> LessThan( final double x ) {
        return new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble < x;
            }
        };
    }

    public static Function1<Double, Boolean> GreaterEqual( final double x ) {
        return new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble >= x;
            }
        };
    }

    public static Function1<Double, Double> Linear( final Function.LinearFunction linearFunction ) {
        return new Function1<Double, Double>() {
            public Double apply( Double aDouble ) {
                return linearFunction.evaluate( aDouble );
            }
        };
    }

    public static PiecewiseFunction getColorComponent( Function1<Color, Integer> component ) {
        final Function.LinearFunction rLow = new Function.LinearFunction( GAS_DENSITY, WATER_DENSITY, component.apply( gasColor ), component.apply( waterColor.getValue() ) );
        final Function.LinearFunction rHigh = new Function.LinearFunction( WATER_DENSITY, HONEY_DENSITY, component.apply( waterColor.getValue() ), component.apply( honeyColor ) );

        PiecewiseFunction colorComponent = new PiecewiseFunction( new PiecewiseFunction.Piece[] {
                new PiecewiseFunction.Piece( LessThan( WATER_DENSITY ), Linear( rLow ) ),
                new PiecewiseFunction.Piece( GreaterEqual( WATER_DENSITY ), Linear( rHigh ) )
        } );
        return colorComponent;
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
        return (int) MathUtil.clamp( min, value, max );
    }

}
