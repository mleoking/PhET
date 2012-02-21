// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.beerslaw.model.Cuvette;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * The left section of the beam, between the light and the cuvette.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeamLeftNode extends PPath {

    public BeamLeftNode( Light light, Cuvette cuvette, ModelViewTransform mvt ) {
        setStroke( new BasicStroke( 0.25f ) );

        double x = mvt.modelToViewDeltaX( light.location.getX() );
        double y = mvt.modelToViewDeltaY( light.location.getY() - ( light.lensDiameter / 2 ) );
        double w = mvt.modelToViewDeltaX( cuvette.location.getX() - light.location.getX() );
        double h = mvt.modelToViewDeltaY( light.lensDiameter );
        setPathTo( new Rectangle2D.Double( x, y, w, h ) );

        // Set the color to match the light's wavelength
        light.wavelength.addObserver( new VoidFunction1<Double>() {
            public void apply( Double wavelength ) {
                Color color = new VisibleColor( wavelength );
                setPaint( color );
                setStrokePaint( color.darker().darker() );
            }
        } );

        // Make this node visible when the light is on.
        light.on.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean on ) {
                setVisible( on );
            }
        });
    }
}
