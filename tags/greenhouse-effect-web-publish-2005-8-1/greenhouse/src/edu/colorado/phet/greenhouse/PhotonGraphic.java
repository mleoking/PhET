/**
 * Class: PhotonGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.Disk;
import edu.colorado.phet.coreadditions.graphics.ShapeGraphicType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.util.Observer;
import java.util.Observable;
import java.util.HashMap;

public class PhotonGraphic implements Graphic, Observer, ShapeGraphicType {

    static int cnt = 0;
    public static final int numTailPts = 4;

    private Photon photon;

    private Point2D.Double[] tailPts = new Point2D.Double[numTailPts];
    private Line2D.Double tailSeg = new Line2D.Double();
    private Stroke photonStroke = new BasicStroke( 0.1f );
    private Color color;
    Point2D.Double p1Util = new Point2D.Double();
    Point2D.Double p2Util = new Point2D.Double();

    private boolean isVisible;

    public PhotonGraphic( Photon photon ) {

        cnt++;

        this.photon = photon;
        color = genColor( photon.getWavelength() );
        photon.addObserver( this );
        isVisible = true;

        for( int i = 0; i < numTailPts; i++ ) {
            tailPts[i] = new Point2D.Double( Double.NaN, Double.NaN );
        }

        this.update();
    }

    protected void finalize() throws Throwable {
        super.finalize();
        cnt--;
    }

    public void paint( Graphics2D g2 ) {
//        graphic.paint( g2 );

        GraphicsUtil.setAntiAliasingOn( g2 );
        g2.setColor( color );
        g2.setStroke( photonStroke );
        for( int i = 0; i < numTailPts - 1 && !Double.isNaN( tailPts[i + 1].getX() ); i++ ) {
            tailSeg.setLine( tailPts[i], tailPts[i+1]);
            g2.draw( tailSeg );
        }

        // draw the arrowhead
        double length = 0.1;
        double theta = Math.atan2( tailPts[1].getY() - tailPts[0].getY(),
                                   tailPts[1].getX() - tailPts[0].getX() );
        double alpha = theta + Math.PI / 4;
        double beta = theta - Math.PI / 4;
        double x1 = tailPts[0].getX() + length * Math.cos( alpha );
        double y1 = tailPts[0].getY() + length * Math.sin( alpha );
        double x2 = tailPts[0].getX() + length * Math.cos( beta );
        double y2 = tailPts[0].getY() + length * Math.sin( beta );
        p1Util.setLocation( x1, y1 );
        p2Util.setLocation( x2, y2 );
        tailSeg.setLine( tailPts[0], p1Util );
//        tailSeg.setLine( tailPts[0], new Point2D.Double( x1, y1 ));
        g2.draw( tailSeg );
        tailSeg.setLine( tailPts[0], p2Util );
//        tailSeg.setLine( tailPts[0], new Point2D.Double( x2, y2 ));
        g2.draw( tailSeg );
    }

    public void update() {

        for( int i = numTailPts - 1; i > 0; i-- ) {
            tailPts[i].setLocation( tailPts[i - 1] );
        }
        tailPts[0].setLocation( photon.getLocation().getX(), photon.getLocation().getY() );
    }

    public void update( Observable o, Object arg ) {
        this.update();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible( boolean visible ) {
        isVisible = visible;
    }

    public void setPhotonStroke( Stroke photonStroke ) {
        this.photonStroke = photonStroke;
    }

    //
    // Statics
    //
    private static HashMap colorLUT = new HashMap();


    static {
        colorLUT.put( new Double( GreenhouseConfig.sunlightWavelength ), new Color( 255, 255, 120 ) );
        colorLUT.put( new Double( GreenhouseConfig.irWavelength ), Color.red );
        colorLUT.put( new Double( GreenhouseConfig.debug_wavelength), Color.green );
    }

    private Color genColor( double wavelength ) {
//        double d = 255 * Math.min( ( Math.abs( 1000 - wavelength ) / 1000), 1);
//        double green = 255 - d;
//        d = 255 * Math.min( ( Math.abs( 1500 - wavelength ) / 1500), 1);
//        double red = 255 - d;
//        d = 255 * Math.min( ( Math.abs( 400 - wavelength ) / 400), 1);
//        double blue = 255 - d;
//        return new Color( (int)red, (int)green, (int)blue );
        return (Color)colorLUT.get( new Double( wavelength ) );
    }

}
