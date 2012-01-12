// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;

/**
 * NeutronNode is the visual representation of a neutron.
 * A neutron is gray, and has a specular highlight with the light source coming from below.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NeutronNode extends SphericalNode {

    private static final double DIAMETER = 8.25;
    private static final Color COLOR = new Color( 128, 128, 128 );
    private static final Color HILITE_COLOR = new Color( 175, 175, 175 );
    private static final Paint ROUND_GRADIENT = new RoundGradientPaint( 0, DIAMETER/6, HILITE_COLOR, new Point2D.Double( DIAMETER/4, DIAMETER/4 ), COLOR );
    private static final Stroke STROKE = new BasicStroke( 0.5f );
    private static final Paint STROKE_PAINT = Color.BLACK;
    
    public NeutronNode() {
        super( DIAMETER, ROUND_GRADIENT, STROKE, STROKE_PAINT, true /* convertToImage */ );
        setPickable( false );
        setChildrenPickable( false );
    }
}
