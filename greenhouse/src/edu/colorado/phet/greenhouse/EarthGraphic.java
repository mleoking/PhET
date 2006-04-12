/**
 * Class: EarthGraphic
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 10, 2003
 * Time: 11:09:24 PM
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.Animation;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.graphics.ImageGraphic;
import edu.colorado.phet.coreadditions.graphics.ShapeGraphicType;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.math.BigInteger;

public class EarthGraphic implements Graphic, ReflectivityAssessor, ShapeGraphicType {

    private ApparatusPanel apparatusPanel;
    Earth earth;
    private Rectangle2D.Double modelBounds;
    private static int numRedsToAve = 20;
    int[] redsToAve = new int[numRedsToAve];
    private ImageGraphic backdropGraphic;
    DiskGraphic disk;
    private Color earthBaseColor = new Color( 0, 180, 100 );
    private boolean isIceAge;
    private BufferedImage gif;
    private AffineTransform earthTx;
    private Animation earthAnimation;
    private EarthAnimator earthAnimator;

    public EarthGraphic( ApparatusPanel apparatusPanel, Earth earth, Rectangle2D.Double modelBounds ) {
        this.apparatusPanel = apparatusPanel;
        this.earth = earth;
        this.modelBounds = modelBounds;
//        earth.setReflectivityAssessor( this );
        disk = new DiskGraphic( earth, earthBaseColor );
        apparatusPanel.addGraphic( disk, GreenhouseConfig.EARTH_BASE_LAYER );

        // Set up the gif of the Earth
        earthAnimation = new Animation( "images/earthGifs/earth", 30 );
        earthAnimator = new EarthAnimator();
        earthAnimator.start();

        this.gif = ImageLoader.fetchBufferedImage( "images/earth-a.gif" );
        double gifToModelScale = ( 2 * earth.getRadius() ) / this.gif.getWidth();
        Point2D.Double earthLoc = new Point2D.Double( earth.getLocation().getX(), earth.getLocation().getY() );
        earthLoc.setLocation( earthLoc.getX() - earth.getRadius() / 2,
                              earthLoc.getY() + earth.getRadius() / 2 );
        this.earthTx = AffineTransform.getScaleInstance( gifToModelScale, -gifToModelScale );
        earthTx.translate( -this.gif.getWidth() / 2, 0 );
        apparatusPanel.addGraphic( this, GreenhouseConfig.EARTH_BASE_LAYER + 1 );
    }

    public void stopAnimation() {
        earthAnimator.setStop();
        earthAnimation = null;
    }

    public int getPixelColor( int x, int y ) {
        int result = 0;
        if( backdropGraphic != null ) {
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
        int red = Math.max( 0, Math.min( (int)( earth.getTemperature() - GreenhouseConfig.earthBaseTemperature ), 255 ) );
        int redSum = 0;
        for( int i = numRedsToAve - 1; i > 0; i-- ) {
            redsToAve[i] = redsToAve[i - 1];
            redSum += redsToAve[i];
        }
        redsToAve[0] = red;
        redSum += red;
        red = Math.min( 2 * redSum / numRedsToAve, 255 );
//        disk.setPaint( new Color( red, 255, 0 ) );

//        disk.setPaint( Color.cyan);
        if( earthAnimation != null ) {
            g2.drawImage( earthAnimation.getCurrFrame(), earthTx, null );
        }
    }

    public void update() {
        double temperature = 0;
        if( earth != null ) {
            temperature = earth.getTemperature();
        }
        disk.setPaint( new Color( Math.min( (int)temperature, 255 ), 180, 20 ) );
        disk.update();
    }

    public void setNoBackdrop() {
        isIceAge = false;
        setBackDrop( null, null );
        disk.setPaint( new Color( 51, 160, 44 ) );
    }

    public void setVirginEarth() {
        isIceAge = false;
//        setBackDrop( "images/1750-background.gif", new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        setBackDrop( "images/today-2.gif", new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 51, 160, 44, 0 ) );
    }

    public void setToday() {
        isIceAge = false;
        setBackDrop( "images/today-2.gif", new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
//        setBackDrop( "images/today.gif", new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 25, 174, 73, 0 ) );
//        disk.setPaint( Color.green );
    }

    public void set1750() {
        isIceAge = false;
        setBackDrop( "images/1750-2.gif", new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
//        setBackDrop( "images/1750-background.gif", new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 25, 174, 73, 0 ) );
//        disk.setPaint( new Color( 51, 160, 44 ) );
    }

    public void setVenus() {
        isIceAge = false;
        setBackDrop( null, null );
        disk.setPaint( new Color( 150, 130, 80 ) );
    }


    public void setTomorrow() {
        isIceAge = false;
        setBackDrop( null, new Point2D.Double( 0, .5 ) );
    }

    public void setIceAge() {
        isIceAge = true;
        setBackDrop( "images/ice-age-2.gif", new Point2D.Double( -modelBounds.getWidth() / 2 - .2, modelBounds.getY() ) );
//        setBackDrop( "images/iceage-3.gif", new Point2D.Double( -modelBounds.getWidth() / 2 - .2, modelBounds.getY() ));
        disk.setPaint( new Color( 149, 134, 78 ) );
//        disk.setPaint( new Color( 0, 180, 100 ) );
    }

    private void setBackDrop( String backdropImageFileName, Point2D.Double location ) {
        if( backdropGraphic != null ) {
            apparatusPanel.removeGraphic( backdropGraphic );
        }
        if( backdropImageFileName != null && !backdropImageFileName.equals( "" ) ) {
            backdropGraphic = new ImageGraphic( backdropImageFileName, location );
            apparatusPanel.addGraphic( backdropGraphic, GreenhouseConfig.EARTH_BACKDROP_LAYER );
        }
    }

    private void setBackDropBI( BufferedImage bImg, Point2D.Double location ) {
        if( backdropGraphic != null ) {
            apparatusPanel.removeGraphic( backdropGraphic );
        }
        backdropGraphic = new ImageGraphic( bImg, location );
        apparatusPanel.addGraphic( backdropGraphic, GreenhouseConfig.EARTH_BACKDROP_LAYER );
    }

    public double getReflectivity( Photon photon ) {
        double reflectivity = 0;
        if( isIceAge ) {
            if( backdropGraphic != null
                && backdropGraphic.contains( photon.getLocation() )
                && photon.getVelocity().getY() < 0
                && photon.getWavelength() == GreenhouseConfig.sunlightWavelength ) {
                int pixel = backdropGraphic.getRGB( photon.getLocation() );
                ColorModel colorModel = backdropGraphic.getBufferedImage().getColorModel();
                int red = colorModel.getRed( pixel );
                int blue = colorModel.getBlue( pixel );
                int green = colorModel.getGreen( pixel );
                int alpha = colorModel.getAlpha( pixel );
                if( red == 255 && green == 255 && blue == 255 ) {
                    reflectivity = .6;
                }
            }
        }
        return reflectivity;
    }

    //
    // Inner classes
    //

    /**
     * Animates the frames in the earth graphic
     */
    private class EarthAnimator extends Thread {
        private boolean stop;

        void setStop() {
            stop = true;
        }

        public void run() {
            while( stop != true ) {
                try {
                    Thread.sleep( 1000 / 20 );
                    if( earthAnimation != null ) {
                        earthAnimation.getNextFrame();
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
