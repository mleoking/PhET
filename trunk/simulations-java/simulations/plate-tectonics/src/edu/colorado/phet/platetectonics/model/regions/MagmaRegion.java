// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TextureStrategy;

/**
 * Creates a teardrop-shaped magma region with a specific angle and scale
 */
public class MagmaRegion extends Region {

    public final Property<Float> alpha = new Property<Float>( 1f );
    public final Property<ImmutableVector2F> position;

    // angle in radians
    public MagmaRegion( final TextureStrategy textureStrategy, final float scale, final float angle, final int numSamples, final ImmutableVector2F initialPosition ) {
        super( 1, numSamples, new Function2<Integer, Integer, Sample>() {
            private ImmutableVector2F textureOffset = new ImmutableVector2F( Math.random(), Math.random() );

            public Sample apply( Integer yIndex, Integer xIndex ) {
                float ratio = ( (float) xIndex ) / ( (float) ( numSamples - 1 ) );
                float theta = (float) ( yIndex == 0
                                        ? ( 1 - ratio ) * ( Math.PI ) // top side, ratio of 0=>1 to pi=>0
                                        : ( 1 - ratio ) * ( -Math.PI ) ); // bottom side, ratio of 0=>1 to -pi=>0
                ImmutableVector2F position = computeTeardropShape( theta ).times( scale ).getRotatedInstance( angle );
                return new Sample( new ImmutableVector3F( position.x, position.y, 0 ),
                                   PlateMotionModel.SIMPLE_MAGMA_TEMP,
                                   PlateMotionModel.SIMPLE_MAGMA_DENSITY,

                                   // add a texture offset that is random for each region, but consistent
                                   textureStrategy.mapFront( position ).plus( textureOffset ) );
            }
        } );
        position = new Property<ImmutableVector2F>( initialPosition );
        for ( Sample sample : getSamples() ) {
            sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( initialPosition.x, initialPosition.y, 0 ) ) );
        }
        position.addObserver( new ChangeObserver<ImmutableVector2F>() {
            public void update( ImmutableVector2F newValue, ImmutableVector2F oldValue ) {
                ImmutableVector3F delta = new ImmutableVector3F( newValue.x - oldValue.x, newValue.y - oldValue.y, 0 );
                for ( Sample sample : getSamples() ) {
                    sample.setPosition( sample.getPosition().plus( delta ) );
                }
            }
        } );
        alpha.addObserver( new SimpleObserver() {
            public void update() {
                setAllAlphas( alpha.get() );
            }
        } );
    }

    // came up with this approximate shape in Mathematica: ParametricPlot[{Cos[t] + Max[0, Cos[t]], Sin[t]*(1 - Max[0, Cos[t]]^2)}, {t, 0, 2*\[Pi]}]
    // it's basically the equation for a circle, but stretched in a certain way
    private static ImmutableVector2F computeTeardropShape( float theta ) {
        // in the future, just scale this amout if you want the tip pointier or less pointy
        final double tipScale = 1;

        final double tipAmount = tipScale * Math.max( 0, Math.cos( theta ) );

        final double tipXPosition = 1 + tipScale; // 1 is from the radius of the circle

        return new ImmutableVector2F( Math.cos( theta ) + tipAmount,
                                      Math.sin( theta ) * ( 1 - tipAmount * tipAmount ) ).minus( new ImmutableVector2F( tipXPosition, 0 ) );
    }
}
