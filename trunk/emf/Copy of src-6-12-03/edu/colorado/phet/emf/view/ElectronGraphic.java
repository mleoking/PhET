/**
 * Class: ElectronGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.userinterface.graphics.InteractiveGraphic;
import edu.colorado.phet.common.userinterface.graphics.ObservingGraphic;
import edu.colorado.phet.common.userinterface.graphics.util.ResourceLoader;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.model.ElectronSpring;
import edu.colorado.phet.graphics.VerticalDragHandler;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Observable;

public class ElectronGraphic implements InteractiveGraphic, ObservingGraphic {

    private Point location = new Point();
    private Electron electron;
    private ElectronSpring spring;
    private VerticalDragHandler dragHandler;


    public ElectronGraphic( Electron electron ) {
        this.electron = electron;
    }

    public void update( Observable o, Object arg ) {
        Electron electron = (Electron)o;
        location.x = (int)electron.getCurrentPosition().getX();
        location.y = (int)electron.getCurrentPosition().getY();
    }

    public void paint( Graphics2D g2 ) {
        Image img = getImage();

        g2.drawImage( img, location.x - img.getWidth(null) / 2, location.y - img.getHeight(null) / 2, null );
//        g2.drawArc( location.x - s_radius / 2, location.y - s_radius / 2, s_radius, s_radius, 0, 360 );
//        g2.fillArc( location.x - s_radius / 2, location.y - s_radius / 2, s_radius, s_radius, 0, 360 );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        Point eventPos = event.getPoint();
        return ( eventPos.getX() >= location.x - s_radius / 2
        &&  eventPos.getX() <= location.x + s_radius / 2
        &&  eventPos.getY() >= location.y - s_radius / 2
        &&  eventPos.getY() <= location.y + s_radius / 2 );
    }

    public void mousePressed( MouseEvent event ) {
        dragHandler = new VerticalDragHandler( event.getPoint(),
                                       new Point( (int)electron.getCurrentPosition().getX(),
                                                  (int)electron.getCurrentPosition().getY() ));
//        spring = new ElectronSpring( new Point2D.Float( (float)event.getPoint().getX(),
//                                                        (float)event.getPoint().getY()),
//                                     electron,
//                                     s_springConst );
//        new AddEmfModelElementCmd( spring ).doIt();
    }

    public void mouseDragged( MouseEvent event ) {
//        spring.setOrigin( event.getPoint() );
        // TODO: do with a command
        electron.moveToNewPosition( dragHandler.getNewLocation( event.getPoint() ));
    }

    public void mouseReleased( MouseEvent event ) {
    }


    private Image getImage() {
        if( s_particleImage == null ) {
            ResourceLoader loader = new ResourceLoader();
            ResourceLoader.LoadedImageDescriptor imageDescriptor = loader.loadImage( s_imageName );
            s_particleImage = imageDescriptor.getImage();
//            this.radius = imageDescriptor.getWidth() / 2;
        }
        return s_particleImage;
    }

    //
    // Statics fields and methods
    //
    private static final int s_radius = 10;
    private static final float s_springConst = 10;
    static String s_imageName = "particle-green-sml.gif";
    static Image s_particleImage;
}
