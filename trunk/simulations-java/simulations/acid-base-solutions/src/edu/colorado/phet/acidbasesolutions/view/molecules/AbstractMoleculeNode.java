/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all molecule nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ abstract class AbstractMoleculeNode extends PComposite {
    
    protected static Paint createPaint( double diameter, Color color, Color hiliteColor ) {
        return new RoundGradientPaint( -diameter/6, -diameter/6, hiliteColor, new Point2D.Double( diameter/4, diameter/4 ), color );
    }
}
