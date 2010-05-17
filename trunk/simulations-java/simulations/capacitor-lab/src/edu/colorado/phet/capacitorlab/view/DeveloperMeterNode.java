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
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeListener;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Air;
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
    private final ValueNode userVoltageNode;
    private final ValueNode userPlateChargeNode;
    private final ValueNode plateSize;
    private final ValueNode dielectricConstantNode;
    private final ValueNode plateAreaNode, dielectricContactAreaNode;
    private final ValueNode plateSeparationNode;
    private final ValueNode capacitanceNode;
    private final ValueNode voltageNode;
    private final ValueNode plateChargeNode;
    private final ValueNode excessPlateChargeNode;
    private final ValueNode surfaceChargeDensityNode;
    private final ValueNode effectiveFieldNode, plateFieldNode, dielectricFieldNode;
    private final ValueNode energyStoredNode;
    
    private final Rectangle2D backgroundRect;
    private final PPath backgroundPath;
    private double maxBackgroundWidth; // this minimizes distracting growing/shrinking of the background
    
    private final PImage closeButton;

    private CustomDielectricMaterial customDielectric;
    private CustomDielectricChangeListener customDielectricChangeListener;

    public DeveloperMeterNode( CLModel model, PNode dragBoundsNode ) {
        
        this.model = model;
        model.getCircuit().addBatteryCapacitorCircuitChangeListener( new  BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void batteryConnectedChanged() {
                updateValues();
            }
        });
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
        ValueNode vacuumPermittivityNode = new ValueNode( CLStrings.EPSILON + "<sub>0</sub>", "F/m", "0.000E00" );
        vacuumPermittivityNode.setValue( CLConstants.E0 );
        parentNode.addChild( vacuumPermittivityNode );
        ValueNode airDielectricConstantNode = new ValueNode( CLStrings.EPSILON + "<sub>a</sub>", "", "0.00000000" );
        airDielectricConstantNode.setValue( new Air().getDielectricConstant() );
        parentNode.addChild( airDielectricConstantNode );
        
        parentNode.addChild( new PText( " " ) );
        parentNode.addChild( new PText( "User Settings ....................................................." ) );
        userVoltageNode = new ValueNode( "V<sub>battery</sub> (battery voltage)", "V", "0.00" );
        parentNode.addChild( userVoltageNode );
        userPlateChargeNode = new ValueNode( "Q<sub>user</sub> (plate charge)", "Coulombs", "0.000E00" );
        parentNode.addChild( userPlateChargeNode );
        plateSize = new ValueNode( "L (plate side length)", "m", "0.0000" );
        parentNode.addChild( plateSize );
        plateSeparationNode = new ValueNode( "d (plate separation)", "m", "0.0000" );
        parentNode.addChild( plateSeparationNode );
        dielectricConstantNode = new ValueNode( CLStrings.EPSILON + "<sub>r</sub>", "", "0.000" );
        parentNode.addChild( dielectricConstantNode );
        
        parentNode.addChild( new PText( " " ) );
        parentNode.addChild( new PText( "Derived ....................................................." ) );
        plateAreaNode = new ValueNode( "A (plate area)", "m<sup>2</sup>", "0.000000" );
        parentNode.addChild( plateAreaNode );
        dielectricContactAreaNode = new ValueNode( "A<sub>d</sub> (dielectric contact area)", "m<sup>2</sup>", "0.000000" );
        parentNode.addChild( dielectricContactAreaNode );
        capacitanceNode = new ValueNode( "C (capacitance)", "F", "0.000E00" );
        parentNode.addChild( capacitanceNode );
        voltageNode = new ValueNode( "V (voltage)", "V", "0.00" );
        parentNode.addChild( voltageNode );
        plateChargeNode = new ValueNode( "Q (plate charge)", "C", "0.000E00" );
        parentNode.addChild( plateChargeNode );
        excessPlateChargeNode = new ValueNode( "Q<sub>excess</sub> (excess plate charge)", "C", "0.000E00" );
        parentNode.addChild( excessPlateChargeNode );
        surfaceChargeDensityNode = new ValueNode( CLStrings.SIGMA + " (surface charge density)", "C/m<sup>2</sup>", "0.000E00" );
        parentNode.addChild( surfaceChargeDensityNode );
        effectiveFieldNode = new ValueNode( "E<sub>effective</sub>", "V/m", "0.000E00" );
        parentNode.addChild( effectiveFieldNode );
        plateFieldNode = new ValueNode( "E<sub>plate</sub>", "V/m", "0.000E00" );
        parentNode.addChild( plateFieldNode );
        dielectricFieldNode = new ValueNode( "E<sub>dielectric</sub>", "V/m", "0.000E00" );
        parentNode.addChild( dielectricFieldNode );
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
        
        // set values
        userVoltageNode.setValue( model.getCircuit().getVoltage() );
        userPlateChargeNode.setValue( model.getCircuit().getPlateCharge() );
        plateSize.setValue( model.getCapacitor().getPlateSize() );
        plateAreaNode.setValue( model.getCapacitor().getPlateArea() );
        dielectricConstantNode.setValue( model.getCapacitor().getDielectricMaterial().getDielectricConstant() );
        dielectricContactAreaNode.setValue( model.getCapacitor().getDielectricContactArea() );
        plateSeparationNode.setValue( model.getCapacitor().getPlateSeparation() );
        capacitanceNode.setValue( model.getCircuit().getCapacitance() );
        voltageNode.setValue( model.getCircuit().getVoltage() );
        plateChargeNode.setValue( model.getCircuit().getPlateCharge() );
        surfaceChargeDensityNode.setValue( model.getCircuit().getSurfaceDensityCharge() );
        excessPlateChargeNode.setValue( model.getCircuit().getExcessPlateCharge() );
        effectiveFieldNode.setValue( model.getCircuit().getEffectiveEfield() );
        plateFieldNode.setValue( model.getCircuit().getPlatesEField() );
        dielectricFieldNode.setValue( model.getCircuit().getDielectricEField() );
        energyStoredNode.setValue( model.getCircuit().getStoredEnergy() );
        
        // visibility
        boolean batteryConnected = model.getCircuit().isBatteryConnected();
        userVoltageNode.setVisible( batteryConnected );
        userPlateChargeNode.setVisible( !batteryConnected );
        
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
