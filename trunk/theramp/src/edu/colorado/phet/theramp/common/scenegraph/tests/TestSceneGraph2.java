/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.theramp.common.scenegraph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 3, 2005
 * Time: 7:14:45 PM
 * Copyright (c) Jun 3, 2005 by Sam Reid
 */

public class TestSceneGraph2 extends SceneGraphPanel {
    public WorldGraphic worldGraphic;
    public Timer timer;

    public TestSceneGraph2() {
        getGraphic().setAntialias( true );
        getGraphic().setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );

        FillGraphic spaceGraphic = new FillGraphic( new Rectangle( 1000, 1000 ), Color.black );
        addGraphic( spaceGraphic );

        worldGraphic = new WorldGraphic();
        addGraphic( worldGraphic );
        worldGraphic.addMouseListener( new Translator() );
        worldGraphic.addMouseListener( new Repaint() );



//        addGraphic( new ImageGraphic( "images/earth.gif" ));

        TextGraphic textGraphic = new TextGraphic( "Testing Scene Graph Panel", Color.white );
        addGraphic( textGraphic );

        JButton zoomIn = new JButton( "Zoom in" );
        zoomIn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoomIn();
            }
        } );

        JButton zoomOut = new JButton( "Zoom zoomOut" );
        zoomOut.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoomOut();
            }
        } );
        AbstractGraphic inBtn = SceneGraphJComponent.newInstance( zoomIn );
        System.out.println( "textGraphic.getLocalHeight() = " + textGraphic.getLocalHeight() );
        inBtn.setLocation( 10, 10 + textGraphic.getLocalHeight() );
        addGraphic( inBtn );

        AbstractGraphic outBtn = SceneGraphJComponent.newInstance( zoomOut );
        outBtn.setLocation( 10, 10 + textGraphic.getLocalHeight() + inBtn.getLocalHeight() );
        addGraphic( outBtn );

        textGraphic.setLocation( 10, 10 );
        double shellRadius = getShellRadius();
        double viewportRadius = shellRadius * scale;
        worldGraphic.setTransformViewport( new Rectangle( 600, 600 ), getViewportRect( viewportRadius ) );

//        final PointerGraphic pointerGraphic = new TestSceneGraph2.PointerGraphic( worldGraphic.getButtonGraphic() );
        final PointerGraphic pointerGraphic = new TestSceneGraph2.PointerGraphic( worldGraphic.getManGraphic() );
        addGraphic( pointerGraphic );
        repaint();

        Timer t = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pointerGraphic.update();
                repaint();
            }
        } );
        t.start();
    }

    private Rectangle2D.Double getViewportRect( double viewWidth ) {
        Point2D.Double center = new Point2D.Double( 0, -radiusEarth );
        Point2D.Double corner = new Point2D.Double( center.getX() + viewWidth * 2, center.getY() + viewWidth * 2 );
        Rectangle2D.Double r = new Rectangle2D.Double();
        r.setFrameFromCenter( center, corner );
        return r;
//        return new Rectangle2D.Double( -radiusEarth, -radiusEarth, viewWidth * 2, viewWidth * 2 );
//        return new Rectangle2D.Double( -radiusEarth, -radiusEarth, viewWidth * 2, viewWidth * 2 );
    }

