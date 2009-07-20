package edu.colorado.phet.circuitconstructionkit.view.chart;

import java.awt.*;

import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.CCKSimulationPanel;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.PiccoloVoltageCalculation;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

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
        getStripChartJFCNode().getXYPlot().getRangeAxis().setLabel( CCKStrings.getString( "voltage-y-axis" ) );
        getLeftCrosshairGraphic().setColor( Color.red );
        getRightCrosshairGraphic().setColor( Color.black );
    }

    public class VoltageReader implements TwoTerminalValueReader {
        private Circuit circuit;

        public VoltageReader( Circuit circuit ) {
            this.circuit = circuit;
        }

        public double getValue( Shape a, Shape b ) {
            return circuit.getVoltage( a, b );
        }
    }
}
