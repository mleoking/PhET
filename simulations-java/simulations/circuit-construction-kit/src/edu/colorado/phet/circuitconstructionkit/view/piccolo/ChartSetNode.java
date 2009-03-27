package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.view.chart.AbstractFloatingChart;
import edu.colorado.phet.circuitconstructionkit.view.chart.CurrentStripChart;
import edu.colorado.phet.circuitconstructionkit.view.chart.VoltageStripChart;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * User: Sam Reid
 * Date: Oct 8, 2006
 * Time: 9:20:59 PM
 */

public class ChartSetNode extends PhetPNode {
    //    private SwingClock stripChartClock = new SwingClock( 30, 1 );
    private CCKSimulationPanel cckSimulationPanel;
    private Circuit circuit;
    private IClock clock;

    public ChartSetNode( CCKSimulationPanel cckSimulationPanel, Circuit circuit, IClock clock ) {
        this.cckSimulationPanel = cckSimulationPanel;
        this.circuit = circuit;
        this.clock = clock;
//        stripChartClock.start();
    }

    public void addCurrentChart() {
        final CurrentStripChart chart = new CurrentStripChart( cckSimulationPanel, CCKStrings.getString( "current-y-axis" ), clock, getCircuit(), cckSimulationPanel );
        chart.translate( 3 * 70, 3 * 70 );

        addChild( chart );
        chart.addListener( new AbstractFloatingChart.Listener() {
            public void chartClosing() {
                removeChild( chart );
            }
        } );
    }

    private Circuit getCircuit() {
        return circuit;
    }

    public void addVoltageChart() {
        final VoltageStripChart chart = new VoltageStripChart( cckSimulationPanel, "Current", clock, getCircuit() );
        chart.translate( 4 * 70, 4 * 70 );

        addChild( chart );
        chart.addListener( new AbstractFloatingChart.Listener() {
            public void chartClosing() {
                removeChild( chart );
            }
        } );
    }
}
