/**
 * Class: AtmosphereGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 13, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.ImageGraphic;
import edu.colorado.phet.coreadditions.graphics.ShapeGraphicType;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.util.Observable;
import java.util.Observer;

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

    public AtmosphereGraphic( Atmosphere atmosphere, final Rectangle2D modelBounds, Component containingComponent ) {
        this.atmosphere = atmosphere;
        atmosphere.addObserver( this );
        BufferedImage atmosphereBI = new ImageLoader().loadBufferedImage( "images/pollution.gif" );
        atmosphereImageGraphic = new ImageGraphic( atmosphereBI, new Point2D.Double( -modelBounds.getWidth() / 2, 0 ) );
//        atmosphereImageGraphic = new ImageGraphic( atmosphereBI, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        addGraphic( atmosphereImageGraphic, 1 );
        update();

        // If the apparatus panel is resized, resize the backdrop graphic
        containingComponent.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Component component = e.getComponent();
                Rectangle2D newBounds = component.getBounds();
                if( atmosphereImageGraphic != null ) {
                BufferedImage bi = atmosphereImageGraphic.getBufferedImage();
                double scaleWidth = newBounds.getWidth() / bi.getWidth();
                double scaleHeight = newBounds.getHeight() / bi.getHeight();
                AffineTransform atx = AffineTransform.getScaleInstance( scaleWidth, scaleHeight );
                AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
                bi = atxOp.filter( bi, null );
                    removeGraphic( atmosphereImageGraphic );
                    atmosphereImageGraphic = new ImageGraphic( bi, new Point2D.Double( -modelBounds.getWidth() / 2, 0 ) );
//                    atmosphereImageGraphic = new ImageGraphic( bi, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
                    addGraphic( atmosphereImageGraphic, 1 );
                }
            }
        } );

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
