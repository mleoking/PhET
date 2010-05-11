/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeListener;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
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
    private static final double BACKGROUND_MARGIN = 3;
    
    private final CLModel model;
    
    private final PComposite parentNode;
    private final ValueNode voltageNode;
    private final ValueNode plateSize;
    private final ValueNode plateAreaNode, dielectricContactAreaNode;
    private final ValueNode plateSeparationNode;
    private final ValueNode capacitanceNode;
    private final ValueNode plateChargeNode, excessPlateChargeNode;
    private final ValueNode effectiveFieldNode, plateFieldNode, dielectricFieldNode;
    private final ValueNode energyStoredNode;
    
    private final Rectangle2D backgroundRect;
    private final PPath backgroundPath;

    private CustomDielectricMaterial customDielectric;
    private CustomDielectricChangeListener customDielectricChangeListener;

    public DeveloperMeterNode( CLModel model, PNode dragBoundsNode ) {
        
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
        
        // background
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
        parentNode.addChild( new PText( "----------" ) );
        voltageNode = new ValueNode( "V (voltage)", "V" );
        parentNode.addChild( voltageNode );
        plateSize = new ValueNode( "L (plate side length)", "m" );
        parentNode.addChild( plateSize );
        plateSeparationNode = new ValueNode( "d (plate separation)", "m" );
        parentNode.addChild( plateSeparationNode );
        parentNode.addChild( new PText( "----------" ) );
        plateAreaNode = new ValueNode( "A (plate area)", "m<sup>2</sup>" );
        parentNode.addChild( plateAreaNode );
        dielectricContactAreaNode = new ValueNode( "A<sub>dielectric</sub> (dielectric contact area)", "m<sup>2</sup>" );
        parentNode.addChild( dielectricContactAreaNode );
        parentNode.addChild( new PText( "----------" ) );
        capacitanceNode = new ValueNode( "C (capacitance)", "F" );
        parentNode.addChild( capacitanceNode );
        plateChargeNode = new ValueNode( "Q (plate charge)", "C" );
        parentNode.addChild( plateChargeNode );
        excessPlateChargeNode = new ValueNode( "Q<sub>excess</sub> (excess plate charge)", "C" );
        parentNode.addChild( excessPlateChargeNode );
        effectiveFieldNode = new ValueNode( "E<sub>effective</sub>", "V/m" );
        parentNode.addChild( effectiveFieldNode );
        plateFieldNode = new ValueNode( "E<sub>plate</sub>", "V/m" );
        parentNode.addChild( plateFieldNode );
        dielectricFieldNode = new ValueNode( "E<sub>dielectric</sub>", "V/m" );
        parentNode.addChild( dielectricFieldNode );
        energyStoredNode = new ValueNode( "U (energy stored)", "J" );
        parentNode.addChild( energyStoredNode );
        
        // close button
        PImage closeButton = new PImage( CLImages.CLOSE_BUTTON );
        double x = parentNode.getFullBoundsReference().getMinX() - closeButton.getFullBoundsReference().getWidth() - 5;
        double y = parentNode.getFullBoundsReference().getMinY();
        closeButton.setOffset( x, y );
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
        voltageNode.setValue( model.getBattery().getVoltage() );
        plateSize.setValue( model.getCapacitor().getPlateSize() );
        plateAreaNode.setValue( model.getCapacitor().getPlateArea() );
        dielectricContactAreaNode.setValue( model.getCapacitor().getDielectricContactArea() );
        plateSeparationNode.setValue( model.getCapacitor().getPlateSeparation() );
        capacitanceNode.setValue( model.getCircuit().getCapacitance() );
        plateChargeNode.setValue( model.getCircuit().getPlateCharge() );
        excessPlateChargeNode.setValue( model.getCircuit().getExcessPlateCharge() );
        effectiveFieldNode.setValue( model.getCircuit().getEffectiveEfield() );
        plateFieldNode.setValue( model.getCircuit().getPlatesEField() );
        dielectricFieldNode.setValue( model.getCircuit().getDielectricEField() );
        energyStoredNode.setValue( model.getCircuit().getStoredEnergy() );
        
        // layout
        layoutColumnLeftAlign( parentNode );
        
        // background
        PBounds bounds = parentNode.getFullBoundsReference();
        backgroundRect.setRect( bounds.getX() - BACKGROUND_MARGIN, bounds.getY() - BACKGROUND_MARGIN, bounds.getWidth() + ( 2 * BACKGROUND_MARGIN ), bounds.getHeight() + ( 2 * BACKGROUND_MARGIN ));
        backgroundPath.setPathTo( backgroundRect );
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
    private void layoutColumnLeftAlign( PNode parentNode ) {
        double ySpacing = 2;
        double x = 0;
        double y = 0;
        for ( int i = 0; i < parentNode.getChildrenCount(); i++ ) {
            if ( i == 0 ) {
                parentNode.getChild( i ).setOffset( x, y );
            }
            else {
                y = parentNode.getChild( i - 1 ).getFullBoundsReference().getMaxY() + ySpacing;
                parentNode.getChild( i ).setOffset( x, y );
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
