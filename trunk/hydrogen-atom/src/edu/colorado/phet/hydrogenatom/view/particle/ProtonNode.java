/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.particle;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.util.RoundGradientPaint;
import edu.colorado.phet.hydrogenatom.view.SphericalNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * 
 * ProtonNode draws a proton.
 * A proton is red, and has a specular highlight with the light source coming from below.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ProtonNode extends SphericalNode {

    private static final double DIAMETER = 11;
    private static final Color COLOR = new Color( 255, 0, 0 ); // red
    private static final Color HILITE_COLOR = new Color( 255, 130, 130 ); // lighter red
    private static final Paint ROUND_GRADIENT = new RoundGradientPaint( 0, DIAMETER/6, HILITE_COLOR, new Point2D.Double( DIAMETER/4, DIAMETER/4 ), COLOR );
    private static final Stroke STROKE = new BasicStroke( 0.5f );
    private static final Paint STROKE_PAINT = Color.BLACK;
    
    public ProtonNode() {
        super( DIAMETER, ROUND_GRADIENT, STROKE, STROKE_PAINT, true /* convertToImage */ );
    }

}
