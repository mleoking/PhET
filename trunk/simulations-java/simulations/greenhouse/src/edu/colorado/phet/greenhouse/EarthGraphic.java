/**
 * Class: EarthGraphic
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 10, 2003
 * Time: 11:09:24 PM
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.colorado.phet.greenhouse.coreadditions.graphics.ImageGraphic;
import edu.colorado.phet.greenhouse.coreadditions.graphics.ShapeGraphicType;
import edu.colorado.phet.greenhouse.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.greenhouse.phetcommon.view.graphics.Animation;
import edu.colorado.phet.greenhouse.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.greenhouse.phetcommon.view.util.graphics.ImageLoader;

/**
 * Quirks:
 * Adds itself to the apparatusPanel
 */
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
    private BufferedImage currentBackdropImage;
    private BufferedImage backgroundToday = ImageLoader.fetchBufferedImage( "greenhouse/images/today-2.gif" );
    private BufferedImage background1750 = ImageLoader.fetchBufferedImage( "greenhouse/images/1750-2.gif" );
    private BufferedImage backgroundIceAge = ImageLoader.fetchBufferedImage( "greenhouse/images/ice-age-2.gif" );
    private Map scaledBackgroundImages = new HashMap();
    Point2D.Double pUtil = new Point2D.Double();

    /**
     * @param apparatusPanel
     * @param earth
     * @param modelBounds
     */
    public EarthGraphic( ApparatusPanel apparatusPanel, Earth earth, final Rectangle2D.Double modelBounds ) {
        this.apparatusPanel = apparatusPanel;
        this.earth = earth;
        this.modelBounds = modelBounds;
        disk = new DiskGraphic( earth, earthBaseColor );
        apparatusPanel.addGraphic( disk, GreenhouseConfig.EARTH_BASE_LAYER );

        // Set up the gif of the Earth
        earthAnimation = new Animation( "greenhouse/images/earthGifs/earth", 30 );
        earthAnimator = new EarthAnimator();
        earthAnimator.start();

        this.gif = ImageLoader.fetchBufferedImage( "greenhouse/images/earth-a.gif" );
        double gifToModelScale = ( 2 * earth.getRadius() ) / this.gif.getWidth();
        Point2D.Double earthLoc = new Point2D.Double( earth.getLocation().getX(), earth.getLocation().getY() );
        earthLoc.setLocation( earthLoc.getX() - earth.getRadius() / 2,
                              earthLoc.getY() + earth.getRadius() / 2 );
        this.earthTx = AffineTransform.getScaleInstance( gifToModelScale, -gifToModelScale );
        System.out.println( "gifToModelScale=" + gifToModelScale );
        earthTx.translate( -this.gif.getWidth() / 2, 0 );
        apparatusPanel.addGraphic( this, GreenhouseConfig.EARTH_BASE_LAYER + 1 );

        // Make two copies of all the background images. One for originals, and one for scaled
        // versions when the frame is resized.
        scaledBackgroundImages.put( backgroundToday, backgroundToday );
        scaledBackgroundImages.put( background1750, background1750 );
        scaledBackgroundImages.put( backgroundIceAge, backgroundIceAge );

        // If the apparatus panel is resized, resize the backdrop graphic
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Component component = e.getComponent();
                Rectangle2D newBounds = component.getBounds();
                // Create a scaled version of each original backdrop image
                for ( Iterator iterator = scaledBackgroundImages.keySet().iterator(); iterator.hasNext(); ) {
                    BufferedImage origImage = (BufferedImage) iterator.next();
                    double scale = newBounds.getWidth() / origImage.getWidth();
                    AffineTransform atx = AffineTransform.getScaleInstance( scale, scale );
                    AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
                    BufferedImage scaledImg = atxOp.filter( origImage, null );
                    scaledBackgroundImages.put( origImage, scaledImg );
                }

                // Set the proper backdrop
                if ( currentBackdropImage != null ) {
                    setBackDropImage( currentBackdropImage, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
                }
            }
        } );
    }

    public void stopAnimation() {
        earthAnimator.setStop();
        earthAnimation = null;
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
        if ( earthAnimation != null ) {
            try {
                g2.drawImage( earthAnimation.getCurrFrame(), earthTx, null );
            }
            catch( OutOfMemoryError outOfMemoryError ) {
                System.out.println( "Caught OutOfMemoryError: " + outOfMemoryError );
                outOfMemoryError.printStackTrace();
                System.out.println( "Continuing..." );
            }
        }
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
        setBackDropImage( backgroundToday, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 51, 160, 44, 0 ) );
    }

    public void setToday() {
        isIceAge = false;
        setBackDropImage( backgroundToday, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 25, 174, 73, 0 ) );
    }

    public void set1750() {
        isIceAge = false;
        setBackDropImage( background1750, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 25, 174, 73, 0 ) );
    }

    public void setVenus() {
        isIceAge = false;
        setBackDropImage( null, null );
        disk.setPaint( new Color( 150, 130, 80 ) );
    }


    public void setTomorrow() {
        isIceAge = false;
        setBackDropImage( null, new Point2D.Double( 0, .5 ) );
    }

