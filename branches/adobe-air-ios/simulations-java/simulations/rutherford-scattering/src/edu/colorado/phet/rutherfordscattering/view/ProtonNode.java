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
 * ProtonNode is the visual representation of a proton.
 * A proton is red, and has a specular highlight with the light source coming from below.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ProtonNode extends SphericalNode {

    private static final double DIAMETER = 8.25;
    private static final Color COLOR = new Color( 255, 0, 0 ); // red
    private static final Color HILITE_COLOR = new Color( 255, 130, 130 ); // lighter red
    private static final Paint ROUND_GRADIENT = new RoundGradientPaint( 0, DIAMETER/6, HILITE_COLOR, new Point2D.Double( DIAMETER/4, DIAMETER/4 ), COLOR );
    private static final Stroke STROKE = new BasicStroke( 0.5f );
    private static final Paint STROKE_PAINT = Color.BLACK;
    
    public ProtonNode() {
        super( DIAMETER, ROUND_GRADIENT, STROKE, STROKE_PAINT, true /* convertToImage */ );
        setPickable( false );
        setChildrenPickable( false );
    }

}
