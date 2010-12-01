package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.gravityandorbits.view.BodyRenderer;

/**
 * @author Sam Reid
 */
public class SphereBody extends Body {
    public SphereBody( String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight, Function.LinearFunction sizer, boolean modifyable ) {
        super( name, x, y, diameter, vx, vy, mass, color, highlight, sizer, modifyable, sizer );
    }

    @Override
    public BodyRenderer createRenderer( double viewDiameter ) {
        return new BodyRenderer.SphereRenderer( this, viewDiameter );
    }
}
