/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.chart.AbstractFloatingChart;
import edu.colorado.phet.common.model.clock.IClock;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 11:43:52 AM
 * Copyright (c) Jun 22, 2006 by Sam Reid
 */

public class VoltageStripChart extends AbstractFloatingChart {
//    private CircuitGraphic circuitGraphic;
//
//    public VoltageStripChart( PSwingCanvas pSwingCanvas, String title, IClock clock, CircuitGraphic circuitGraphic ) {
//        super( pSwingCanvas, title, new ValueReader() {
//            public double getValue( double x, double y ) {
//                return 0;
//            }
//        }, clock );
//        this.circuitGraphic = circuitGraphic;
//        super.setValueReader( new VoltageStripChart.VoltageReader() );
//    }
//
//    public void recomputeVoltage() {
//        if( !getVisible() ) {
//            return;
//        }
//        Area tipIntersection = new Area( getRedTipShape() );
//        tipIntersection.intersect( new Area( blackLeadGraphic.getTipShape() ) );
//        if( !tipIntersection.isEmpty() ) {
//            unitGraphic.setVoltage( 0 );
//        }
//        else {
//            VoltmeterGraphic.Connection red = redLeadGraphic.detectConnection( module.getCircuitGraphic() );
//            VoltmeterGraphic.Connection black = blackLeadGraphic.detectConnection( module.getCircuitGraphic() );
//            if( red == null || black == null ) {
//                unitGraphic.setUnknownVoltage();
//            }
//            else {
//                //dfs from one branch to the other, counting the voltage drop.
//                double volts = module.getCircuit().getVoltage( red, black );
//                if( Double.isInfinite( volts ) ) {
//                    unitGraphic.setUnknownVoltage();
//                }
//                else {
//                    unitGraphic.setVoltage( volts );
//                }
//            }
//        }
//    }
//
//    public class VoltageReader implements ValueReader2Terminal {
//        public double getValue( Point2D p1, Point2D p2 ) {
//            Point2D target = new Point2D.Double( x, y );
//            //check for intersect with circuit.
//            Graphic[] g = circuitGraphic.getBranchGraphics();
//            for( int i = g.length - 1; i >= 0; i-- ) {
//                Graphic graphic = g[i];
//                if( graphic instanceof InteractiveBranchGraphic ) {
//                    InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)graphic;
//                    Branch branch = ibg.getBranch();
//                    BranchGraphic branchGraphic = ibg.getBranchGraphic();
//                    Shape shape = branchGraphic.getCoreShape();//getShape();
//                    if( shape.contains( target ) ) {
//                        double current = branch.getCurrent();
//                        DecimalFormat df = circuitGraphic.getModule().getDecimalFormat();
//                        String amps = df.format( Math.abs( current ) );
//                        return current;
//                    }
//                }
//            }
//            return 0.0;
//        }
//    }

    public VoltageStripChart( PSwingCanvas pSwingCanvas, String title, IClock clock ) {
        super( pSwingCanvas, title, clock );
    }
}
