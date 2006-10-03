package edu.colorado.phet.cck.chart;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 11:43:52 AM
 * Copyright (c) Jun 22, 2006 by Sam Reid
 */

public class PiccoloCurrentStripChart extends SingleTerminalFloatingChart.Piccolo {
    private Circuit circuit;

    public PiccoloCurrentStripChart( PhetPCanvas pSwingCanvas, String title, IClock clock, Circuit circuit ) {
        super( pSwingCanvas, title, new ValueReader() {
            public double getValue( double x, double y ) {
                return 0;
            }
        }, clock );
        this.circuit = circuit;
        super.setValueReader( new PiccoloCurrentStripChart.CurrentReader() );
    }

    public class CurrentReader implements ValueReader {
        public double getValue( double x, double y ) {
            Point2D target = new Point2D.Double( x, y );
            Branch[]branches = circuit.getBranches();
            for( int i = 0; i < branches.length; i++ ) {
                Branch branch = branches[i];
                Shape shape = branch.getShape();
                if( shape.contains( target ) ) {
                    return branch.getCurrent();
                }
            }
            return Double.NaN;
        }
    }

}
