package edu.colorado.phet.bernoulli.spline.segments;

import edu.colorado.phet.bernoulli.common.CircleGraphic;
import edu.colorado.phet.bernoulli.common.FakeClock;
import edu.colorado.phet.bernoulli.common.SegmentGraphic;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 24, 2003
 * Time: 4:36:59 AM
 * Copyright (c) Aug 24, 2003 by Sam Reid
 */
public class SegmentCollisionTest {
    static class TestModule extends Module {
        private Segment a;
        private Segment b;
        CircleGraphic cg;
        private double height = 1;
        private SegmentGraphic bg;

        //
        public TestModule( String name ) {
            super( name );

            final ModelViewTransform2d transform = new ModelViewTransform2d( new Rectangle2D.Double( 0, 0, 10, 10 ), new Rectangle( 0, 0, 1, 1 ) );
            a = new Segment( 0, 0, 10, 6 );
            b = new Segment( 2, height, 4, height );

            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );
            Stroke stroke = new BasicStroke( 1.0f );
            SegmentGraphic ag = new SegmentGraphic( transform, a.getStartPoint().getX(), a.getStartPoint().getY(), a.getFinishPoint().getX(), a.getFinishPoint().getY(), Color.black, stroke );
            bg = new SegmentGraphic( transform, b.getStartPoint().getX(), b.getStartPoint().getY(), b.getFinishPoint().getX(), b.getFinishPoint().getY(), Color.black, stroke );
            getApparatusPanel().addGraphic( new Graphic() {
                public void paint( Graphics2D g ) {
                    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                }
            }, -1 );
            getApparatusPanel().addGraphic( ag, 0 );
            getApparatusPanel().addGraphic( bg, 0 );
            getApparatusPanel().addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    if( transform != null && getApparatusPanel() != null && getApparatusPanel().getBounds() != null ) {
                        transform.setViewBounds( getApparatusPanel().getBounds() );
                    }
                }

                public void componentShown( ComponentEvent e ) {
                    if( transform != null && getApparatusPanel() != null && getApparatusPanel().getBounds() != null ) {
                        transform.setViewBounds( getApparatusPanel().getBounds() );
                    }
                }
            } );
//            Point2D.Double intersectionPoint = b.getTopIntersection(a);
            cg = new CircleGraphic( new Point2D.Double( 5, 5 ), .2, Color.red, transform );
            getApparatusPanel().addGraphic( cg, 1 );
        }

        public void activate( PhetApplication app ) {
            new Thread( new Runnable() {
                public void run() {
                    while( true ) {
                        double yval = height + .04;
                        b = new Segment( 2, yval, 4, yval );
                        bg.setState( b.getStartPoint().getX(), b.getStartPoint().getY(), b.getFinishPoint().getX(), b.getFinishPoint().getY() );
                        Point2D.Double intersectionPoint = b.getIntersection( a );
                        if( intersectionPoint != null ) {
                            cg.setLocation( intersectionPoint );
                        }
                        getApparatusPanel().repaint();
                        try {
                            Thread.sleep( 50 );
                        }
                        catch( InterruptedException e ) {
                            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                        }
                        setHeight( yval );
                    }
                }
            } ).start();
        }

        public void setHeight( double h ) {
            this.height = h;
        }

        public void deactivate( PhetApplication app ) {
        }

    }

    public static void main( String[] args ) {
        Module testModule = new TestModule( "Test" );
        PhetApplication pa = new PhetApplication( new ApplicationDescriptor( "title", "desc", "version", new FrameSetup() {
            public void initialize( JFrame frame ) {
                frame.setSize( 400, 400 );
                frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
                frame.setVisible( true );
            }
        } ), testModule, new FakeClock() );
        pa.startApplication( testModule );
    }
}
