/**
 * Class: ElectronGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.fastpaint.FastPaintImageGraphic;
//import edu.colorado.phet.common.view.graphics.SimpleBufferedImageGraphic;
import edu.colorado.phet.emf.Config;
import edu.colorado.phet.emf.model.Electron;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ElectronGraphic extends FastPaintImageGraphic
//public class ElectronGraphic extends SimpleBufferedImageGraphic
        implements SimpleObserver {

    private Point location = new Point();
    private ApparatusPanel apparatusPanel;
    private Electron electron;

    public ElectronGraphic( ApparatusPanel apparatusPanel, BufferedImage image, Electron electron ) {
        super( image, apparatusPanel );
        this.apparatusPanel = apparatusPanel;
        this.electron = electron;
        electron.addObserver( this );
    }

    public Point getLocation() {
        return location;
    }

    public boolean contains( int x, int y ) {
        boolean result = super.contains( x - location.x + getImage().getWidth( null ) / 2, y - location.y + getImage().getHeight( null ) / 2 );
        return result;
    }

    public void update() {
//        super.setLocation( (int)electron.getCurrentPosition().getX(), (int)electron.getCurrentPosition().getY() );
        location.x = (int)electron.getCurrentPosition().getX();
        location.y = (int)electron.getCurrentPosition().getY();
        apparatusPanel.repaint();
    }

    public void paint( Graphics2D g2 ) {
        Image img = getImage();
        g2.drawImage( img, location.x - img.getWidth( null ) / 2, location.y - img.getHeight( null ) / 2, null );
    }

    private Image getImage() {
        return super.getBufferedImage();
    }

    //
    // Statics fields and methods
    //
    private static final int s_radius = 10;
    private static final float s_springConst = 10;
    static String s_imageName = Config.bigElectronImg;
    static BufferedImage s_particleImage;

    static {
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

}
