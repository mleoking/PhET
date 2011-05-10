// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows an explosion for a smaller Body when it crashes into a larger Body.
 *
 * @author Sam Reid
 */
public class ExplosionNode extends PNode {
    public static final int NUM_STEPS_FOR_ANIMATION = 10;

    public ExplosionNode( final Body body, final Property<ModelViewTransform> modelViewTransform ) {
        //Function that computes the diameter as a function of the animation step
        final Function1<Integer, Double> getDiameter = new Function1<Integer, Double>() {
            public Double apply( Integer numClockTicksSinceExplosion ) {
                if ( numClockTicksSinceExplosion < NUM_STEPS_FOR_ANIMATION / 2 ) {
                    return new Function.LinearFunction( 0, NUM_STEPS_FOR_ANIMATION / 2, 1, getMaxViewDiameter( body, modelViewTransform ) ).evaluate( numClockTicksSinceExplosion );
                }
                else if ( numClockTicksSinceExplosion < NUM_STEPS_FOR_ANIMATION ) {
                    return new Function.LinearFunction( NUM_STEPS_FOR_ANIMATION / 2, NUM_STEPS_FOR_ANIMATION, getMaxViewDiameter( body, modelViewTransform ), 1 ).evaluate( numClockTicksSinceExplosion );
                }
                else {
                    return 1.0;
                }
            }
        };
        //Add the graphic that shows the explosion, uses the twinkle graphics from the cartoon sun
        addChild( getExplosionEdgeGraphic( body, getDiameter ) );

        //update the location of this node when the body changes
        body.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                setOffset( modelViewTransform.get().modelToView( body.getPosition() ).toPoint2D() );
            }
        } );
    }

    //TODO: make this a class instead of method
    private BodyRenderer.SunRenderer getExplosionEdgeGraphic( final Body body, final Function1<Integer, Double> getDiameter ) {
        final IBodyColors yellowAndWhite = new IBodyColors() {
            public Color getHighlight() {
                return Color.white;
            }

            public Color getColor() {
                return Color.yellow;
            }
        };
        final Function1<Double, Double> getDoubleRadius = new Function1<Double, Double>() {
            public Double apply( Double radius ) {
                return radius * 2;
            }
        };
        return new BodyRenderer.SunRenderer( yellowAndWhite, 1, 14, getDoubleRadius ) {{
            new RichSimpleObserver() {
                public void update() {
                    setVisible( body.getCollidedProperty().get() && body.getClockTicksSinceExplosion().get() <= NUM_STEPS_FOR_ANIMATION );
                }
            }.observe( body.getCollidedProperty(), body.getClockTicksSinceExplosion() );
            body.getClockTicksSinceExplosion().addObserver( new SimpleObserver() {
                public void update() {
                    setDiameter( getDiameter.apply( body.getClockTicksSinceExplosion().get() ) );
                }
            } );

        }};
    }

    private double getMaxViewDiameter( Body body, Property<ModelViewTransform> modelViewTransform ) {
        return modelViewTransform.get().modelToViewDeltaX( body.getDiameter() ) * 2;
    }
}
