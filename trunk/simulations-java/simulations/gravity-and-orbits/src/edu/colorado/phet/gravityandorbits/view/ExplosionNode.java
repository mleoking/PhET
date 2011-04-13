// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
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
    int numStepsForAnimation = 10;//REVIEW constant should be static final and all uppercase

    public ExplosionNode( final Body body, final Property<ModelViewTransform> modelViewTransform ) {
        //Function that computes the diameter as a function of the animation step
        //REVIEW name this diameterFunction
        final Function1<Integer, Double> diameter = new Function1<Integer, Double>() {
            public Double apply( Integer numClockTicksSinceExplosion ) {
                if ( numClockTicksSinceExplosion < numStepsForAnimation / 2 ) {
                    return new Function.LinearFunction( 0, numStepsForAnimation / 2, 1, getMaxViewDiameter( body, modelViewTransform ) ).evaluate( numClockTicksSinceExplosion );
                }
                else if ( numClockTicksSinceExplosion < numStepsForAnimation ) {
                    return new Function.LinearFunction( numStepsForAnimation / 2, numStepsForAnimation, getMaxViewDiameter( body, modelViewTransform ), 1 ).evaluate( numClockTicksSinceExplosion );
                }
                else {
                    return 1.0;
                }
            }
        };
        //REVIEW incredibly difficult to read or document, why not encapsulate in a subclass of SunRenderer?
        //Add the graphic that shows the explosion, uses the twinkle graphics from the cartoon sun
        addChild( new BodyRenderer.SunRenderer( new IBodyColors() {
            public Color getHighlight() {
                return Color.white;
            }

            public Color getColor() {
                return Color.yellow;
            }
        }, 1, 14, new Function1<Double, Double>() {
            public Double apply( Double radius ) {
                return radius * 2;
            }
        } ) {{
            final SimpleObserver visible = new SimpleObserver() {
                public void update() {
                    setVisible( body.getCollidedProperty().getValue() && body.getClockTicksSinceExplosion().getValue() <= numStepsForAnimation );
                }
            };
            body.getCollidedProperty().addObserver( visible );
            body.getClockTicksSinceExplosion().addObserver( visible );
            body.getClockTicksSinceExplosion().addObserver( new SimpleObserver() {
                public void update() {
                    setDiameter( diameter.apply( body.getClockTicksSinceExplosion().getValue() ) );
                }
            } );

        }} );
        body.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                setOffset( getPosition( modelViewTransform, body ).toPoint2D() );
            }
        } );
    }

    private double getMaxViewDiameter( Body body, Property<ModelViewTransform> modelViewTransform ) {
        return modelViewTransform.getValue().modelToViewDeltaX( body.getDiameter() ) * 2;
    }

    //REVIEW one usage, consider inlining
    private ImmutableVector2D getPosition( Property<ModelViewTransform> modelViewTransform, Body body ) {
        return modelViewTransform.getValue().modelToView( body.getPosition() );
    }
}
