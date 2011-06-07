// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.util.HashMap;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugNode;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.capacitorlab.view.MultipleCapacitorsCircuitNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.EFieldDetectorView;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterView;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Canvas for the "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsCanvas extends CLCanvas {

    // global view properties, directly observable
    public final Property<Boolean> plateChargesVisibleProperty = new Property<Boolean>( CLConstants.PLATE_CHARGES_VISIBLE );
    public final Property<Boolean> eFieldVisibleProperty = new Property<Boolean>( CLConstants.EFIELD_VISIBLE );
    public final Property<DielectricChargeView> dielectricChargeViewProperty = new Property<DielectricChargeView>( CLConstants.DIELECTRIC_CHARGE_VIEW );

    private final CLGlobalProperties globalProperties;
    private final MultipleCapacitorsModel model;

    private final PNode circuitParentNode; // parent of all circuit nodes, so we don't have to mess with rendering order
    private final HashMap<ICircuit, PNode> circuitToNodeMap;

    // meters
    private final CapacitanceMeterNode capacitanceMeterNode;
    private final PlateChargeMeterNode plateChargeMeterNode;
    private final StoredEnergyMeterNode storedEnergyMeterNode;
    private final VoltmeterView voltmeter;
    private final EFieldDetectorView eFieldDetector;

    // debug
    private final PNode shapesDebugParentNode;

    public MultipleCapacitorsCanvas( final MultipleCapacitorsModel model, final CLModelViewTransform3D mvt, CLGlobalProperties globalProperties ) {
        super( model, mvt );

        this.model = model;
        this.globalProperties = globalProperties;

        /*
         * Maximums, for calibrating various view representations.
         * Note that for charge and fields, we get these values from the DielectricModel,
         * so that density of charge and field will be the same across all modules.
         */
        final double maxPlateCharge = DielectricModel.getMaxPlateCharge();
        final double maxExcessDielectricPlateCharge = DielectricModel.getMaxExcessDielectricPlateCharge();
        final double maxEffectiveEField = DielectricModel.getMaxEffectiveEField();
        final double maxDielectricEField = DielectricModel.getMaxDielectricEField();
        final double eFieldReferenceMagnitude = MultipleCapacitorsModel.getEFieldReferenceMagnitude();

        // One node for each circuit. These persist so we don't have to do cleanup when the current circuit changes.
        circuitParentNode = new PNode();
        circuitToNodeMap = new HashMap<ICircuit, PNode>();
        for ( ICircuit circuit : model.getCircuits() ) {
            PNode node = new MultipleCapacitorsCircuitNode( circuit, mvt, false /* dielectricVisible */,
                                                            plateChargesVisibleProperty, eFieldVisibleProperty, dielectricChargeViewProperty,
                                                            maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
            node.setVisible( false );
            circuitParentNode.addChild( node );
            circuitToNodeMap.put( circuit, node );
        }

        // meters
        capacitanceMeterNode = new CapacitanceMeterNode( model.getCapacitanceMeter(), mvt, CLStrings.TOTAL_CAPACITANCE );
        plateChargeMeterNode = new PlateChargeMeterNode( model.getPlateChargeMeter(), mvt, CLStrings.STORED_CHARGE );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.getStoredEnergyMeter(), mvt, CLStrings.STORED_ENERGY );
        voltmeter = new VoltmeterView( model.getVoltmeter(), mvt );
        eFieldDetector = new EFieldDetectorView( model.getEFieldDetector(), mvt, eFieldReferenceMagnitude, globalProperties.dev, true /* eFieldDetectorSimplified */ );

        // debug
        shapesDebugParentNode = new PComposite();

        // rendering order
        addChild( circuitParentNode );
        addChild( capacitanceMeterNode );
        addChild( plateChargeMeterNode );
        addChild( storedEnergyMeterNode );
        addChild( eFieldDetector.getBodyNode() );
        addChild( eFieldDetector.getWireNode() );
        addChild( eFieldDetector.getProbeNode() );
        addChild( voltmeter.getBodyNode() );
        addChild( voltmeter.getPositiveProbeNode() );
        addChild( voltmeter.getPositiveWireNode() );
        addChild( voltmeter.getNegativeProbeNode() );
        addChild( voltmeter.getNegativeWireNode() );
        addChild( shapesDebugParentNode );

        // When the current circuit changes, make the proper circuit node visible.
        model.currentCircuitProperty.addObserver( new SimpleObserver() {
            public void update() {
                int numberOfChildren = circuitParentNode.getChildrenCount();
                for ( int i = 0; i < numberOfChildren; i++ ) {
                    circuitParentNode.getChild( i ).setVisible( false );
                }
                circuitToNodeMap.get( model.currentCircuitProperty.get() ).setVisible( true );
                updateShapesDebugNodes();
            }
        } );

        // change visibility of debug shapes
        SimpleObserver shapesVisibilityObserver = new SimpleObserver() {
            public void update() {
                updateShapesDebugNodes();
            }
        };
        globalProperties.eFieldShapesVisibleProperty.addObserver( shapesVisibilityObserver );
        globalProperties.voltageShapesVisibleProperty.addObserver( shapesVisibilityObserver );
    }

    public void reset() {
        // global properties of the view
        plateChargesVisibleProperty.reset();
        eFieldVisibleProperty.reset();
        dielectricChargeViewProperty.reset();
        // zoom level of bar meters
        capacitanceMeterNode.reset();
        plateChargeMeterNode.reset();
        storedEnergyMeterNode.reset();
    }

    private void updateShapesDebugNodes() {
        shapesDebugParentNode.removeAllChildren();

        PNode voltageShapesDebugNode = new VoltageShapesDebugNode( model.currentCircuitProperty.get(), model.getVoltmeter() );
        shapesDebugParentNode.addChild( voltageShapesDebugNode );
        voltageShapesDebugNode.setVisible( globalProperties.voltageShapesVisibleProperty.get() );

        PNode eFieldShapesDebugNode = new EFieldShapesDebugNode( model.currentCircuitProperty.get() );
        shapesDebugParentNode.addChild( eFieldShapesDebugNode );
        eFieldShapesDebugNode.setVisible( globalProperties.eFieldShapesVisibleProperty.get() );
    }
}
