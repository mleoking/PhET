/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.PanZoomWorldKeyHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:51 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3PhetPCanvas extends PhetPCanvas {
    private EC3Module ec3Module;
    private EnergyConservationModel ec3Model;
    private AbstractSpline spline;
    private ArrayList bodyGraphics = new ArrayList();
    private AbstractSpline revspline;

    public EC3PhetPCanvas( EC3Module ec3Module ) {
        this.ec3Module = ec3Module;
        this.ec3Model = ec3Module.getEnergyConservationModel();
        for( int i = 0; i < ec3Model.numBodies(); i++ ) {
            BodyGraphic bodyGraphic = new BodyGraphic( ec3Module, ec3Model.bodyAt( i ) );
            bodyGraphics.add( bodyGraphic );
            addWorldChild( bodyGraphic );
        }

//        spline = new CubicSpline( 50 );
        spline = new CubicSpline( 30 );


//        spline.addControlPoint( 150, 300 );
//        spline.addControlPoint( 200, 320 );
//        spline.addControlPoint( 350, 300 );
//        spline.addControlPoint( 400, 375 );

//        spline.addControlPoint( 125, 198 );
//        spline.addControlPoint( 250, 512 );
//        spline.addControlPoint( 591, 447 );
//        spline.addControlPoint( 419, 130 );

//        spline.addControlPoint( 125, 198 );
//        spline.addControlPoint( 250, 512 );
//        spline.addControlPoint( 591, 447 );
//        spline.addControlPoint( 747, 189 );
//
//        spline.addControlPoint( 125, 198 );
//        spline.addControlPoint( 250, 512 );
//        spline.addControlPoint( 591, 447 );
//        spline.addControlPoint( 620, 198 );
//        spline.addControlPoint( 700, 198 );
//        spline.addControlPoint( 750, 198 );
//        spline.addControlPoint( 800, 198 );

        spline.addControlPoint( 125, 198 );
        spline.addControlPoint( 250, 512 );
        spline.addControlPoint( 591, 447 );
        spline.addControlPoint( 543, 147 );
        spline.addControlPoint( 422, 333 );
        spline.addControlPoint( 810, 351 );
        spline.addControlPoint( 800, 198 );
        revspline = spline.createReverseSpline();
        SplineGraphic splineGraphic = new SplineGraphic( spline, revspline );
        ec3Model.addSpline( spline );
        ec3Model.addSpline( revspline );
//        ec3Model.addSpline( spline.createReverseSpline() );

        addWorldChild( splineGraphic );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_P ) {
                    System.out.println( "spline.getSegmentPath().getLength() = " + spline.getSegmentPath().getLength() );
                    printControlPoints();
                }
                else if( e.getKeyCode() == KeyEvent.VK_B ) {
                    toggleBox();
                }
                else if( e.getKeyCode() == KeyEvent.VK_A ) {
                    addSkater();
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        addKeyListener( new PanZoomWorldKeyHandler( this ) );
    }

    private void addSkater() {
        Body body = new Body( Body.createDefaultBodyRect() );
        ec3Model.addBody( body );

//        for( int i = 0; i < ec3Model.numBodies(); i++ ) {
        BodyGraphic bodyGraphic = new BodyGraphic( ec3Module, body );
        bodyGraphics.add( bodyGraphic );
        addWorldChild( bodyGraphic );
//        }
    }

    private void toggleBox() {
        if( bodyGraphics.size() > 0 ) {
            boolean state = ( (BodyGraphic)bodyGraphics.get( 0 ) ).isBoxVisible();
            for( int i = 0; i < bodyGraphics.size(); i++ ) {
                BodyGraphic bodyGraphic = (BodyGraphic)bodyGraphics.get( i );
                bodyGraphic.setBoxVisible( !state );
            }
        }
    }

    private void printControlPoints() {
        spline.printControlPointCode();
    }

}
