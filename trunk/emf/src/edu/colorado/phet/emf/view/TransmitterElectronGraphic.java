/**
 * Class: TransmitterElectronGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Dec 4, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.emf.model.Electron;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class TransmitterElectronGraphic extends ElectronGraphic {
    private ApparatusPanel apparatusPanel;
    private Graphic wiggleMeGraphic;

    public TransmitterElectronGraphic( ApparatusPanel apparatusPanel, Electron electron, Point origin ) {
        super( apparatusPanel, electron );
        init( apparatusPanel, origin );
    }

    public TransmitterElectronGraphic( ApparatusPanel apparatusPanel, Electron electron, Image image, Point origin ) {
        super( apparatusPanel, electron, image );
        init( apparatusPanel, origin );
    }

    private void init( ApparatusPanel apparatusPanel, final Point origin ) {

        Thread t = new Thread( new ArrowCacheBuilder() );
        t.start();
        this.apparatusPanel = apparatusPanel;
        wiggleMeGraphic = new Graphic() {
            Point2D.Double start = new Point2D.Double( 0, 0 );
            Point2D.Double stop = new Point2D.Double( origin.getX() - 100, origin.getY() + 50 );
            Point2D.Double current = new Point2D.Double( start.getX(), start.getY() );
            String family = "Sans Serif";
            int style = Font.BOLD;
            int size = 16;
            Font font = new Font( family, style, size );

            public void paint( Graphics2D g ) {
                current.setLocation( ( current.x + ( stop.x - current.x ) * .02 ),
                                     ( current.y + ( stop.y - current.y ) * .04 ) );
                g.setFont( font );
                g.setColor( new Color( 0, 0, 200 ));
                g.drawString( "Wiggle me >", (int)current.getX(), (int)current.getY() );

            }
        };
        apparatusPanel.addGraphic( wiggleMeGraphic, 5 );
    }

    public void mouseReleased( MouseEvent event ) {
        apparatusPanel.removeGraphic( wiggleMeGraphic );
        super.mouseReleased( event );
    }

    private class ArrowCacheBuilder implements Runnable {
        public void run() {
            Color color = FieldLatticeView.arrowRed;
            for( double length = 20; length < 100; length += FieldVector.lengthGranularity * 2 ) {
                for( double theta = 0; theta <= Math.PI * 2; theta += FieldVector.thetaGranularity * 2 ) {
                    FieldVector.getGraphic( length, theta, color );
                }
            }
        }
    }
}
