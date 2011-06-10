// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.developer;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.meter.Voltmeter;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.shapes.BatteryShapeCreator;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeCreator;
import edu.colorado.phet.capacitorlab.shapes.VoltmeterShapeCreator;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the shapes related to the measurement of voltage.
 * All shapes are in the global view coordinate frame; when a model object moves, its shape changes.
 * This is a developer feature, not intended to be visible to the user.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltageShapesDebugNode extends PComposite {

    private static final boolean SHOW_BATTERY_BODY = false;

    private static final Stroke STROKE = new BasicStroke( 2f );
    private static final Color STROKE_COLOR = CLPaints.VOLTAGE_DEBUG_SHAPES;

    public VoltageShapesDebugNode( final ICircuit circuit, final Voltmeter voltmeter ) {

        // nothing interactive here
        setPickable( false );
        setChildrenPickable( false );

        // battery
        {
            final Battery battery = circuit.getBattery();
            final BatteryShapeCreator shapeCreator = battery.getShapeCreator();

            final PPath bodyNode = new PhetPPath( shapeCreator.createBodyShape(), STROKE, STROKE_COLOR );
            if ( SHOW_BATTERY_BODY ) {
                addChild( bodyNode );
            }
            bodyNode.setVisible( circuit.isBatteryConnected() );

            final PPath topTerminalNode = new PhetPPath( shapeCreator.createTopTerminalShape(), STROKE, STROKE_COLOR );
            addChild( topTerminalNode );
            topTerminalNode.setVisible( circuit.isBatteryConnected() );

            // set top terminal shape to match polarity
            battery.addPolarityObserver( new SimpleObserver() {
                public void update() {
                    topTerminalNode.setPathTo( shapeCreator.createTopTerminalShape() );
                }
            } );
            circuit.addCircuitChangeListener( new CircuitChangeListener() {
                public void circuitChanged() {
                    bodyNode.setVisible( circuit.isBatteryConnected() );
                    topTerminalNode.setVisible( circuit.isBatteryConnected() );
                }
            } );
        }

        // capacitors
        for ( Capacitor capacitor : circuit.getCapacitors() ) {
            final CapacitorShapeCreator shapeCreator = capacitor.getShapeCreator();
            final PPath topPlateNode = new PhetPPath( shapeCreator.createTopPlateShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( topPlateNode );

            final PPath bottomPlateNode = new PhetPPath( shapeCreator.createBottomPlateShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( bottomPlateNode );

            // set plate shapes to match model
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    topPlateNode.setPathTo( shapeCreator.createTopPlateShapeOccluded() );
                    bottomPlateNode.setPathTo( shapeCreator.createBottomPlateShapeOccluded() );
                }
            };
            capacitor.addPlateSizeObserver( o );
            capacitor.addPlateSeparationObserver( o );
            capacitor.addDielectricOffsetObserver( o );
        }

        // wires
        ArrayList<Wire> wires = circuit.getWires();
        for ( int i = 0; i < wires.size(); i++ ) {

            final Wire wire = wires.get( i );

            final PPath wireNode = new PhetPPath( wire.getShape(), STROKE, STROKE_COLOR );
            addChild( wireNode );
            wireNode.setVisible( circuit.isBatteryConnected() );

            wire.addShapeObserver( new SimpleObserver() {
                public void update() {
                    wireNode.setPathTo( wire.getShape() );
                }
            } );

            if ( i == 0 || i == wires.size() - 1 ) {
                circuit.addCircuitChangeListener( new CircuitChangeListener() {
                    public void circuitChanged() {
                        wireNode.setVisible( circuit.isBatteryConnected() );
                    }
                } );
            }
        }

        // voltmeter
        {
            final VoltmeterShapeCreator shapeCreator = voltmeter.getShapeCreator();

            final PPath positiveTipNode = new PhetPPath( shapeCreator.getPositiveProbeTipShape(), STROKE, STROKE_COLOR );
            addChild( positiveTipNode );

            final PPath negativeTipNode = new PhetPPath( shapeCreator.getNegativeProbeTipShape(), STROKE, STROKE_COLOR );
            addChild( negativeTipNode );

            // set shape to match positive probe location
            voltmeter.positiveProbeLocationProperty.addObserver( new SimpleObserver() {
                public void update() {
                    positiveTipNode.setPathTo( shapeCreator.getPositiveProbeTipShape() );
                }
            } );

            // set shape to match negative probe location
            voltmeter.negativeProbeLocationProperty.addObserver( new SimpleObserver() {
                public void update() {
                    negativeTipNode.setPathTo( shapeCreator.getNegativeProbeTipShape() );
                }
            } );

            // set visibility to match voltmeter visibility
            voltmeter.visibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    positiveTipNode.setVisible( voltmeter.isVisible() );
                    negativeTipNode.setVisible( voltmeter.isVisible() );
                }
            } );
        }
    }
}
