/**
 * Class: TransmitterElectronGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Dec 4, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.BoundedGraphic;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.Config;
import edu.colorado.phet.graphics.VerticalDragHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;

public class TransmitterElectronGraphic extends DefaultInteractiveGraphic implements BoundedGraphic, Translatable {
    private static BufferedImage image;
    static {
        try {
            image = ImageLoader.loadBufferedImage( Config.bigElectronImg );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private ApparatusPanel apparatusPanel;
    private Graphic wiggleMeGraphic;
    private ElectronGraphic electronGraphic;
    private Electron electron;
    private VerticalDragHandler dragHandler;
    private Point dragPt;

    public TransmitterElectronGraphic( ApparatusPanel apparatusPanel, Electron electron, Point origin ) {
        super( null );
        this.electron = electron;
        init( apparatusPanel, origin, electron );
    }

    public void mouseDragged( MouseEvent e ) {
        dragPt = e.getPoint();
        super.mouseDragged( e );
    }

    public void translate( double dx, double dy ) {
        electron.moveToNewPosition( new Point2D.Double( dragPt.getX(), dragPt.getY() ));
    }

    public boolean contains( int x, int y ) {
        boolean b = electronGraphic.contains( x, y );
        return b;

    }

    private void init( ApparatusPanel apparatusPanel, final Point origin, Electron electron ) {

        this.apparatusPanel = apparatusPanel;
        electronGraphic = new ElectronGraphic( apparatusPanel, image, electron );
        super.setBoundary( electronGraphic);
        super.setGraphic( electronGraphic);
        electron.addObserver( electronGraphic );
        this.addCursorHandBehavior();
        this.addTranslationBehavior( this );

        // We're going to draw an animated "Wiggle me". While we're doing this, we might as well
        // use the time to build the arrow cache
        Thread t = new Thread( new ArrowCacheBuilder() );
        t.start();

        // Draw the animted "Wiggle me"
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
                g.drawString( "Wiggle the", (int)current.getX(), (int)current.getY() - g.getFontMetrics( font ).getHeight() );
                g.drawString( "electron >", (int)current.getX(), (int)current.getY() );

            }
        };
        apparatusPanel.addGraphic( wiggleMeGraphic, 5 );
    }

    public void mouseReleased( MouseEvent event ) {
        apparatusPanel.removeGraphic( wiggleMeGraphic );
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

//
//    /**
//     * Test to see how much more expensive it is to work with imutable Integers
//     * rather than ints.
//     * @param args
//     */
//    public static void main( String[] args ) {
//        int c = 0;
//        long t0 = System.currentTimeMillis();
//        for( int i = 0; i < 100000; i++ ) {
//            int j = i * i;
//            if( j % 2 == 0 ) {
//                c++;
//            }
//        }
//        long t1 = System.currentTimeMillis();
//
//        long t2 = System.currentTimeMillis();
//        for( int i = 0; i < 100000; i++ ) {
//            int j = i * i;
//            Vector2D v = new Vector2D( );
//            if( j % 2 == 0 ) {
//                c = (int)v.getX();
//            }
//        }
////        for( Integer i1 = new Integer( 0 ); !i1.equals( new Integer( 100000 )); i1 = new Integer( i1.intValue() + 1 )) {
////            Vector2D v = new Vector2D( );
////            Integer j = new Integer( i1.intValue() * i1.intValue() );
////            if( j.intValue() % 2 == 0 ) {
////                c++;
////                c = (int)v.getX();
////            }
////        }
//        long t3 = System.currentTimeMillis();
//        System.out.println( "loop 1: " + (t1 - t0 ) );
//        System.out.println( "loop 2: " + (t3 - t2 ) );
//        System.out.println( "c: " + c );
//    }
}
