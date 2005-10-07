/**
 * Class: AtmosphereGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 13, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.Annulus;
import edu.colorado.phet.coreadditions.graphics.ShapeGraphicType;
import edu.colorado.phet.coreadditions.graphics.ImageGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.Observer;
import java.util.Observable;

public class AtmosphereGraphic implements Graphic, Observer, ShapeGraphicType {

    private Atmosphere atmosphere;
    private Area troposphereGraphic;
    private Color baseAtmosphereColor = new Color( 0, 0, 156 );
    private GradientPaint atmospherePaint;
    private Annulus troposphere;
//    private ImageGraphicType greenhouseTexture;
//    private BufferedImage gtBI;
    private float greenhouseAlpha;

    public AtmosphereGraphic( Atmosphere atmosphere ) {
        this.atmosphere = atmosphere;
        atmosphere.addObserver( this );
        troposphere = atmosphere.getTroposphere();

        Ellipse2D.Double outerDisk = new Ellipse2D.Double();
        outerDisk.setFrameFromCenter( troposphere.getCenter().getX(),
                                      troposphere.getCenter().getY(),
                                      troposphere.getCenter().getX() + troposphere.getOuterDiameter() / 2,
                                      troposphere.getCenter().getY() + troposphere.getOuterDiameter() / 2 );
        Ellipse2D.Double innerDisk = new Ellipse2D.Double();
        innerDisk.setFrameFromCenter( troposphere.getCenter().getX(),
                                      troposphere.getCenter().getY(),
                                      troposphere.getCenter().getX() + troposphere.getInnerDiameter() / 2,
                                      troposphere.getCenter().getY() + troposphere.getInnerDiameter() / 2 );
        troposphereGraphic = new Area( outerDisk );
        troposphereGraphic.subtract( new Area( innerDisk ) );

//        gtBI = ImageLoader.fetchBufferedImage( "images/greenhouse-texture.gif" );
//        greenhouseTexture = new ImageGraphicType( gtBI, new Point2D.Double( 0, 0 ));
        update();
    }

    public void paint( Graphics2D g2 ) {
        RenderingHints orgHints = g2.getRenderingHints();
        Composite orgComposite = g2.getComposite();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        g2.setPaint( atmospherePaint );
        g2.fill( troposphereGraphic );

        // Add the greenhouse gas texture
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, greenhouseAlpha));
//        greenhouseTexture.paint( g2 );
        g2.setRenderingHints( orgHints );
        g2.setComposite( orgComposite );
    }

    public void update( Observable o, Object arg ) {
        this.update();
    }

    private void update() {
        greenhouseAlpha = computeGreenhouseAlpha();
        Color startColor = setAtmosphereColor();
        Color endColor = Color.black;
        atmospherePaint = new GradientPaint( 0,
                                             0,
                                             startColor,
                                             0,
                                             (float)( ( troposphere.getOuterDiameter() - troposphere.getInnerDiameter() ) / 2 ),
                                             endColor );
    }

    private Color setAtmosphereColor() {
        int red = baseAtmosphereColor.getRed();
        int green = baseAtmosphereColor.getGreen();
        int blue = baseAtmosphereColor.getBlue();
        double percentMaxGreenhouseGasConcentration = atmosphere.getGreenhouseGasConcentration()
                / GreenhouseConfig.maxGreenhouseGasConcentration;
        red = (int)Math.min( 255, red + 255 * percentMaxGreenhouseGasConcentration );
        green = (int)Math.min( 255, green + 255 * percentMaxGreenhouseGasConcentration );
        Color atmosphereColor = new Color( red, green, blue );
        return atmosphereColor;
    }

    private float computeGreenhouseAlpha() {
        return (float)( atmosphere.getGreenhouseGasConcentration() / GreenhouseConfig.maxGreenhouseGasConcentration );
    }
}
