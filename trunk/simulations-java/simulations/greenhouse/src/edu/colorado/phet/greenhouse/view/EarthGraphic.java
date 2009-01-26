/**
 * Class: EarthGraphic
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 10, 2003
 * Time: 11:09:24 PM
 */
package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.common.graphics.ApparatusPanel;
import edu.colorado.phet.greenhouse.common.graphics.Graphic;
import edu.colorado.phet.greenhouse.common.graphics.ImageGraphic;
import edu.colorado.phet.greenhouse.model.Earth;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.colorado.phet.greenhouse.model.ReflectivityAssessor;

/**
 * Quirks:
 * Adds itself to the apparatusPanel
 */
public class EarthGraphic implements Graphic, ReflectivityAssessor {

    private ApparatusPanel apparatusPanel;
    Earth earth;
    private Rectangle2D.Double modelBounds;
    private static int numRedsToAve = 20;
    int[] redsToAve = new int[numRedsToAve];
    private ImageGraphic backdropGraphic;
    private DiskGraphic disk;
    private Color earthBaseColor = new Color( 0, 180, 100 );
    private boolean isIceAge;
    private BufferedImage gif;
    private AffineTransform earthTx;
    private BufferedImage currentBackdropImage;
    private BufferedImage backgroundToday = GreenhouseResources.getImage( "today-2.gif" );
    private BufferedImage background1750 = GreenhouseResources.getImage( "1750-2.gif" );
    private BufferedImage backgroundIceAge = GreenhouseResources.getImage( "ice-age-2.gif" );
    Point2D.Double pUtil = new Point2D.Double();
    private double desiredImageWidth = 100;  // Somewhat arbitrary initial value, will be recalculated during init.

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

        this.gif = GreenhouseResources.getImage( "earth-a.gif" );
        double gifToModelScale = ( 2 * earth.getRadius() ) / this.gif.getWidth();
        Point2D.Double earthLoc = new Point2D.Double( earth.getLocation().getX(), earth.getLocation().getY() );
        earthLoc.setLocation( earthLoc.getX() - earth.getRadius() / 2,
                              earthLoc.getY() + earth.getRadius() / 2 );
        this.earthTx = AffineTransform.getScaleInstance( gifToModelScale, -gifToModelScale );
//        System.out.println( "gifToModelScale=" + gifToModelScale );
        earthTx.translate( -this.gif.getWidth() / 2, 0 );
        apparatusPanel.addGraphic( this, GreenhouseConfig.EARTH_BASE_LAYER + 1 );

        // If the apparatus panel is resized, resize the backdrop graphic
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Component component = e.getComponent();
                Rectangle2D newBounds = component.getBounds();
                if (newBounds.getWidth() > 0 && newBounds.getHeight() > 0){
                	desiredImageWidth = newBounds.getWidth();
                	
                    // Set and scale the proper backdrop
                    if ( currentBackdropImage != null ) {
                        setBackDropImage( currentBackdropImage, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
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
        setBackDropImage( backgroundToday, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 51, 160, 44, 0 ) );
    }

    public void setToday() {
        isIceAge = false;
        setBackDropImage( backgroundToday, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
        disk.setPaint( new Color( 22, 174, 73, 0 ) );
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
        backdropGraphic = new ImageGraphic( 
        		BufferedImageUtils.rescaleXMaintainAspectRatio(currentBackdropImage, (int)desiredImageWidth), 
        		location );
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

}
