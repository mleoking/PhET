package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.gravityandorbits.view.BodyRenderer;

/**
 * @author Sam Reid
 */
public class SphereBody extends Body {
    public SphereBody( Body parent, String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight, Function.LinearFunction sizer, double cartoonDiameterScaleFactor, double cartoonOffsetScale ) {
        super( parent, name, x, y, diameter, vx, vy, mass, color, highlight, sizer, sizer, cartoonDiameterScaleFactor, cartoonOffsetScale );
    }

    @Override
    public BodyRenderer createRenderer( double viewDiameter ) {
        return new BodyRenderer.SphereRenderer( this, viewDiameter );
    }
}
