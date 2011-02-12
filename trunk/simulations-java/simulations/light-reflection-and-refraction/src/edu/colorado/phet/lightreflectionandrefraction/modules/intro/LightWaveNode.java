// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class LightWaveNode extends PNode {

    public LightWaveNode( final ModelViewTransform transform, final LightRay lightRay ) {
        addChild( new PhetPPath( createPaint( transform, lightRay ) ) {{
            lightRay.addObserver( new SimpleObserver() {
                public void update() {
                    final Shape shape = transform.modelToView( lightRay.getWaveShape() );
                    setPathTo( shape );
                }
            } );
            lightRay.phase.addObserver( new SimpleObserver() {
                public void update() {
                    setPaint( createPaint( transform, lightRay ) );
                }
            } );
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    private GradientPaint createPaint( ModelViewTransform transform, LightRay lightRay ) {
        double powerFraction = lightRay.getPowerFraction();
        double viewWavelength = transform.modelToViewDeltaX( lightRay.getWavelength() );
        final ImmutableVector2D vec = transform.modelToViewDelta( lightRay.toVector2D() ).getNormalizedInstance().getScaledInstance( viewWavelength );
        final Color red = new Color( 1f, 0, 0, (float) Math.sqrt( powerFraction ) );
        final Color black = new Color( 0, 0, 0, (float) Math.sqrt( powerFraction ) );

        ImmutableVector2D phaseOffset = vec.getNormalizedInstance().getScaledInstance( lightRay.phase.getValue() );
        float dx = (float) phaseOffset.getX();
        float dy = (float) phaseOffset.getY();
        return new GradientPaint( dx, dy, red, dx + (float) vec.getX(), dy + (float) vec.getY(), black, true );
    }
}