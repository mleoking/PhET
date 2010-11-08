package edu.colorado.phet.workenergy.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.piccolophet.nodes.barchart.BarChartNode;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class BarGraphNode extends PNode {
    public BarGraphNode( final Property<Boolean> visibleProperty, final WorkEnergyObject object ) {
        visibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visibleProperty.getValue() );
            }
        } );

        BarChartNode barGraph = new BarChartNode( "Energy (J)", 0.3, Color.white );
        barGraph.init( new BarChartNode.Variable[] {
                new WorkEnergyBarValue( "Kinetic", object.getKineticEnergyProperty(), PhetColorScheme.KINETIC_ENERGY, barGraph ),
                new WorkEnergyBarValue( "Potential", object.getPotentialEnergyProperty(), PhetColorScheme.POTENTIAL_ENERGY, barGraph ),
                new WorkEnergyBarValue( "Thermal", object.getThermalEnergyProperty(), PhetColorScheme.HEAT_THERMAL_ENERGY, barGraph ),
                new WorkEnergyBarValue( "Total", object.getTotalEnergyProperty(), PhetColorScheme.TOTAL_ENERGY, barGraph )
        } );
        addChild( barGraph );
    }

    public static class WorkEnergyBarValue extends BarChartNode.Variable {
        public WorkEnergyBarValue( String name, final Property<Double> value, Color color, final BarChartNode parent ) {
            super( name, value.getValue(), color );
            value.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( value.getValue() );
                    System.out.println( "value.getValue() = " + value.getValue() );
                    parent.update();
                }
            } );
        }
    }
}