//    static double ingescale = 1;//IAH did this one. :)
    static double radiusEarth = 600;/// ingescale;//meters.
    static double altitudeSpace = 100;// / ingescale;//meters

    private static double getShellRadius() {
        double shellRadius = radiusEarth + altitudeSpace;
        return shellRadius;
    }

    double scale = 1.3;

    private void zoomIn() {
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                double viewportRadius = getShellRadius() * scale;
                worldGraphic.setTransformViewport( new Rectangle( 600, 600 ), getViewportRect( viewportRadius ) );
//                scale = scale * 0.98;
                scale = scale * 0.8;
                repaint();
                System.out.println( "scale = " + scale );
//                if( scale < 10E-4 ) {
                if( viewportRadius < 2.5 ) {
                    timer.stop();
                }
            }
        } );
        timer.start();
    }

    private void zoomOut() {
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                double viewportRadius = getShellRadius() * scale;
                worldGraphic.setTransformViewport( new Rectangle( 600, 600 ), getViewportRect( viewportRadius ) );
                scale = scale / 0.8;
                repaint();
                System.out.println( "scale = " + scale );
                if( scale > 10 ) {
                    timer.stop();
                }
            }
        } );
        timer.start();
    }

    static class WorldGraphic extends GraphicLayerNode {
        public AbstractGraphic buttonGraphic;
        private AbstractGraphic manGraphic;

        public WorldGraphic() {
            double shellRadius = getShellRadius();
            setName( "WorldGraphic" );
            FillGraphic sky = new FillGraphic( toEllipse( shellRadius ), Color.blue );

            Ellipse2D.Double earthEllipse = toEllipse( radiusEarth );
//            AbstractGraphic earth = new ImageGraphic( "images/earth.gif" );
//            AbstractGraphic earth = new ImageGraphic( "images/ball-earth.png" );
//            AbstractGraphic earth = new ImageGraphic( "images/earth3.gif" );
            //            FillGraphic earth = new FillGraphic( toEllipse( radiusEarth ), Color.green );
            AbstractGraphic earth = new FillGraphic( new Ellipse2D.Double( 0, 0, 1, 1 ), Color.green );
            earth.setTransformBounds( earthEllipse.getX(), earthEllipse.getY(), earthEllipse.getWidth(), earthEllipse.getHeight() );
            addGraphic( sky );
            addGraphic( earth );
            double stickHeight = 2.0;
//            Rectangle2D r = new Rectangle2D.Double( 0, -radiusEarth / ingescale - stickHeight, stickHeight / 2, stickHeight );
            Rectangle2D r = new Rectangle2D.Double( 0, -radiusEarth - stickHeight, stickHeight / 2, stickHeight );
            manGraphic = new FillGraphic( r, Color.orange );
            manGraphic.addMouseListener( new Translator() );
            manGraphic.addMouseListener( new Repaint() );
            addGraphic( manGraphic );

            JButton buttonhead = new JButton( "Button" );
            buttonGraphic = SceneGraphJComponent.newInstance( buttonhead );

            buttonGraphic.setTransformBounds( r.getX(), r.getY(), r.getWidth(), r.getWidth() * buttonGraphic.getLocalHeight() / buttonGraphic.getLocalWidth() );
            addGraphic( buttonGraphic );
//            setComposite( true );
        }

        private Ellipse2D.Double toEllipse( double radiusEarth ) {
            return new Ellipse2D.Double( -radiusEarth, -radiusEarth, radiusEarth * 2, radiusEarth * 2 );
        }

        public AbstractGraphic getButtonGraphic() {
            return buttonGraphic;
        }

        public AbstractGraphic getManGraphic() {
            return manGraphic;
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        SceneGraphJComponent.init( frame );
        SceneGraphPanel sceneGraphPanel = new TestSceneGraph2();
        frame.setContentPane( sceneGraphPanel );
        frame.setSize( 600, 600 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public class PointerGraphic extends GraphicLayerNode {
        private AbstractGraphic target;
        public TextGraphic textGraphic;

        public PointerGraphic( AbstractGraphic target ) {
            this.target = target;
//            FillGraphic fillGraphic = new FillGraphic( new Rectangle( -15, -15, 30, 30 ), new Color( 200,20,20,128) );
            Arrow arrow = new Arrow( new Point2D.Double( 30, 0 ), new Point2D.Double( 0, 0 ), 10, 10, 5 );
//            FillGraphic fillGraphic = new FillGraphic( new Rectangle( 30, 30 ), new Color( 200, 20, 20, 128 ) );
            FillGraphic fillGraphic = new FillGraphic( arrow.getShape(), Color.red );
            addGraphic( fillGraphic );
            textGraphic = new TextGraphic( "", Color.white );
            textGraphic.setFontLucidaSansBold( 16 );
            addGraphic( textGraphic );
            update();
        }

        public void update() {
            GraphicNode root = target.getRoot();
//            System.out.println( "root = " + root );
            Shape boundsInRoot = target.getBoundsIn( root );
//            System.out.println( "target.getLocalBounds() = " + target.getLocalBounds() );
//            System.out.println( "boundsInRoot = " + boundsInRoot );
            Rectangle2D rectInRoot = boundsInRoot.getBounds2D();
            System.out.println( "rectInRoot = " + rectInRoot );
            System.out.println( "rectInRoot.getCenterY() = " + rectInRoot.getCenterY() );
            setLocation( rectInRoot.getCenterX(), rectInRoot.getCenterY() );
//            textGraphic.setText(""+rectInRoot.getCenterY());
            textGraphic.setText( "" + rectInRoot.getX() + "," + rectInRoot.getY() );
        }
    }
}
