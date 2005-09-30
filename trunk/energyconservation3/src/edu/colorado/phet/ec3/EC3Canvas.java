/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.FloorGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.ec3.view.SplineMatch;
import edu.colorado.phet.piccolo.PanZoomWorldKeyHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:51 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3Canvas extends PhetPCanvas {
    private EC3Module ec3Module;
    private EnergyConservationModel ec3Model;

    private PNode bodyGraphics = new PNode();
    private PNode splineGraphics = new PNode();
    public static final int NUM_CUBIC_SPLINE_SEGMENTS = 30;

//    ArrayList pressedKeys = new ArrayList();
    HashMap pressedKeys = new HashMap();
    private static final Object DUMMY_VALUE = new Object();

    public EC3Canvas( EC3Module ec3Module ) {
        this.ec3Module = ec3Module;
        this.ec3Model = ec3Module.getEnergyConservationModel();
        Floor floor = ec3Model.floorAt( 0 );
        getPhetRootNode().addBackWorldChild( new SkyGraphic( floor.getY() ) );
        addWorldChild( new FloorGraphic( floor ) );

        for( int i = 0; i < ec3Model.numBodies(); i++ ) {
            BodyGraphic bodyGraphic = new BodyGraphic( ec3Module, ec3Model.bodyAt( i ) );
            addBodyGraphic( bodyGraphic );
        }

        CubicSpline spline = new CubicSpline( NUM_CUBIC_SPLINE_SEGMENTS );
        SplineToolbox splineToolbox = new SplineToolbox( this );
//        splineToolbox.setOffset( 50,50);
        getPhetRootNode().addBackScreenChild( splineToolbox );

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
        AbstractSpline revspline = spline.createReverseSpline();
        SplineGraphic splineGraphic = new SplineGraphic( this, spline, revspline );
        ec3Model.addSpline( spline, revspline );
//        ec3Model.addSpline( spline.createReverseSpline() );

        addSplineGraphic( splineGraphic );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                pressedKeys.put( new Integer( e.getKeyCode() ), DUMMY_VALUE );
                if( e.getKeyCode() == KeyEvent.VK_P ) {
                    System.out.println( "spline.getSegmentPath().getLength() = " + ec3Model.splineAt( 0 ).getSegmentPath().getLength() );
                    printControlPoints();
                }
                else if( e.getKeyCode() == KeyEvent.VK_B ) {
                    toggleBox();
                }
                else if( e.getKeyCode() == KeyEvent.VK_A ) {
                    addSkater();
                }
                else if( e.getKeyCode() == KeyEvent.VK_N ) {
                    addSpline();
                }
            }

            public void keyReleased( KeyEvent e ) {
                pressedKeys.remove( new Integer( e.getKeyCode() ) );
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        addKeyListener( new PanZoomWorldKeyHandler( this ) );
        addWorldChild( splineGraphics );
        addWorldChild( bodyGraphics );
        ec3Model.addEnergyModelListener( new EnergyConservationModel.EnergyModelListener() {
            public void preStep( double dt ) {
                updateThrust();
            }
        } );
    }

    private void updateThrust() {
        Body body = ec3Model.bodyAt( 0 );
        double xThrust = 0.0;
        double yThrust = 0.0;
        int thrustValue = 5000;
        if( pressedKeys.containsKey( new Integer( KeyEvent.VK_RIGHT ) ) ) {
            xThrust = thrustValue;
        }
        else if( pressedKeys.containsKey( new Integer( KeyEvent.VK_LEFT ) ) ) {
            xThrust = -thrustValue;
        }
        if( pressedKeys.containsKey( new Integer( KeyEvent.VK_UP ) ) ) {
            yThrust = -thrustValue;
        }
        else if( pressedKeys.containsKey( new Integer( KeyEvent.VK_DOWN ) ) ) {
            yThrust = thrustValue;
        }
        body.setThrust( xThrust, yThrust );
    }

    public void addSplineGraphic( SplineGraphic splineGraphic ) {
//        addWorldChild( splineGraphic );
        splineGraphics.addChild( splineGraphic );
    }

    private void addSpline() {
        CubicSpline spline = new CubicSpline( NUM_CUBIC_SPLINE_SEGMENTS );
        spline.addControlPoint( 50, 50 );
        spline.addControlPoint( 150, 50 );
        spline.addControlPoint( 300, 50 );
        AbstractSpline rev = spline.createReverseSpline();

        ec3Model.addSpline( spline, rev );
        SplineGraphic splineGraphic = new SplineGraphic( this, spline, rev );
        addSplineGraphic( splineGraphic );
    }

    private void addSkater() {
        Body body = new Body( Body.createDefaultBodyRect() );
        ec3Model.addBody( body );

//        for( int i = 0; i < ec3Model.numBodies(); i++ ) {
        BodyGraphic bodyGraphic = new BodyGraphic( ec3Module, body );
        addBodyGraphic( bodyGraphic );
//        }
    }

    private void addBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.addChild( bodyGraphic );
//        addWorldChild( bodyGraphic );
    }

    private void toggleBox() {
        if( bodyGraphics.getChildrenReference().size() > 0 ) {
            boolean state = ( (BodyGraphic)bodyGraphics.getChildrenReference().get( 0 ) ).isBoxVisible();
            for( int i = 0; i < bodyGraphics.getChildrenReference().size(); i++ ) {
                BodyGraphic bodyGraphic = (BodyGraphic)bodyGraphics.getChildrenReference().get( i );
                bodyGraphic.setBoxVisible( !state );
            }
        }
    }

    private void printControlPoints() {
        ec3Model.splineAt( 0 ).printControlPointCode();
    }

    int threshold = 100;

    public SplineMatch proposeMatch( SplineGraphic splineGraphic, final Point2D toMatch ) {

        ArrayList matches = new ArrayList();

        for( int i = 0; i < numSplineGraphics(); i++ ) {
            SplineGraphic target = splineGraphicAt( i );
            PNode startNode = target.getControlPointGraphic( 0 );
            double dist = distance( toMatch, startNode );

            if( dist < threshold && ( splineGraphic != target ) ) {
                SplineMatch match = new SplineMatch( target, 0 );
                matches.add( match );
            }

            PNode endNode = target.getControlPointGraphic( target.numControlPointGraphics() - 1 );
            double distEnd = distance( toMatch, endNode );
            if( distEnd < threshold && splineGraphic != target ) {
                SplineMatch match = new SplineMatch( target, target.numControlPointGraphics() - 1 );
                matches.add( match );
            }
        }
        Collections.sort( matches, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                SplineMatch a = (SplineMatch)o1;
                SplineMatch b = (SplineMatch)o2;
                return Double.compare( distance( toMatch, a.getTarget() ), distance( toMatch, b.getTarget() ) );
            }
        } );
        if( matches.size() == 0 ) {
            return null;
        }
        return (SplineMatch)matches.get( 0 );
    }

    private double distance( Point2D toMatch, PNode startNode ) {
        double dist = startNode.getFullBounds().getCenter2D().distance( toMatch );
        return dist;
    }

    private SplineGraphic splineGraphicAt( int i ) {
        return (SplineGraphic)splineGraphics.getChildrenReference().get( i );
    }

    private int numSplineGraphics() {
        return splineGraphics.getChildrenReference().size();
    }

    public void attach( SplineGraphic splineGraphic, int index, SplineMatch match ) {
        //delete both of those, add one new parent.
        removeSpline( splineGraphic );
        removeSpline( match.getSplineGraphic() );
//        ec3Model.removeSpline( splineGraphic.getSpline() );
//        ec3Model.removeSpline( splineGraphic.getReverseSpline() );
//        ec3Model.removeSpline( match.getSplineGraphic().getSpline() );
//        ec3Model.removeSpline( match.getSplineGraphic().getReverseSpline() );
//
//        removeSplineGraphic( splineGraphic );
//        removeSplineGraphic( match.getSplineGraphic() );

        AbstractSpline spline = new CubicSpline( NUM_CUBIC_SPLINE_SEGMENTS );
        AbstractSpline a = splineGraphic.getSpline();
        AbstractSpline b = match.getSpline();
        if( index == 0 ) {
            for( int i = a.numControlPoints() - 1; i >= 0; i-- ) {
                spline.addControlPoint( a.controlPointAt( i ) );
            }
        }
        else {
            for( int i = 0; i < a.numControlPoints(); i++ ) {
                spline.addControlPoint( a.controlPointAt( i ) );
            }
        }
        if( match.matchesBeginning() ) {
            for( int i = 1; i < b.numControlPoints(); i++ ) {
                spline.addControlPoint( b.controlPointAt( i ) );
            }
        }
        else if( match.matchesEnd() ) {
            for( int i = b.numControlPoints() - 2; i >= 0; i-- ) {
                spline.addControlPoint( b.controlPointAt( i ) );
            }
        }
        AbstractSpline reverse = spline.createReverseSpline();
        ec3Model.addSpline( spline, reverse );
        addSplineGraphic( new SplineGraphic( this, spline, reverse ) );
    }

    private void removeSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.removeChild( splineGraphic );
    }

    public EnergyConservationModel getEnergyConservationModel() {
        return ec3Model;
    }

    public void removeSpline( SplineGraphic splineGraphic ) {
        removeSplineGraphic( splineGraphic );
        ec3Model.removeSpline( splineGraphic.getSpline() );
        ec3Model.removeSpline( splineGraphic.getReverseSpline() );

    }
}
