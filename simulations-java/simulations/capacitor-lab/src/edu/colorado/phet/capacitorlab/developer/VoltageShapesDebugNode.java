// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.developer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.capacitorlab.shapes.BatteryShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.VoltmeterShapeFactory;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the shapes related to the measurement of voltage.
 * All shapes are in the global view coordinate frame; when a model object moves, its shape changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltageShapesDebugNode extends PComposite {

    private static final boolean SHOW_BATTERY_BODY = false;

    private static final Stroke STROKE = new BasicStroke( 2f );
    private static final Color STROKE_COLOR = CLPaints.VOLTAGE_SHAPES;

    public VoltageShapesDebugNode( final DielectricModel model ) {

        // nothing interactive here
        setPickable( false );
        setChildrenPickable( false );

        // battery
        {
            final Battery battery = model.getBattery();
            final BatteryCapacitorCircuit circuit = model.getCircuit();
            final BatteryShapeFactory shapeFactory = battery.getShapeFactory();

            final PPath bodyNode = new PhetPPath( shapeFactory.createBodyShape(), STROKE, STROKE_COLOR );
            if ( SHOW_BATTERY_BODY ) {
                addChild( bodyNode );
            }
            bodyNode.setVisible( circuit.isBatteryConnected() );

            final PPath topTerminalNode = new PhetPPath( shapeFactory.createTopTerminalShape(), STROKE, STROKE_COLOR );
            addChild( topTerminalNode );
            topTerminalNode.setVisible( circuit.isBatteryConnected() );

            // set top terminal shape to match polarity
            battery.addPolarityObserver( new SimpleObserver() {
                public void update() {
                    topTerminalNode.setPathTo( shapeFactory.createTopTerminalShape() );
                }
            } );
            circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
                public void circuitChanged() {
                    bodyNode.setVisible( circuit.isBatteryConnected() );
                    topTerminalNode.setVisible( circuit.isBatteryConnected() );
                }
            } );
        }

        // capacitor
        {
            final Capacitor capacitor = model.getCapacitor();
            final CapacitorShapeFactory shapeFactory = capacitor.getShapeFactory();

            final PPath topPlateNode = new PhetPPath( shapeFactory.createTopPlateShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( topPlateNode );

            final PPath bottomPlateNode = new PhetPPath( shapeFactory.createBottomPlateShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( bottomPlateNode );

            // set plate shapes to match model
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    topPlateNode.setPathTo( shapeFactory.createTopPlateShapeOccluded() );
                    bottomPlateNode.setPathTo( shapeFactory.createBottomPlateShapeOccluded() );
                }
            };
            capacitor.addPlateSizeObserver( o );
            capacitor.addPlateSeparationObserver( o );
            capacitor.addDielectricOffsetObserver( o );
        }

        // wires
        {
            final BatteryCapacitorCircuit circuit = model.getCircuit();

            final Wire topWire = model.getTopWire();
            final PPath topWireNode = new PhetPPath( topWire.getShape(), STROKE, STROKE_COLOR );
            addChild( topWireNode );
            topWireNode.setVisible( circuit.isBatteryConnected() );

            final Wire bottomWire = model.getBottomWire();
            final PPath bottomWireNode = new PhetPPath( bottomWire.getShape(), STROKE, STROKE_COLOR );
            addChild( bottomWireNode );
            bottomWireNode.setVisible( circuit.isBatteryConnected() );

            topWire.addShapeObserver( new SimpleObserver() {
                public void update() {
                    topWireNode.setPathTo( topWire.getShape() );
                }
            } );

            bottomWire.addShapeObserver( new SimpleObserver() {
                public void update() {
                    bottomWireNode.setPathTo( bottomWire.getShape() );
                }
            } );

            circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
                public void circuitChanged() {
                    topWireNode.setVisible( circuit.isBatteryConnected() );
                    bottomWireNode.setVisible( circuit.isBatteryConnected() );
                }
            } );
        }

        // voltmeter
        {
            final Voltmeter voltmeter = model.getVoltmeter();
            final VoltmeterShapeFactory shapeFactory = voltmeter.getShapeFactory();

            final PPath positiveTipNode = new PhetPPath( shapeFactory.getPositiveProbeTipShape(), STROKE, STROKE_COLOR );
            addChild( positiveTipNode );

            final PPath negativeTipNode = new PhetPPath( shapeFactory.getNegativeProbeTipShape(), STROKE, STROKE_COLOR );
            addChild( negativeTipNode );

            // set shape to match positive probe location
            voltmeter.addPositiveProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    positiveTipNode.setPathTo( shapeFactory.getPositiveProbeTipShape() );
                }
            } );

            // set shape to match negative probe location
            voltmeter.addNegativeProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    negativeTipNode.setPathTo( shapeFactory.getNegativeProbeTipShape() );
                }
            } );

            // set visibility to match voltmeter visibility
            voltmeter.addVisibleObserver( new SimpleObserver() {
                public void update() {
                    positiveTipNode.setVisible( voltmeter.isVisible() );
                    negativeTipNode.setVisible( voltmeter.isVisible() );
                }
            } );
        }
    }

}
