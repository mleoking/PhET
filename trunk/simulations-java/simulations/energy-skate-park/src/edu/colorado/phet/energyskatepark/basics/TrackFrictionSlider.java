// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author John Blanco
 */
public class TrackFrictionSlider extends PNode {

    private static final double FRICTION_MAX = 0.01;

    private double savedFrictionValue = 0;

    public TrackFrictionSlider( final EnergySkateParkBasicsModule module ) {
        final Property<Double> frictionAmount = new Property<Double>( 0.0 );
        final HSliderNode sliderNode = new HSliderNode( 0, FRICTION_MAX, 90, 5, frictionAmount, module.frictionEnabled ) {
            @Override protected Paint getTrackFillPaint( Rectangle2D trackRect ) {
                return Color.WHITE;
            }

            {
                addLabel( 0, new PText( EnergySkateParkResources.getString( "none" ) ) );
                addLabel( FRICTION_MAX, new PText( EnergySkateParkResources.getString( "lots" ) ) );
            }
        };
        addChild( sliderNode );

        frictionAmount.addObserver( new VoidFunction1<Double>() {
            public void apply( Double friction ) {
                if ( module.frictionEnabled.get() ) {
                    module.setCoefficientOfFriction( friction );
                }
            }
        } );

        module.frictionEnabled.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isEnabled, Boolean wasEnabled ) {
                if ( isEnabled && !wasEnabled ) {
                    frictionAmount.set( savedFrictionValue );
                }
                else if ( wasEnabled && !isEnabled ) {
                    savedFrictionValue = frictionAmount.get();
                    frictionAmount.set( 0.0 );
                }
            }
        } );
    }
}
