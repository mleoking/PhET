// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

import edu.colorado.phet.beerslawlab.beerslaw.model.Beam;
import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Representation of light as a beam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeamNode extends PPath {

    public BeamNode( final Beam beam, final ModelViewTransform mvt ) {

        // not interactive, so that probe can be grabbed behind the beam
        setPickable( false );
        setChildrenPickable( false );

        // put a gray outline around the beam, so that lighter colors are still visible
        setStroke( new BasicStroke( 0.25f ) );
        setStrokePaint( ColorUtils.createColor( Color.LIGHT_GRAY, 210 ) );

        // Shape
        beam.shape.addObserver( new VoidFunction1<ImmutableRectangle2D>() {
            public void apply( ImmutableRectangle2D r ) {
                setPathTo( mvt.modelToView( r ).toRectangle2D() );
            }
        } );

        // Paint
        beam.paint.addObserver( new VoidFunction1<Paint>() {
            public void apply( Paint paint ) {
                setPaint( paint );
            }
        } );

        // Make this node visible when beam is visible.
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                setVisible( beam.visible.get() );
            }
        };
        visibilityObserver.observe( beam.visible );
    }
}
