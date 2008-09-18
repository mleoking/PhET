/**
 * Class: ElectronGraphic Package: edu.colorado.phet.emf.view Author: Another
 * Guy Date: May 23, 2003
 */

package edu.colorado.phet.radiowaves.view;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common_1200.view.ApparatusPanel;
import edu.colorado.phet.common_1200.view.graphics.BufferedImageGraphic;
import edu.colorado.phet.common_1200.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_1200.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_1200.view.util.ImageLoader;
import edu.colorado.phet.radiowaves.EmfConfig;
import edu.colorado.phet.radiowaves.model.Electron;

public class ElectronGraphic extends BufferedImageGraphic implements SimpleObserver, TransformListener {

    private Point location = new Point();
    private ApparatusPanel apparatusPanel;
    private Electron electron;
    private AffineTransform atx = new AffineTransform();
    private AffineTransform containerTx;

    public ElectronGraphic( ApparatusPanel apparatusPanel, BufferedImage image, Electron electron ) {
        super( image );
        this.apparatusPanel = apparatusPanel;
        this.electron = electron;
        electron.addObserver( this );
    }

    public void transformChanged( ModelViewTransform2D mvt ) {
        containerTx = mvt.getAffineTransform();
        update();
    }

    public Point getLocation() {
        return location;
    }

    public void update() {

        //        Rectangle r1 = new Rectangle( location.x - getImage().getWidth( null ) / 2, location.y - getImage().getHeight( null ) / 2,
        //                                      getImage().getWidth( null ), getImage().getHeight( null ));

        location.x = (int) electron.getCurrentPosition().getX();
        location.y = (int) electron.getCurrentPosition().getY();

        atx.setToTranslation( location.x - getImage().getWidth( null ) / 2, location.y - getImage().getHeight( null ) / 2 );
        AffineTransform totalTx = new AffineTransform();
        if ( containerTx != null ) {
            totalTx.concatenate( containerTx );
        }
        totalTx.concatenate( atx );
        super.setTransform( totalTx );

        //        Rectangle r2 = new Rectangle( location.x - getImage().getWidth( null ) / 2, location.y - getImage().getHeight( null ) / 2,
        //                                      getImage().getWidth( null ), getImage().getHeight( null ));

        // todo: eventually use the fast repaint, rather than repainting the whole apparatus panel
        //        super.repaint();   // method 1 for fast repaint: requires making repaint() protected in super class
        //        apparatusPanel.repaint( r1 );  // method 2 for fast repaint
        //        apparatusPanel.repaint( r2 );  // method 2 for fast repaint
        apparatusPanel.repaint();
    }

    //    public void paint( Graphics2D g2 ) {
    //        Image img = getImage();
    //        g2.drawImage( img, location.x - img.getWidth( null ) / 2, location.y - img.getHeight( null ) / 2, null );
    //    }

    private Image getImage() {
        return super.getBufferedImage();
    }

    //
    // Statics fields and methods
    //
    private static final int s_radius = 10;
    private static final float s_springConst = 10;
    static String s_imageName = EmfConfig.bigElectronImg;
    static BufferedImage s_particleImage;

    static {
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

}
