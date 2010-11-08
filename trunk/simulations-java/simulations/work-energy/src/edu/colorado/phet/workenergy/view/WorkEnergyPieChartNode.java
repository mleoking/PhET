package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class WorkEnergyPieChartNode extends PNode {
    final PieChartNode pieChartNode = new PieChartNode( new PieChartNode.PieValue[0], new Rectangle( 0, 0, 1, 1 ) );
    Function.LinearFunction energyToDiameter = new Function.LinearFunction( 0, 1000, 0, 100 );

    public WorkEnergyPieChartNode( final Property<Boolean> visibleProperty, final WorkEnergyObject object, final ModelViewTransform2D transform ) {
        addChild( new PText( "Pie chart" ) );
        visibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visibleProperty.getValue() );
            }
        } );
        final SimpleObserver updatePieChartLocation = new SimpleObserver() {
            public void update() {
                Point2D viewLocation = transform.modelToViewDouble( object.getTopCenter() );
                setOffset( viewLocation.getX(), viewLocation.getY() - getPieDiameter( object ) / 2 );
            }
        };
        object.getPositionProperty().addObserver( updatePieChartLocation );
        object.getTotalEnergyProperty().addObserver( updatePieChartLocation );

        object.getTotalEnergyProperty().addObserver( new SimpleObserver() {
            public void update() {
                //Pie chart should be proportionate in size to total energy
                int diameter = getPieDiameter( object );
                pieChartNode.setArea( new Rectangle( -diameter / 2, -diameter / 2, diameter, diameter ) );
            }
        } );
        final SimpleObserver updatePieSlices = new SimpleObserver() {
            public void update() {
                pieChartNode.setPieValues( new PieChartNode.PieValue[] { new PieChartNode.PieValue( object.getKineticEnergyProperty().getValue(), PhetColorScheme.KINETIC_ENERGY ),
                        new PieChartNode.PieValue( object.getPotentialEnergyProperty().getValue(), PhetColorScheme.POTENTIAL_ENERGY ),
                        new PieChartNode.PieValue( object.getThermalEnergyProperty().getValue(), PhetColorScheme.HEAT_THERMAL_ENERGY ) } );
            }
        };
        object.getKineticEnergyProperty().addObserver( updatePieSlices );
        object.getPotentialEnergyProperty().addObserver( updatePieSlices );
        object.getThermalEnergyProperty().addObserver( updatePieSlices );

        addChild( pieChartNode );
    }

    private int getPieDiameter( WorkEnergyObject object ) {
        double e = object.getTotalEnergy();
        return (int) energyToDiameter.evaluate( e );
    }
}
