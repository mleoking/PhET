// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.beerslaw.model.Cuvette;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light.LightRepresentation;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * The beam view of the light.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeamNode extends PhetPNode {

    public BeamNode( final Light light, Cuvette cuvette, ModelViewTransform mvt ) {
        addChild( new LeftSegmentNode( light, cuvette, mvt ) );
        addChild( new CenterSegmentNode( light, cuvette, mvt ) );

        // Make this node visible when the light is on and type is "beam".
        final RichSimpleObserver observer = new RichSimpleObserver() {
            public void update() {
                setVisible( light.on.get() & light.representation.get() == LightRepresentation.BEAM );
            }
        };
        observer.observe( light.on, light.representation );
    }

    // Base class for all segments of the beam.
    private static abstract class SegmentNode extends PPath {
        public SegmentNode( final Light light ) {
            setStroke( new BasicStroke( 0.25f ) );
        }

        public void setBeamColor( Color color ) {
            setPaint( color );
            setStrokePaint( color.darker().darker() );
        }
    }

    // The left segment, between the light and the left edge of the cuvette.
    private static class LeftSegmentNode extends SegmentNode {

        public LeftSegmentNode( final Light light, Cuvette cuvette, ModelViewTransform mvt ) {
            super( light );

            double x = mvt.modelToViewDeltaX( light.location.getX() );
            double y = mvt.modelToViewDeltaY( light.location.getY() - ( light.lensDiameter / 2 ) );
            double w = mvt.modelToViewDeltaX( cuvette.location.getX() - light.location.getX() );
            double h = mvt.modelToViewDeltaY( light.lensDiameter );
            setPathTo( new Rectangle2D.Double( x, y, w, h ) );

            // Set the color to match the light's wavelength
            light.wavelength.addObserver( new VoidFunction1<Double>() {
                public void apply( Double wavelength ) {
                    setBeamColor( new VisibleColor( wavelength ) );
                }
            } );
        }
    }

    // The center segment, the portion that passes through the solution in the cuvette.
    private static class CenterSegmentNode extends SegmentNode {

        public CenterSegmentNode( final Light light, final Cuvette cuvette, final ModelViewTransform mvt ) {
            super( light );

            // resize the beam when the path length (cuvette width) changes
            cuvette.width.addObserver( new VoidFunction1<Double>() {
                public void apply( Double width ) {
                    double x = mvt.modelToViewDeltaX( cuvette.location.getX() );
                    double y = mvt.modelToViewDeltaY( light.location.getY() - ( light.lensDiameter / 2 ) );
                    double w = mvt.modelToViewDeltaX( cuvette.width.get() );
                    double h = mvt.modelToViewDeltaY( light.lensDiameter );
                    setPathTo( new Rectangle2D.Double( x, y, w, h ) );
                }
            } );

            // Set the color to match the light's wavelength
            light.wavelength.addObserver( new VoidFunction1<Double>() {
                public void apply( Double wavelength ) {
                    setBeamColor( ColorUtils.createColor( new VisibleColor( wavelength ), 100 ) );
                }
            } );
        }
    }
}
