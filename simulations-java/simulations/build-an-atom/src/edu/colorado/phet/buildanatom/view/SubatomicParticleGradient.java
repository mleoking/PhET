package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

//DOC
/**
 * @author Sam Reid
 */
public class SubatomicParticleGradient extends RoundGradientPaint {
    public SubatomicParticleGradient( double radius, Color baseColor ) {
        super( -radius / 1.5,
               -radius / 1.5, ColorUtils.brighterColor( baseColor, 0.8 ), new Point2D.Double( radius, radius ),
               baseColor );
    }
}
