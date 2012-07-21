// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import java.awt.Dimension;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.surfaces.LineSurface;

import edu.colorado.phet.common.phetcommon.math.vector.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 1:22:41 AM
 */

public class MoveElectronsJade implements ModelElement {
    private JadeElectronSet jadeElectronSet;
    private DynamicsEngine engine;
    private DoubleGeneralPath path;
    private Area area;
    private boolean debug = true;

    /**
     * Sets up the simulator and graphics for a basic JADE car simulator.
     */
    public MoveElectronsJade( JadeElectronSet jadeElectronSet ) {
        this.jadeElectronSet = jadeElectronSet;
        jadeElectronSet.addListener( new JadeElectronSet.Adapter() {
            public void electronAdded( JadeElectron electron ) {
                engine.addPrimitive( electron );
            }

            public void electronRemoved( JadeElectron electron ) {
                engine.removePrimitive( electron );
            }
        } );
        engine = new DynamicsEngine();

        engine.setDamping( 0.95 );
        engine.setGravity( 0.0, 0.0 );
        engine.setSurfaceBounce( 0.9 );

        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer( str, "\n\t" );
        while ( st.hasMoreTokens() ) {
            list.add( new Point2D.Double( Double.parseDouble( st.nextToken() ), Double.parseDouble( st.nextToken() ) ) );
        }
        for ( int i = 1; i < list.size(); i++ ) {
            Point2D.Double prev = (Point2D.Double) list.get( i - 1 );
            Point2D.Double cur = (Point2D.Double) list.get( i );
            LineSurface s = new LineSurface( cur.getX(), cur.getY(), prev.getX(), prev.getY() );
            s.setCollisionDepth( 25 );
            engine.addSurface( s );
        }

        path = new DoubleGeneralPath();
        for ( int i = 0; i < list.size(); i++ ) {
            Point2D.Double cur = (Point2D.Double) list.get( i );
            if ( i == 0 ) {
                path.moveTo( cur.getX(), cur.getY() );
            }
            else {
                path.lineTo( cur.getX(), cur.getY() );
            }
        }
        path.closePath();
        area = new Area( path.getGeneralPath() );
    }

    protected DynamicsEngine getEngine() {
        return engine;
    }

    protected AbstractVector2D getForce( JadeElectron node ) {
        MutableVector2D sum = new MutableVector2D();
        for ( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
            JadeElectron particle = jadeElectronSet.getJadeElectron( i );
            if ( particle != node ) {
                sum = sum.add( getForce( node, particle ) );
            }
        }
        if ( isLegal( sum ) ) {
            return sum;
        }
        else {
            return new MutableVector2D();
        }
    }

    private boolean isLegal( MutableVector2D sum ) {
        return !Double.isInfinite( sum.getX() ) && !Double.isNaN( sum.getX() ) && !Double.isInfinite( sum.getY() ) && !Double.isNaN( sum.getY() );
    }

    protected AbstractVector2D getForce( JadeElectron circleParticle, JadeElectron particle ) {
        return getForce( circleParticle, particle, 5.0 );
    }

    protected AbstractVector2D getForce( JadeElectron circleParticle, JadeElectron particle, double k ) {
        AbstractVector2D vec = new MutableVector2D( circleParticle.getPosition(), particle.getPosition() );
        if ( vec.getMagnitude() <= 1 ) {
            return new MutableVector2D();
        }
        Vector2D v = vec.getInstanceOfMagnitude( -k / Math.pow( vec.getMagnitude(), 1.5 ) );
        double max = 0.05;
        if ( v.getMagnitude() > max ) {
            v = v.getInstanceOfMagnitude( max );
        }
//        System.out.println( "v = " + v );
        return v;
    }

    /**
     * Component - returns the preferred size (at this stage, 640x480)
     */
    public Dimension getPreferredSize() {
        return new Dimension( 640, 480 );
    }

    public static final String str = "163\t200\n" +
                                     "165\t224\n" +
                                     "186\t252\n" +
                                     "187\t269\n" +
                                     "208\t335\n" +
                                     "223\t351\n" +
                                     "242\t350\n" +
                                     "262\t338\n" +
                                     "276\t345\n" +
                                     "218\t402\n" +
                                     "192\t377\n" +
                                     "181\t371\n" +
                                     "147\t293\n" +
                                     "129\t273\n" +
                                     "86\t366\n" +
                                     "95\t381\n" +
                                     "123\t390\n" +
                                     "128\t401\n" +
                                     "91\t404\n" +
                                     "46\t402\n" +
                                     "44\t360\n" +
                                     "83\t274\n" +
                                     "46\t234\n" +
                                     "4\t218\n" +
                                     "4\t198\n" +
                                     "24\t140\n" +
                                     "77\t65\n" +
                                     "111\t48\n" +
                                     "133\t48\n" +
                                     "137\t40\n" +
                                     "145\t40\n" +
                                     "164\t10\n" +
                                     "186\t7\n" +
                                     "212\t19\n" +
                                     "211\t28\n" +
                                     "206\t32\n" +
                                     "200\t50\n" +
                                     "202\t61\n" +
                                     "191\t77\n" +
                                     "173\t74\n" +
                                     "167\t79\n" +
                                     "174\t94\n" +
                                     "181\t107\n" +
                                     "183\t123\n" +
                                     "190\t136\n" +
                                     "207\t145\n" +
                                     "286\t120\n" +
                                     "298\t135\n" +
                                     "296\t144\n" +
                                     "275\t150\n" +
                                     "270\t169\n" +
                                     "200\t183\n" +
                                     "172\t172\n" +
                                     "162\t200";

    public void stepInTime( double dt ) {
        for ( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
            JadeElectron circleParticle = jadeElectronSet.getJadeElectron( i );
            AbstractVector2D force = getForce( circleParticle );
            circleParticle.setAcceleration( force.getX(), force.getY() );
        }
        engine.timeStep();
        for ( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
            jadeElectronSet.getJadeElectron( i ).notifyElectronMoved();
        }
//        for( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
//            System.out.println( "jes: " + jadeElectronSet.getJadeElectron( i ).getPosition() );
//        }
        testRemove();
        count++;
//        remapLocations();
    }

    private void testRemove() {
        int problemCount = 0;
        for ( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
            if ( isProblematic( jadeElectronSet.getJadeElectron( i ) ) ) {
                problemCount++;
//                System.out.println( "jadeElectronSet.getJadeElectron( i).getPosition() = " + jadeElectronSet.getJadeElectron( i ).getPosition() );
                if ( !contains( jadeElectronSet.getJadeElectron( i ) ) ) {
                    if ( debug ) {
                        System.out.println( "Removed bogus electron." );
                    }
                    remove( i );
                    i--;
                }
            }
        }
        if ( debug && problemCount > 0 ) {
            System.out.println( "problemCount total= " + problemCount );
        }
    }

    private boolean isProblematic( JadeElectron jadeElectron ) {
        return jadeElectron.getPosition().getX() > 266 && jadeElectron.getPosition().getY() > 300;
    }

    int count = 0;

    private void remove( int i ) {
        jadeElectronSet.removeElectron( i );
    }

    private boolean contains( JadeElectron jadeElectron ) {
        return area.contains( jadeElectron.getPosition() );
    }

}
