/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeListener;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial.CustomDielectricChangeListener;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Developer node for displaying various model values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevModelDisplayNode extends PhetPNode {
    
    private final CLModel model;
    private final ValueNode capacitanceNode;
    private final ValueNode plateChargeNode;
    private final ValueNode effectiveFieldNode, plateFieldNode, dielectricFieldNode;
    private final ValueNode energyStoredNode;

    private CustomDielectricMaterial customDielectric;
    private CustomDielectricChangeListener customDielectricChangeListener;

    public DevModelDisplayNode( CLModel model ) {
        
        this.model = model;
        model.getCapacitor().addCapacitorChangeListener( new CapacitorChangeListener() {

            public void dielectricMaterialChanged() {
                updateDielectricListener();
                updateValues();
            }

            public void dielectricOffsetChanged() {
                updateValues();
            }

            public void plateSeparationChanged() {
                updateValues();
            }

            public void plateSizeChanged() {
                updateValues();
            }
        });
        model.getBattery().addBatteryChangeListener( new BatteryChangeListener() {

            public void connectedChanged() {
                updateValues();
            }

            public void polarityChanged() {
                updateValues();
            }

            public void voltageChanged() {
                updateValues();
            }
        });
        
        customDielectricChangeListener = new CustomDielectricChangeListener() {
            public void dielectricConstantChanged() {
                updateValues();
            }
        };
        
        capacitanceNode = new ValueNode( "capacitance", "F" );
        addChild( capacitanceNode );
        plateChargeNode = new ValueNode( "plate charge", "C" );
        addChild( plateChargeNode );
        effectiveFieldNode = new ValueNode( "E-effective", "V/m" );
        addChild( effectiveFieldNode );
        plateFieldNode = new ValueNode( "E-plate", "V/m" );
        addChild( plateFieldNode );
        dielectricFieldNode = new ValueNode( "E-dielectric", "V/m" );
        addChild( dielectricFieldNode );
        energyStoredNode = new ValueNode( "energy stored", "J" );
        addChild( energyStoredNode );
        
        updateDielectricListener();
        updateValues();
    }
    
    private void updateValues() {
        
        // set values
        capacitanceNode.setValue( model.getCircuit().getCapacitance() );
        plateChargeNode.setValue( model.getCircuit().getPlateCharge() );
        effectiveFieldNode.setValue( model.getCircuit().getEffectiveEfield() );
        plateFieldNode.setValue( model.getCircuit().getPlatesEField() );
        dielectricFieldNode.setValue( model.getCircuit().getDielectricEField() );
        energyStoredNode.setValue( model.getCircuit().getEnergyStored() );
        
        // layout
        double ySpacing = 2;
        double x = 0;
        double y = 0;
        capacitanceNode.setOffset( x, y );
        y = capacitanceNode.getFullBoundsReference().getMaxY() + ySpacing;
        plateChargeNode.setOffset( x, y );
        y = plateChargeNode.getFullBoundsReference().getMaxY() + ySpacing;
        effectiveFieldNode.setOffset( x, y );
        y = effectiveFieldNode.getFullBoundsReference().getMaxY() + ySpacing;
        plateFieldNode.setOffset( x, y );
        y = plateFieldNode.getFullBoundsReference().getMaxY() + ySpacing;
        dielectricFieldNode.setOffset( x, y );
        y = dielectricFieldNode.getFullBoundsReference().getMaxY() + ySpacing;
        energyStoredNode.setOffset( x, y );
    }
    
    private void updateDielectricListener() {
        if ( customDielectric != null ) {
            customDielectric.removeCustomDielectricChangeListener( customDielectricChangeListener );
            customDielectric = null;
        }
        DielectricMaterial material = model.getCapacitor().getDielectricMaterial();
        if ( material instanceof CustomDielectricMaterial ) {
            customDielectric = (CustomDielectricMaterial) material;
            customDielectric.addCustomDielectricChangeListener( customDielectricChangeListener );
        }
    }
    
    private static class ValueNode extends PText {
        
        private final String label;
        private final String units;
        
        public ValueNode( String label, String units ) {
            this( label, units, 0 );
        }
        
        public ValueNode( String label, String units, double value ) {
            this.label = label;
            this.units = units;
            setValue( value );
        }
        
        public void setValue( double value ) {
            setText( label + " = " + value + " " + units );
        }
    }
}
