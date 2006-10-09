package edu.colorado.phet.cck.phetgraphics_cck.chart;

import edu.colorado.phet.cck.phetgraphics_cck.circuit.CircuitGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.VoltageCalculation;
import edu.colorado.phet.cck.chart.DoubleTerminalFloatingChart;
import edu.colorado.phet.cck.chart.TwoTerminalValueReader;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 11:43:52 AM
 * Copyright (c) Jun 22, 2006 by Sam Reid
 */

public class PhetgraphicsVoltageStripChart extends DoubleTerminalFloatingChart.Phetgraphics {
    private CircuitGraphic circuitGraphic;

    public PhetgraphicsVoltageStripChart( PhetPCanvas pSwingCanvas, String title, IClock clock, CircuitGraphic circuitGraphic ) {
        super( pSwingCanvas, title, new TwoTerminalValueReader() {
            public double getValue( Shape a, Shape b ) {
                return 0;
            }
        }, clock );
        this.circuitGraphic = circuitGraphic;
        super.setValueReader( new VoltageReader( circuitGraphic ) );
        getStripChartJFCNode().setVerticalRange( -20, 20 );
        getStripChartJFCNode().getXYPlot().getRangeAxis().setLabel( "Voltage (V)" );
        getLeftCrosshairGraphic().setColor( Color.red );
        getRightCrosshairGraphic().setColor( Color.black );
    }

    public class VoltageReader implements TwoTerminalValueReader {
        private CircuitGraphic circuitGraphic;

        public VoltageReader( CircuitGraphic circuitGraphic ) {
            this.circuitGraphic = circuitGraphic;
        }

        public double getValue( Shape a, Shape b ) {
            return new VoltageCalculation( circuitGraphic.getCircuit() ).getVoltage( circuitGraphic, a, b );
        }
    }
}
