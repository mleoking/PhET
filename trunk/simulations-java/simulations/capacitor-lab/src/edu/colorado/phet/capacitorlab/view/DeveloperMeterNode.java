/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial.CustomDielectricChangeListener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Developer "meter" for displaying various model values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMeterNode extends PhetPNode {
    
    private static final Color BACKGROUND_FILL_COLOR = new Color( 255, 255, 255, 125 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final double BACKGROUND_MARGIN = 5;
    
    private final CLModel model;
    
    private final PComposite parentNode;
    
    // user settings
    private final ValueNode batteryVoltageNode;
    private final ValueNode manualPlateChargeNode;
    private final ValueNode plateSideLengthNode;
    private final ValueNode plateSeparationNode;
    private final ValueNode dielectricConstantNode;
    
    // derived values
    private final ValueNode dielectricContactAreaNode, airContactAreaNode, plateAreaNode;
    private final ValueNode airCapacitanceNode, dielectricCapacitanceNode, totalCapacitanceNode;
    private final ValueNode plateVoltageNode;
    private final ValueNode airCharge, dielectricCharge, totalCharge, excessAirCharge, excessDielectricCharge;
    private final ValueNode airSurfaceChargeDensity, dielectricSurfaceChargeDensity;
    private final ValueNode eEffective, ePlates, eAir, eDielectric;
    private final ValueNode energyStoredNode;
    
    private final Rectangle2D backgroundRect;
    private final PPath backgroundPath;
    private double maxBackgroundWidth; // this minimizes distracting growing/shrinking of the background
    
    private final PImage closeButton;

    private CustomDielectricMaterial customDielectric;
    private CustomDielectricChangeListener customDielectricChangeListener;

    public DeveloperMeterNode( CLModel model, PNode dragBoundsNode ) {
        
        this.model = model;
        model.getCircuit().addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
            
            public void batteryConnectedChanged() {
                updateValues();
            }
            
            public void chargeChanged() {
                updateValues();
            }
            
            public void capacitanceChanged() {
                updateValues();
            }
            
            public void efieldChanged() {
                updateValues();
            }
            
            public void energyChanged() {
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
        
        // background
        maxBackgroundWidth = 0;
        backgroundRect = new Rectangle2D.Double();
        backgroundPath = new PPath( backgroundRect );
        backgroundPath.setPaint( BACKGROUND_FILL_COLOR );
        backgroundPath.setStrokePaint( BACKGROUND_STROKE_COLOR );
        backgroundPath.setStroke( BACKGROUND_STROKE );
        addChild( backgroundPath );
        
        // fields
        parentNode = new PComposite();
        addChild( parentNode );
        PText titleNode = new PText( "Developer Meter" );
        titleNode.setFont( new PhetFont( Font.BOLD, 14 ) );
        titleNode.setTextPaint( Color.RED );
        parentNode.addChild( titleNode );
        
        parentNode.addChild( new PText( " " ) );
        parentNode.addChild( new PText( "Constants ....................................................." ) );
        ValueNode epsilon0 = new ValueNode( CLStrings.EPSILON + "0", "F/m", "0.000E00" );
        epsilon0.setValue( CLConstants.EPSILON_0 );
        parentNode.addChild( epsilon0 );
        ValueNode epsilonAir = new ValueNode( CLStrings.EPSILON + "_air", "", "0.00000000" );
        epsilonAir.setValue( CLConstants.EPSILON_AIR );
        parentNode.addChild( epsilonAir );
        ValueNode epsilonVacuum = new ValueNode( CLStrings.EPSILON + "_vacuum", "", "0.0" );
        epsilonVacuum.setValue( CLConstants.EPSILON_VACUUM );
        parentNode.addChild( epsilonVacuum );
        
        parentNode.addChild( new PText( " " ) );
        parentNode.addChild( new PText( "User Settings ....................................................." ) );
        batteryVoltageNode = new ValueNode( "V_battery", "V", "0.00" );
        parentNode.addChild( batteryVoltageNode );
        manualPlateChargeNode = new ValueNode( "Q_disconnected", "Coulombs", "0.000E00" );
        parentNode.addChild( manualPlateChargeNode );
        plateSideLengthNode = new ValueNode( "L (plate side)", "m", "0.0000" );
        parentNode.addChild( plateSideLengthNode );
        plateSeparationNode = new ValueNode( "d (plate separation)", "m", "0.0000" );
        parentNode.addChild( plateSeparationNode );
        dielectricConstantNode = new ValueNode( CLStrings.EPSILON + "r", "", "0.000" );
        parentNode.addChild( dielectricConstantNode );
        
        parentNode.addChild( new PText( " " ) );
        parentNode.addChild( new PText( "Derived ....................................................." ) );
        // area
        dielectricContactAreaNode = new ValueNode( "A_dielectric", "m^2", "0.000000" );
        parentNode.addChild( dielectricContactAreaNode );
        airContactAreaNode = new ValueNode( "A_air", "m^2", "0.000000" );
        parentNode.addChild( airContactAreaNode );
        plateAreaNode = new ValueNode( "A_plate", "m^2", "0.000000" );
        parentNode.addChild( plateAreaNode );
        // capacitance
        airCapacitanceNode = new ValueNode( "C_air", "F", "0.000E00" );
        parentNode.addChild( airCapacitanceNode );
        dielectricCapacitanceNode = new ValueNode( "C_dielectric", "F", "0.000E00" );
        parentNode.addChild( dielectricCapacitanceNode );
        totalCapacitanceNode = new ValueNode( "C_total", "F", "0.000E00" );
        parentNode.addChild( totalCapacitanceNode );
        // voltage
        plateVoltageNode = new ValueNode( "V_plate", "V", "0.00" );
        parentNode.addChild( plateVoltageNode );
        // charge
        airCharge = new ValueNode( "Q_air", "C", "0.000E00" );
        parentNode.addChild( airCharge );
        dielectricCharge = new ValueNode( "Q_dielectric", "C", "0.000E00" );
        parentNode.addChild( dielectricCharge );
        totalCharge = new ValueNode( "Q_total", "C", "0.000E00" );
        parentNode.addChild( totalCharge );
        excessAirCharge = new ValueNode( "Q_excess_air", "C", "0.000E00" );
        parentNode.addChild( excessAirCharge );
        excessDielectricCharge = new ValueNode( "Q_excess_dielectric", "C", "0.000E00" );
        parentNode.addChild( excessDielectricCharge );
        // surface charge density
        airSurfaceChargeDensity = new ValueNode( CLStrings.SIGMA + "_air", "C/m^2", "0.000E00" );
        parentNode.addChild( airSurfaceChargeDensity );
        dielectricSurfaceChargeDensity = new ValueNode( CLStrings.SIGMA + "_dielectric", "C/m^2", "0.000E00" );
        parentNode.addChild( dielectricSurfaceChargeDensity );
        // E-field
        eEffective = new ValueNode( "E_effective", "V/m", "0.000E00" );
        parentNode.addChild( eEffective );
        ePlates = new ValueNode( "E_plates", "V/m", "0.000E00" );
        parentNode.addChild( ePlates );
        eAir = new ValueNode( "E_air", "V/m", "0.000E00" );
        parentNode.addChild( eAir );
        eDielectric = new ValueNode( "E_dielectric", "V/m", "0.000E00" );
        parentNode.addChild( eDielectric );
        // energy
        energyStoredNode = new ValueNode( "U (energy stored)", "J", "0.000E00" );
        parentNode.addChild( energyStoredNode );
        
        // close button
        closeButton = new PImage( CLImages.CLOSE_BUTTON );
        closeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseReleased( PInputEvent event ) {
                DeveloperMeterNode.this.setVisible( false );
            }
        });
        addChild( closeButton );
        
        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new BoundedDragHandler( this, dragBoundsNode ) );
        
        updateDielectricListener();
        updateValues();
    }
    
    private void updateValues() {
        
        BatteryCapacitorCircuit circuit = model.getCircuit();
        Battery battery = circuit.getBattery();
        Capacitor capacitor = circuit.getCapacitor();
        
        /* user settings */
        batteryVoltageNode.setValue( battery.getVoltage() );
        manualPlateChargeNode.setValue( circuit.getManualPlateCharge() );
        plateSideLengthNode.setValue( capacitor.getPlateSideLength() );
        plateSeparationNode.setValue( capacitor.getPlateSeparation() );
        dielectricConstantNode.setValue( capacitor.getDielectricMaterial().getDielectricConstant() );
        
        /* derived */
        // area
        plateAreaNode.setValue( capacitor.getPlateArea() );
        dielectricContactAreaNode.setValue( capacitor.getDielectricContactArea() );
        airContactAreaNode.setValue( capacitor.getAirContactArea() );
        // capacitance
        airCapacitanceNode.setValue( capacitor.getAirCapacitance() );
        dielectricCapacitanceNode.setValue( capacitor.getDieletricCapacitance() );
        totalCapacitanceNode.setValue( capacitor.getTotalCapacitance() );
        // voltage
        plateVoltageNode.setValue( circuit.getPlateVoltage() );
        // charge
        airCharge.setValue( circuit.getAirPlateCharge() );
        dielectricCharge.setValue( circuit.getDielectricPlateCharge() );
        totalCharge.setValue( circuit.getTotalPlateCharge() );
        excessAirCharge.setValue( circuit.getExcessAirPlateCharge() );
        excessDielectricCharge.setValue( circuit.getExcessDielectricPlateCharge() );
        // surface charge density
        airSurfaceChargeDensity.setValue( model.getCircuit().getAirSurfaceDensityCharge() );
        dielectricSurfaceChargeDensity.setValue( model.getCircuit().getDielectricSurfaceDensityCharge() );
        // E-field
        eEffective.setValue( model.getCircuit().getEffectiveEfield() );
        ePlates.setValue( model.getCircuit().getPlatesEField() );
        eAir.setValue( model.getCircuit().getAirEField() );
        eDielectric.setValue( model.getCircuit().getDielectricEField() );
        // energy
        energyStoredNode.setValue( model.getCircuit().getStoredEnergy() );
        
        // layout
        layoutColumnLeftAlign( parentNode );
        
        // background
        PBounds bounds = parentNode.getFullBoundsReference();
        double x = bounds.getX() - BACKGROUND_MARGIN;
        double y = bounds.getY() - BACKGROUND_MARGIN;
        double w = Math.max( bounds.getWidth() + ( 2 * BACKGROUND_MARGIN ), maxBackgroundWidth );
        double h = bounds.getHeight() + ( 2 * BACKGROUND_MARGIN );
        backgroundRect.setRect( x, y, w, h );
        backgroundPath.setPathTo( backgroundRect );
        maxBackgroundWidth = w;
        
        // close button
        x = backgroundPath.getFullBoundsReference().getMaxX() - closeButton.getFullBoundsReference().getWidth() - 4;
        y = backgroundPath.getFullBoundsReference().getMinY() + 4;
        closeButton.setOffset( x, y );
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
     * Invisible children are skipped.
     */
    private void layoutColumnLeftAlign( PNode parentNode ) {
        double ySpacing = 2;
        double x = 0;
        double y = 0;
        PNode previousNode = null;
        for ( int i = 0; i < parentNode.getChildrenCount(); i++ ) {
            PNode child = parentNode.getChild( i );
            if ( child.getVisible() ) {
                if ( previousNode == null ) {
                    child.setOffset( x, y );
                }
                else {
                    y = previousNode.getFullBoundsReference().getMaxY() + ySpacing;
                    child.setOffset( x, y );
                }
                previousNode = child;
            }
        }
    }
    
    private static class ValueNode extends HTMLNode {
        
        private final String label;
        private final String units;
        private final NumberFormat format;
        
        public ValueNode( String label, String units, String pattern ) {
            setFont( new PhetFont() );
            this.label = label;
            this.units = units;
            this.format = new DecimalFormat( pattern );
        }
        
        public void setValue( double value ) {
            String valueString = ( value == 0 ) ? String.valueOf( value ) : format.format( value  );
            setHTML( label + " = " +  valueString + " " + units );
        }
    }
}
