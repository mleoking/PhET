/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeListener;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial.CustomDielectricChangeListener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Developer node for displaying various model values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevModelDisplayNode extends PhetPNode {
    
    private final CLModel model;
    private final ValueNode voltageNode;
    private final ValueNode plateSize;
    private final ValueNode plateAreaNode, dielectricInsideAreaNode;
    private final ValueNode plateSeparationNode;
    private final ValueNode capacitanceNode;
    private final ValueNode plateChargeNode, excessPlateChargeNode;
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
        
        voltageNode = new ValueNode( "V (voltage)", "V" );
        addChild( voltageNode );
        plateSize = new ValueNode( "L (plate side length)", "m" );
        addChild( plateSize );
        plateSeparationNode = new ValueNode( "d (plate separation)", "m" );
        addChild( plateSeparationNode );
        addChild( new PText( "----------" ) );
        plateAreaNode = new ValueNode( "A (plate area)", "m<sup>2</sup>" );
        addChild( plateAreaNode );
        dielectricInsideAreaNode = new ValueNode( "Ad (dielectric area inside)", "m<sup>2</sup>" );
        addChild( dielectricInsideAreaNode );
        addChild( new PText( "----------" ) );
        capacitanceNode = new ValueNode( "C (capacitance)", "F" );
        addChild( capacitanceNode );
        plateChargeNode = new ValueNode( "Q (plate charge)", "C" );
        addChild( plateChargeNode );
        excessPlateChargeNode = new ValueNode( "Q<sub>excess</sub> (excess plate charge)", "C" );
        addChild( excessPlateChargeNode );
        effectiveFieldNode = new ValueNode( "E<sub>effective</sub>", "V/m" );
        addChild( effectiveFieldNode );
        plateFieldNode = new ValueNode( "E<sub>plate</sub>", "V/m" );
        addChild( plateFieldNode );
        dielectricFieldNode = new ValueNode( "E<sub>dielectric</sub>", "V/m" );
        addChild( dielectricFieldNode );
        energyStoredNode = new ValueNode( "U (energy stored)", "J" );
        addChild( energyStoredNode );
        
        updateDielectricListener();
        updateValues();
    }
    
    private void updateValues() {
        
        // set values
        voltageNode.setValue( model.getBattery().getVoltage() );
        plateSize.setValue( model.getCapacitor().getPlateSize() );
        plateAreaNode.setValue( model.getCapacitor().getPlateArea() );
        dielectricInsideAreaNode.setValue( model.getCapacitor().getDielectricInsideArea() );
        plateSeparationNode.setValue( model.getCapacitor().getPlateSeparation() );
        capacitanceNode.setValue( model.getCircuit().getCapacitance() );
        plateChargeNode.setValue( model.getCircuit().getPlateCharge() );
        excessPlateChargeNode.setValue( model.getCircuit().getExcessPlateCharge() );
        effectiveFieldNode.setValue( model.getCircuit().getEffectiveEfield() );
        plateFieldNode.setValue( model.getCircuit().getPlatesEField() );
        dielectricFieldNode.setValue( model.getCircuit().getDielectricEField() );
        energyStoredNode.setValue( model.getCircuit().getEnergyStored() );
        
        // layout
        layoutColumnLeftAlign();
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
    
    /*
     * Layouts out children of this node in one column, left aligned.
     * They will appear in the order that they were added as children.
     */
    private void layoutColumnLeftAlign() {
        double ySpacing = 2;
        double x = 0;
        double y = 0;
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            if ( i == 0 ) {
                getChild( i ).setOffset( x, y );
            }
            else {
                y = getChild( i - 1 ).getFullBoundsReference().getMaxY() + ySpacing;
                getChild( i ).setOffset( x, y );
            }
        }
    }
    
    private static class ValueNode extends HTMLNode {
        
        private final String label;
        private final String units;
        
        public ValueNode( String label, String units ) {
            this( label, units, 0 );
            setFont( new PhetFont() );
        }
        
        public ValueNode( String label, String units, double value ) {
            this.label = label;
            this.units = units;
            setValue( value );
        }
        
        public void setValue( double value ) {
            setHTML( label + " = " + value + " " + units );
        }
    }
}
