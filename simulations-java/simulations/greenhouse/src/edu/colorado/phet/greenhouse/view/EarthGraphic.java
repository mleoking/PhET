// Copyright 2002-2011, University of Colorado

/**
 * Class: EarthGraphic
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 10, 2003
 * Time: 11:09:24 PM
 */
package edu.colorado.phet.greenhouse.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.photonabsorption.model.WavelengthConstants;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.common.graphics.ApparatusPanel;
import edu.colorado.phet.greenhouse.common.graphics.Graphic;
import edu.colorado.phet.greenhouse.common.graphics.ImageGraphic;
import edu.colorado.phet.greenhouse.model.Earth;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.colorado.phet.greenhouse.model.ReflectivityAssessor;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * Quirks:
 * Adds itself to the apparatusPanel
 */
public class EarthGraphic implements Graphic, ReflectivityAssessor {

    private static final double Y_OFFSET = -1.0;
	private final ApparatusPanel apparatusPanel;
    Earth earth;
    private final Rectangle2D.Double modelBounds;
    private static int numRedsToAve = 20;
    int[] redsToAve = new int[numRedsToAve];
    private GreenhouseBackgroundImageGraphic backdropGraphic;
    private final DiskGraphic disk;
    private final Color earthBaseColor = new Color( 0, 180, 100 );
    private boolean isIceAge;
    private BufferedImage currentBackdropImage;
    private final BufferedImage backgroundToday = GreenhouseResources.getImage( "today-2.gif" );
    private final BufferedImage background1750 = GreenhouseResources.getImage( "1750-2.gif" );
    private final BufferedImage backgroundIceAge = GreenhouseResources.getImage( "ice-age-2.gif" );

