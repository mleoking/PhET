// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.chart;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.CCKSimulationPanel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 11:43:52 AM
 */

public class CurrentStripChart extends SingleTerminalFloatingChart.Piccolo {
    private Circuit circuit;

    public CurrentStripChart(PhetPCanvas pSwingCanvas, String title, IClock clock, Circuit circuit, CCKSimulationPanel cckSimulationPanel) {
        super(pSwingCanvas, title, new ValueReader() {
            public double getValue(double x, double y) {
                return 0;
            }
        }, clock, cckSimulationPanel);
        this.circuit = circuit;
        super.setValueReader(new CurrentStripChart.CurrentReader());
    }

    public class CurrentReader implements ValueReader {
        public double getValue(double x, double y) {
            Point2D target = new Point2D.Double(x, y);
            Branch[] branches = circuit.getBranches();
            for (int i = 0; i < branches.length; i++) {
                Branch branch = branches[i];
                Shape shape = branch.getShape();
                if (shape.contains(target)) {
                    return branch.getCurrent();
                }
            }
            return Double.NaN;
        }
    }

}
