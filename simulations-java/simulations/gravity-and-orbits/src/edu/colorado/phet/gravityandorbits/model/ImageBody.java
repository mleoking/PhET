package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.gravityandorbits.view.BodyRenderer;

/**
 * @author Sam Reid
 */
public class ImageBody extends Body {
    public ImageBody( String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight, Function.LinearFunction sizer, double cartoonDiameterScaleFactor ) {
        this( name, x, y, diameter, vx, vy, mass, color, highlight, sizer, sizer, cartoonDiameterScaleFactor );
    }

    public ImageBody( String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight, Function.LinearFunction sizer, Function.LinearFunction iconSizer, double cartoonDiameterScaleFactor ) {
        super( name, x, y, diameter, vx, vy, mass, color, highlight, sizer, iconSizer, cartoonDiameterScaleFactor );
    }

    @Override
    public BodyRenderer createRenderer( double viewDiameter ) {
        return new BodyRenderer.ImageRenderer( this, viewDiameter );
    }
}
