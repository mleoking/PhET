/**
 * Class: AtmosphereGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 13, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.coreadditions.Annulus;
import edu.colorado.phet.coreadditions.graphics.ShapeGraphicType;
import edu.colorado.phet.coreadditions.graphics.ImageGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observer;
import java.util.Observable;

/**
 * An overlay graphic that is supposed to look like polluted air. It fades in and out depending on the
 * amount of junk in the atmosphere.
 */
public class AtmosphereGraphic extends CompositeGraphic implements Observer, ShapeGraphicType {

    private Atmosphere atmosphere;
    private float greenhouseAlpha;
    private double maxAlpha = 0.4;

    private ImageGraphic atmosphereImageGraphic;
    private boolean visible;

    public AtmosphereGraphic( Atmosphere atmosphere, Rectangle2D modelBounds ) {
        this.atmosphere = atmosphere;
        atmosphere.addObserver( this );
        BufferedImage atmosphereBI = new ImageLoader().loadBufferedImage( "images/pollution.gif" );
        atmosphereImageGraphic = new ImageGraphic( atmosphereBI, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        addGraphic( atmosphereImageGraphic, 1 );
        update();
    }

    public void setVisible( boolean b ) {
        visible = b;
    }

    public void paint( Graphics2D g2 ) {
        Composite orgComposite = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, greenhouseAlpha ) );
        super.paint( g2 );
        g2.setComposite( orgComposite );
    }

    public void update( Observable o, Object arg ) {
        this.update();
    }

    private void update() {
        greenhouseAlpha = computeGreenhouseAlpha();
    }

    private float computeGreenhouseAlpha() {
        if( visible ) {
            return (float)( maxAlpha * atmosphere.getGreenhouseGasConcentration() / GreenhouseConfig.maxGreenhouseGasConcentration );
        }
        else {
            return 0;
        }
    }
}
