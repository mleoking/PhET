/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 8:51:31 PM
 * Copyright (c) Feb 4, 2005 by Sam Reid
 */

public class AnimatedButton3D extends CompositePhetGraphic {
    String text;
    int origDepth;
    double shearX;
    int baseInsetX = 5;
    int baseInsetY = 4;
    int surfaceInsetX = 4;
    int surfaceInsetY = 3;
    private int depth;
    private PhetTextGraphic phetTextGraphic;
    private PhetShapeGraphic buttonSurface;
    private PhetShapeGraphic base;
    private PhetShapeGraphic sidePanel;
    private PhetShapeGraphic frontPanel;
    private Point2D e;
    private Point2D f;
    private Point2D g;
    private Timer timer;
    private ActionListener animator;
    AnimationState state;
    ArrayList stateQueue = new ArrayList();
    Paint enterPaint;
    Paint normalPaint;

    public AnimatedButton3D( Component component, String text, final int origDepth, double shearX ) {
        super( component );
        this.text = text;
        this.origDepth = origDepth;
        this.depth = origDepth;
        this.shearX = shearX;
        BasicStroke stroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        Font font = new Font( "Lucida Sans", Font.BOLD, 14 );
        phetTextGraphic = new PhetTextGraphic( component, font, text, Color.black, 0, 0 );
        AffineTransform shear = AffineTransform.getShearInstance( shearX, 0 );

        Rectangle origTextBounds = phetTextGraphic.getBounds();
        origTextBounds = RectangleUtils.expand( origTextBounds, surfaceInsetX, surfaceInsetY );
        enterPaint = Color.green;
//        normalPaint=Color.yellow;

        Point a0 = new Point( origTextBounds.x, origTextBounds.y );
        Point b0 = new Point( origTextBounds.x + origTextBounds.width, origTextBounds.y );
        Point c0 = new Point( origTextBounds.x + origTextBounds.width, origTextBounds.y + origTextBounds.height );
        Point d0 = new Point( origTextBounds.x, origTextBounds.y + origTextBounds.height );

        Point2D a = shear.transform( a0, null );
        Point2D b = shear.transform( b0, null );
        Point2D c = shear.transform( c0, null );
        Point2D d = shear.transform( d0, null );

        normalPaint = new GradientPaint( a, Color.yellow, b, Color.white );

        DoubleGeneralPath buttonSurfacePath = new DoubleGeneralPath( a );
        buttonSurfacePath.lineTo( b );
        buttonSurfacePath.lineTo( c );
        buttonSurfacePath.lineTo( d );
        buttonSurfacePath.lineTo( a );

        buttonSurface = new PhetShapeGraphic( component, buttonSurfacePath.getGeneralPath(), normalPaint, stroke, Color.black );

        double dx = c.getX() - d.getX();
//        Shape buttonSurface = shear.createTransformedShape( phetTextGraphic.getBounds() );
        DoubleGeneralPath frontPanelPath = new DoubleGeneralPath( d );
        frontPanelPath.lineToRelative( 0, origDepth );
        e = frontPanelPath.getCurrentPoint();
        frontPanelPath.lineToRelative( dx, 0 );
        f = frontPanelPath.getCurrentPoint();
        frontPanelPath.lineToRelative( 0, -origDepth );

        frontPanelPath.lineTo( d );

        frontPanel = new PhetShapeGraphic( component, frontPanelPath.getGeneralPath(), normalPaint, stroke, Color.black );
        this.phetTextGraphic.transform( shear );

        double bcX = c.getX() - b.getX();
        double byC = c.getY() - b.getY();
        DoubleGeneralPath sidePanelPath = new DoubleGeneralPath( b );
        sidePanelPath.lineToRelative( 0, origDepth );
        g = sidePanelPath.getCurrentPoint();
        sidePanelPath.lineToRelative( bcX, byC );
        sidePanelPath.lineTo( c );
        sidePanelPath.lineTo( b );
        sidePanel = new PhetShapeGraphic( component, sidePanelPath.getGeneralPath(), normalPaint, stroke, Color.black );

        Point2D h = new Point2D.Double( a.getX(), a.getY() + origDepth );

        Point2D m = new Point2D.Double( e.getX() - baseInsetX, e.getY() + baseInsetY );
        Point2D n = new Point2D.Double( f.getX() + baseInsetX, f.getY() + baseInsetY );
        Point2D o = new Point2D.Double( g.getX() + baseInsetX, g.getY() - baseInsetY );
        Point2D p = new Point2D.Double( h.getX() - baseInsetX, h.getY() - baseInsetY );

        DoubleGeneralPath basePath = new DoubleGeneralPath( m );
        basePath.lineTo( n );
        basePath.lineTo( o );
        basePath.lineTo( p );
        basePath.lineTo( m );
        base = new PhetShapeGraphic( component, basePath.getGeneralPath(), Color.blue, stroke, Color.black );

        addGraphic( base );

        addGraphic( buttonSurface );
        addGraphic( phetTextGraphic );
        addGraphic( sidePanel );
        addGraphic( frontPanel );

        Point2D reg = new Point2D.Double( m.getX(), Math.min( p.getY(), a.getY() ) );
        setRegistrationPoint( (int)reg.getX(), (int)reg.getY() );

        addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                stateQueue.add( new MovingDown() );
                timer.start();
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                stateQueue.add( new MovingUp() );
                timer.start();
            }

        } );

        addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mouseEntered( MouseEvent e ) {
//                buttonSurface.setPaint( enterPaint );
//                frontPanel.setPaint( enterPaint );
//                sidePanel.setPaint( enterPaint );

                buttonSurface.setBorderPaint( Color.gray );
                frontPanel.setBorderPaint( Color.gray );
                sidePanel.setBorderPaint( Color.gray );
            }

            // implements java.awt.event.MouseListener
            public void mouseExited( MouseEvent e ) {
//                buttonSurface.setPaint( normalPaint );
//                frontPanel.setPaint( normalPaint );
//                sidePanel.setPaint( normalPaint );

                buttonSurface.setBorderPaint( Color.black );
                frontPanel.setBorderPaint( Color.black );
                sidePanel.setBorderPaint( Color.black );
            }
        } );
        animator = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean finished = state == null || state.animate();
                if( finished ) {
                    state = null;
                    if( stateQueue.size() > 0 ) {
                        state = (AnimationState)stateQueue.remove( 0 );
                    }
                    else {
                        timer.stop();
                    }
                }
            }
        };
        timer = new Timer( 30, animator );
        setCursorHand();
    }

    ArrayList listeners = new ArrayList();

    public void addActionListener( ActionListener actionListener ) {
        listeners.add( actionListener );
    }

    interface AnimationState {

        boolean animate();
    }

    class MovingDown implements AnimationState {

        public boolean animate() {
            int newDepth = Math.max( depth - 4, 0 );
            boolean fireEvent = newDepth == 0 && depth != 0;
            setDepth( newDepth );
            if( fireEvent ) {
                fireEvent();
            }
            return depth == 0;
        }

        private void fireEvent() {
            ActionEvent event = new ActionEvent( AnimatedButton3D.this, 0, "cmd" );
            for( int i = 0; i < listeners.size(); i++ ) {
                ActionListener actionListener = (ActionListener)listeners.get( i );
                actionListener.actionPerformed( event );
            }
        }
    }

    class MovingUp implements AnimationState {

        public boolean animate() {
            int newDepth = Math.min( depth + 4, origDepth );
            setDepth( newDepth );
            return depth == origDepth;
        }
    }

    private void setDepth( int depth ) {
        this.depth = depth;
        double dy = origDepth - depth;
        phetTextGraphic.setLocation( 0, (int)dy );
        buttonSurface.setLocation( 0, (int)dy );
        DoubleGeneralPath frontPanelPath = new DoubleGeneralPath( e );
        frontPanelPath.lineTo( f );
        frontPanelPath.lineToRelative( 0, -depth );
        frontPanelPath.lineToRelative( e.getX() - f.getX(), 0 );
        frontPanelPath.lineTo( e );
        frontPanel.setShape( frontPanelPath.getGeneralPath() );

        DoubleGeneralPath sidePanelPath = new DoubleGeneralPath( f );
        sidePanelPath.lineTo( g );
        sidePanelPath.lineToRelative( 0, -depth );
        sidePanelPath.lineToRelative( f.getX() - g.getX(), f.getY() - g.getY() );
        sidePanelPath.lineTo( f );
        sidePanel.setShape( sidePanelPath.getGeneralPath() );
    }


}