//    public static ArrayList snowPoints = new ArrayList();

    public void setIceAge() {
        isIceAge = true;
        setBackDropImage( backgroundIceAge, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 149, 134, 78 ) );

//        for( int i = 0; i < backdropGraphic.getBufferedImage().getWidth(); i++ ) {
//            for( int j = 0; j < backdropGraphic.getBufferedImage().getHeight(); j++ ) {
//                int result = backdropGraphic.getBufferedImage().getRGB( i, j );
//                if( result == 0xFFFFFFFF ) {
//                    Point2D.Double p = new Point2D.Double( i, j);
        earthTx.transform( pUtil, null );
//                    snowPoints.add( earthTx.transform( p,null ));
//                }
//            }
//        }
    }

    private void setBackDrop( BufferedImage backdropImage, Point2D.Double location ) {
        currentBackdropImage = backdropImage;
        if ( backdropGraphic != null ) {
            apparatusPanel.removeGraphic( backdropGraphic );
        }
        if ( backdropImage != null ) {
            backdropGraphic = new ImageGraphic( backdropImage, location );
            apparatusPanel.addGraphic( backdropGraphic, GreenhouseConfig.EARTH_BACKDROP_LAYER );
        }
    }

    private void setBackDropImage( BufferedImage bImg, Point2D.Double location ) {
        currentBackdropImage = bImg;
        if ( backdropGraphic != null ) {
            apparatusPanel.removeGraphic( backdropGraphic );
        }
        BufferedImage scaledImage = (BufferedImage) scaledBackgroundImages.get( bImg );
        backdropGraphic = new ImageGraphic( scaledImage, location );
        apparatusPanel.addGraphic( backdropGraphic, GreenhouseConfig.EARTH_BACKDROP_LAYER );
    }

    public double getReflectivity( Photon photon ) {
        double reflectivity = 0;
        if ( isIceAge ) {
            if ( backdropGraphic != null
                 && backdropGraphic.contains( photon.getLocation() )
                 && photon.getVelocity().getY() < 0
                 && photon.getWavelength() == GreenhouseConfig.sunlightWavelength ) {

                // The 1 in the following line is a hack number that is needed to make the locations work out
                pUtil.setLocation( photon.getLocation().getX(), photon.getLocation().getY() + 1 );
                int pixel = backdropGraphic.getRGB( pUtil );
                ColorModel colorModel = backdropGraphic.getBufferedImage().getColorModel();
                int red = colorModel.getRed( pixel );
                int blue = colorModel.getBlue( pixel );
                int green = colorModel.getGreen( pixel );
                if ( red == 255 && green == 255 && blue == 255 ) {
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
            while ( !stop ) {
                try {
                    Thread.sleep( 1000 / 20 );
                    if ( earthAnimation != null ) {
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