    /**
     * @param apparatusPanel
     * @param earth
     * @param modelBounds
     */
    public EarthGraphic( ApparatusPanel apparatusPanel, Earth earth, final Rectangle2D.Double modelBounds) {
        this.apparatusPanel = apparatusPanel;
        this.earth = earth;
        this.modelBounds = modelBounds;
        disk = new DiskGraphic( earth, earthBaseColor );
        apparatusPanel.addGraphic( disk, GreenhouseConfig.EARTH_BASE_LAYER );

        Point2D.Double earthLoc = new Point2D.Double( earth.getLocation().getX(), earth.getLocation().getY() );
        earthLoc.setLocation( earthLoc.getX() - earth.getRadius() / 2,
                              earthLoc.getY() + earth.getRadius() / 2 );
        apparatusPanel.addGraphic( this, GreenhouseConfig.EARTH_BASE_LAYER + 1 );

        // If the apparatus panel is resized, resize the backdrop graphic
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized( ComponentEvent e ) {
                Component component = e.getComponent();
                Rectangle2D newBounds = component.getBounds();
                if (newBounds.getWidth() > 0 && newBounds.getHeight() > 0){
                    // Set and scale the proper backdrop
                    if ( currentBackdropImage != null ) {
                        setBackDropImage( currentBackdropImage, new Point2D.Double( -modelBounds.getWidth() / 2, Y_OFFSET ) );
                    }
                }
            }
        } );
    }

    public int getPixelColor( int x, int y ) {
        int result = 0;
        if ( backdropGraphic != null ) {
            result = backdropGraphic.getBufferedImage().getRGB( x, y );
            System.out.println( result );
        }
        return result;
    }

    /**
     * Controls the color of the earth based on its temperature
     */
    public void paint( Graphics2D g2 ) {

        // Compute color for the earth, based on its temperature
        int red = Math.max( 0, Math.min( (int) ( earth.getTemperature() - GreenhouseConfig.earthBaseTemperature ), 255 ) );
        int redSum = 0;
        for ( int i = numRedsToAve - 1; i > 0; i-- ) {
            redsToAve[i] = redsToAve[i - 1];
            redSum += redsToAve[i];
        }
        redsToAve[0] = red;
        redSum += red;
        red = Math.min( 2 * redSum / numRedsToAve, 255 );
        g2.setColor( Color.gray );
    }

    public void update() {
        double temperature = 0;
        if ( earth != null ) {
            temperature = earth.getTemperature();
        }
        disk.setPaint( new Color( Math.min( (int) temperature, 255 ), 180, 20 ) );
        disk.update();
    }

    public void setNoBackdrop() {
        isIceAge = false;
        setBackDrop( null, null );
        disk.setPaint( new Color( 51, 160, 44 ) );
    }

    public void setVirginEarth() {
        isIceAge = false;
        setBackDropImage( backgroundToday, new Point2D.Double( -modelBounds.getWidth() / 2, Y_OFFSET ) );
        disk.setPaint( new Color( 51, 160, 44, 0 ) );
    }

    public void setToday() {
        isIceAge = false;
        setBackDropImage( backgroundToday, new Point2D.Double( -modelBounds.getWidth() / 2, Y_OFFSET ) );
        disk.setPaint( new Color( 22, 174, 73, 0 ) );
    }

    public void set1750() {
        isIceAge = false;
        setBackDropImage( background1750, new Point2D.Double( -modelBounds.getWidth() / 2, Y_OFFSET ) );
        disk.setPaint( new Color( 25, 174, 73, 0 ) );
    }

    public void setVenus() {
        isIceAge = false;
        setBackDropImage( null, null );
        disk.setPaint( new Color( 150, 130, 80 ) );
    }

    public void setIceAge() {
        isIceAge = true;
        setBackDropImage( backgroundIceAge, new Point2D.Double( -modelBounds.getWidth() / 2, Y_OFFSET ) );
        disk.setPaint( new Color( 149, 134, 78 ) );
    }

    private void setBackDrop( BufferedImage backdropImage, Point2D.Double location ) {
        currentBackdropImage = backdropImage;
        if ( backdropGraphic != null ) {
            apparatusPanel.removeGraphic( backdropGraphic );
        }
        if ( backdropImage != null ) {
            backdropGraphic = new GreenhouseBackgroundImageGraphic( backdropImage, location,apparatusPanel );
            apparatusPanel.addGraphic( backdropGraphic, GreenhouseConfig.EARTH_BACKDROP_LAYER );
        }
    }

    private void setBackDropImage( BufferedImage bImg, Point2D.Double location ) {
        currentBackdropImage = bImg;
        if ( backdropGraphic != null ) {
            apparatusPanel.removeGraphic( backdropGraphic );
        }
        //Scale the currentBackdropImage so that it fills the width of the apparatus panel
        //phetgraphics API was confusing and difficult to get it right,
        //so we just clear the AffineTransform so that we can set it up ourselves, see #2453
        backdropGraphic = new GreenhouseBackgroundImageGraphic(currentBackdropImage, location,apparatusPanel );
        apparatusPanel.addGraphic( backdropGraphic, GreenhouseConfig.EARTH_BACKDROP_LAYER );
    }

    /**
     * This graphic allows lookup of pixel colors, which is currently used for detection of collision with ice.
     */
    static class GreenhouseBackgroundImageGraphic extends ImageGraphic {

        private final BufferedImage image;
        private final ApparatusPanel apparatusPanel;

        public GreenhouseBackgroundImageGraphic( BufferedImage image, Point2D.Double modelLocation, ApparatusPanel apparatusPanel ) {
            super( image, modelLocation );
            this.image = image;
            this.apparatusPanel = apparatusPanel;
        }

        @Override
        public void paint( Graphics2D g2 ) {
            AffineTransform savedTransform = g2.getTransform();
            g2.setTransform( new AffineTransform() );
            g2.drawRenderedImage( image, getTransform() );
            g2.setTransform( savedTransform );
        }

        private PAffineTransform getTransform() {
            final PAffineTransform newTransform = new PAffineTransform();
            double xScale = (double) apparatusPanel.getWidth() / (double) image.getWidth();
            double yScale = (double) apparatusPanel.getHeight() / (double) image.getHeight();
            newTransform.translate( 0, apparatusPanel.getHeight() - image.getHeight() * yScale );
            newTransform.scale( xScale, yScale );
            return newTransform;
        }

        /**
         * Find the image color at the specified coordinate.
         * @param x
         * @param y
         * @return
         */
        public Color getColor( double x, double y ) {
            try {
                Point2D dstPoint = getTransform().createInverse().transform( new Point2D.Double( x, y ), null );

                //This block of code helps debug the x,y, photon location.
//                Graphics2D g2 = image.createGraphics();
//                g2.setPaint( Color.green );
//                g2.fillOval( (int) dstPoint.getX(), (int) dstPoint.getY(), 2, 2 );
//                g2.dispose();

                final int x1 = (int) dstPoint.getX();
                final int y1 = (int) dstPoint.getY();
                if ( x1 >= 0 && y1 >= 0 && x1 < image.getWidth() && y1 < image.getHeight() ) {
                    int clr = image.getRGB( x1, y1 );
                    int red = ( clr & 0x00ff0000 ) >> 16;
                    int green = ( clr & 0x0000ff00 ) >> 8;
                    int blue = clr & 0x000000ff;
                    return new Color( red, green, blue );
                }
                else {
                    return Color.black;
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
                return Color.black;
            }
        }
    }

    public double getReflectivity( Photon photon ) {
        double reflectivity = 0;
        if ( isIceAge ) {
            if ( backdropGraphic != null
                 && photon.getVelocity().getY() < 0
                 && photon.getWavelength() == WavelengthConstants.SUNLIGHT_WAVELENGTH) {

                Point2D photonPosInView = apparatusPanel.modelToView( photon.getLocation() );

                Color pixel = backdropGraphic.getColor( photonPosInView.getX(), photonPosInView.getY() );
                if ( pixel.getRed() == 255 && pixel.getGreen() == 255 && pixel.getBlue() == 255 ) {
                    reflectivity = .6;
                }
            }
        }
        return reflectivity;
    }
}
