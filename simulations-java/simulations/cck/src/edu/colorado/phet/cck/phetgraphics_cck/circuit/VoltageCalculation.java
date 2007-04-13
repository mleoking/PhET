/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Connection;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.piccolo_cck.GraphTraversalVoltage;
import edu.colorado.phet.cck.piccolo_cck.VoltageDifference;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 7, 2004
 * Time: 4:57:32 PM
 * Copyright (c) Oct 7, 2004 by Sam Reid
 */
public class VoltageCalculation {
    private Circuit circuit;
    private VoltageDifference voltageDifference;

    public VoltageCalculation( Circuit circuit ) {
        this.circuit = circuit;
        voltageDifference = new GraphTraversalVoltage( circuit );
    }

    public double getVoltage( Connection a, Connection b ) {
        if( a.equals( b ) ) {
            return 0;
        }
        else {
            Junction startJ = a.getJunction();
            Junction endJ = b.getJunction();
            double va = a.getVoltageAddon();
            double vb = -b.getVoltageAddon();//this has to be negative, because on the path VA->A->B->VB, the the VB computation is VB to B.
//            System.out.println( "va = " + va );
//            System.out.println( "vb = " + vb );
            double voltInit = ( va + vb );
//            double voltInit = ( va - vb );
//            double voltInit = ( vb - va );
//            double voltInit = -( va + vb );
            //the sign of va and vb depend on the
//            System.out.println( "voltInit = " + voltInit );

            double junctionAnswer = voltageDifference.getVoltage( new ArrayList(), startJ, endJ, 0 );
            double junctionAnswer2 = -voltageDifference.getVoltage( new ArrayList(), endJ, startJ, 0 );
//            System.out.println( "junctionAnswer = " + junctionAnswer );
//            System.out.println( "junctionAnswer2 = " + junctionAnswer2 );
            double diff = ( junctionAnswer - junctionAnswer2 );
            if( diff > .0001 && !Double.isInfinite( junctionAnswer ) && !Double.isInfinite( junctionAnswer2 ) ) {
                new RuntimeException( "Junction answers inconsistent, ans1=" + junctionAnswer + ", ans2=" + junctionAnswer2 ).printStackTrace();
            }
            double result = Double.POSITIVE_INFINITY;
            if( !Double.isInfinite( junctionAnswer ) ) {
                result = ( junctionAnswer + voltInit );
            }
            else if( !Double.isInfinite( junctionAnswer2 ) ) {
                result = ( junctionAnswer2 + voltInit );
            }
            //            return result;
//            System.out.println( "result = " + result );
            return result;
        }
    }

    public double getVoltage( CircuitGraphic circuitGraphic, Shape a, Shape b ) {

        Area tipIntersection = new Area( a );
        tipIntersection.intersect( new Area( b ) );
        if( !tipIntersection.isEmpty() ) {
            return 0;
        }
        else {
            Connection red = detectConnection( circuitGraphic, a );
            Connection black = detectConnection( circuitGraphic, b );
            if( red == null || black == null ) {
                return Double.NaN;
            }
            else {
                //dfs from one branch to the other, counting the voltage drop.
                return circuit.getVoltage( red, black );
            }
        }
    }

    private Branch detectBranch( CircuitGraphic circuitGraphic, Shape tipShape ) {
        Graphic[] g = circuitGraphic.getBranchGraphics();
//        Shape tipShape = getTipShape();
        InteractiveBranchGraphic overlap = null;
        for( int i = g.length - 1; i >= 0; i-- ) {
            Graphic graphic = g[i];
            if( graphic instanceof InteractiveBranchGraphic ) {
                InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)graphic;
                Shape shape = ibg.getBranchGraphic().getCoreShape();//getShape();
                Area intersection = new Area( tipShape );
                intersection.intersect( new Area( shape ) );
                if( !intersection.isEmpty() ) {
                    overlap = ibg;
                    break;
                }
            }
        }
//            System.out.println( "overlap = " + overlap );
        if( overlap == null ) {
            return null;
        }
        else {
            return overlap.getBranch();
        }
    }

    public Connection detectConnection( CircuitGraphic circuitGraphic, Shape tipShape ) {
        Branch branch = detectBranch( circuitGraphic, tipShape );
        Junction junction = detectJunction( circuitGraphic, tipShape );
        Connection result = null;
        if( junction != null ) {
            result = new Connection.JunctionConnection( junction );
        }
        else if( branch != null ) {
            //could choose the closest junction
            //but we want a potentiometer.
            result = new Connection.JunctionConnection( branch.getStartJunction() );
            Rectangle tipRect = tipShape.getBounds();
            Point2D tipCenter = RectangleUtils.getCenter( tipRect );
            Point2D tipCenterModel = circuitGraphic.getTransform().viewToModel( new Point( (int)tipCenter.getX(), (int)tipCenter.getY() ) );
            Point2D.Double branchStartModel = branch.getStartJunction().getPosition();
//                Point2D branchStartModel = circuitGraphic.getTransform().viewToModel( (int)branchCenterView.getX(), (int)branchCenterView.getY() );
            Vector2D vec = new Vector2D.Double( branchStartModel, tipCenterModel );
            double dist = vec.getMagnitude();
            result = new Connection.BranchConnection( branch, dist );
        }
        return result;
    }

    private Junction detectJunction( CircuitGraphic circuitGraphic, Shape tipShape ) {
        Graphic[] j = circuitGraphic.getJunctionGraphics();
//        Shape tipShape = getTipShape();
        Junction junction = null;
        for( int i = 0; i < j.length; i++ ) {
            Graphic graphic = j[i];
            HasJunctionGraphic hj = (HasJunctionGraphic)graphic;
            JunctionGraphic jg = hj.getJunctionGraphic();
            Area area = new Area( jg.getShape() );
            area.intersect( new Area( tipShape ) );
            if( !area.isEmpty() ) {
                junction = jg.getJunction();
                break;
            }
        }
        return junction;
    }

}
