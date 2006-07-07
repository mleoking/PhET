package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.chart.DoubleTerminalFloatingChart;
import edu.colorado.phet.cck3.chart.TwoTerminalValueReader;
import edu.colorado.phet.cck3.circuit.CircuitGraphic;
import edu.colorado.phet.common.model.clock.IClock;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 11:43:52 AM
 * Copyright (c) Jun 22, 2006 by Sam Reid
 */

public class VoltageStripChart extends DoubleTerminalFloatingChart {
    private CircuitGraphic circuitGraphic;

    public VoltageStripChart( PSwingCanvas pSwingCanvas, String title, IClock clock, CircuitGraphic circuitGraphic ) {
        super( pSwingCanvas, title, new TwoTerminalValueReader() {
            public double getValue( double x, double y, double x1, double y1 ) {
                return 0;
            }
        }, clock );
        this.circuitGraphic = circuitGraphic;
        super.setValueReader( new VoltageReader() );
    }

    public class VoltageReader implements TwoTerminalValueReader {
//        public double getValue( double x, double y ) {
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

        public double getValue( double x, double y, double x1, double y1 ) {
            return 0;
        }
    }
}
