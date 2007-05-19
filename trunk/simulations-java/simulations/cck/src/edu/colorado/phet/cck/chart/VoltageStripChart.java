package edu.colorado.phet.cck.chart;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.piccolo_cck.CCKSimulationPanel;
import edu.colorado.phet.cck.piccolo_cck.PiccoloVoltageCalculation;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 11:43:52 AM
 */

public class VoltageStripChart extends DoubleTerminalFloatingChart.Piccolo {

    public VoltageStripChart( CCKSimulationPanel pSwingCanvas, String title, IClock clock, Circuit circuit ) {
        super( pSwingCanvas, title, new TwoTerminalValueReader() {
            public double getValue( Shape a, Shape b ) {
                return 0;
            }
        }, clock );
        super.setValueReader( new VoltageStripChart.VoltageReader( circuit ) );
        getStripChartJFCNode().setVerticalRange( -20, 20 );
        getStripChartJFCNode().getXYPlot().getRangeAxis().setLabel( "Voltage (V)" );
        getLeftCrosshairGraphic().setColor( Color.red );
        getRightCrosshairGraphic().setColor( Color.black );
    }

    public class VoltageReader implements TwoTerminalValueReader {
        private Circuit circuit;

        public VoltageReader( Circuit circuit ) {
            this.circuit = circuit;
        }

        public double getValue( Shape a, Shape b ) {
            return new PiccoloVoltageCalculation( circuit ).getVoltage( a, b );
        }
    }
}
