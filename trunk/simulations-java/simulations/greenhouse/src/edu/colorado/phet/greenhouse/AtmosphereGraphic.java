/**
 * Class: AtmosphereGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 13, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.greenhouse.coreadditions.graphics.ImageGraphic;
import edu.colorado.phet.greenhouse.coreadditions.graphics.ShapeGraphicType;
import edu.colorado.phet.greenhouse.phetcommon.view.CompositeGraphic;

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
        BufferedImage atmosphereBI = GreenhouseResources.getImage( "pollution.gif" );
        atmosphereImageGraphic = new ImageGraphic( atmosphereBI, new Point2D.Double( -modelBounds.getWidth() / 2, 0 ) );
//        atmosphereImageGraphic = new ImageGraphic( atmosphereBI, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        addGraphic( atmosphereImageGraphic, 1 );
        update();

        // If the apparatus panel is resized, resize the backdrop graphic
        containingComponent.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Component component = e.getComponent();
                Rectangle2D newBounds = component.getBounds();
                if ( atmosphereImageGraphic != null ) {
                    BufferedImage bi = atmosphereImageGraphic.getBufferedImage();
                    if ( newBounds.getWidth() != 0 && newBounds.getHeight() != 0 && bi.getWidth() != 0 && bi.getHeight() != 0 ) {
                        double scaleWidth = newBounds.getWidth() / bi.getWidth();
                        double scaleHeight = newBounds.getHeight() / bi.getHeight();

                        AffineTransform atx = AffineTransform.getScaleInstance( scaleWidth, scaleHeight );
                        AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
                        try {
                            bi = atxOp.filter( bi, null );
                            removeGraphic( atmosphereImageGraphic );
                            atmosphereImageGraphic = new ImageGraphic( bi, new Point2D.Double( -modelBounds.getWidth() / 2, 0 ) );
//                    atmosphereImageGraphic = new ImageGraphic( bi, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
                            addGraphic( atmosphereImageGraphic, 1 );
                        }
                        catch( ImagingOpException ioe ) {
                            System.out.println( "Caught ioe=" + ioe );
                            ioe.printStackTrace();
                        }
                    }
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
        if ( visible ) {
            return (float) ( maxAlpha * atmosphere.getGreenhouseGasConcentration() / GreenhouseConfig.maxGreenhouseGasConcentration );
        }
        else {
            return 0;
        }
    }
}
