/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial.CustomDielectricChangeListener;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Panel that displays all "raw" model values.
 * This is intended for developer use and is not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelValuesPanel extends JPanel {
    
    private static final Font VALUE_DISPLAY_FONT = new PhetFont( 10 );
    
    private final CLModel model;
    
    // user settings
    private final ValueDisplay batteryVoltage;
    private final ValueDisplay disconnectedCharge;
    private final ValueDisplay plateSideLength;
    private final ValueDisplay plateSeparation;
    private final ValueDisplay dielectricOffset;
    private final ValueDisplay dielectricConstant;
    
    // derived values
    private final ValueDisplay dielectricContactArea, airContactArea, plateArea;
    private final ValueDisplay airCapacitance, dielectricCapacitance, totalCapacitance;
    private final ValueDisplay plateVoltage;
    private final ValueDisplay airCharge, dielectricCharge, totalCharge, excessAirCharge, excessDielectricCharge;
    private final ValueDisplay airSurfaceChargeDensity, dielectricSurfaceChargeDensity;
    private final ValueDisplay eEffective, ePlates, eAir, eDielectric;
    private final ValueDisplay energyStored;
    
    private CustomDielectricMaterial customDielectric;
    private CustomDielectricChangeListener customDielectricChangeListener;
    private BatteryCapacitorCircuitChangeListener circuitChangeListener;

    public ModelValuesPanel( CLModel model ) {
        
        this.model = model;
        circuitChangeListener = new BatteryCapacitorCircuitChangeListener() {
            
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
        };
        model.getCircuit().addBatteryCapacitorCircuitChangeListener( circuitChangeListener );
        
        customDielectricChangeListener = new CustomDielectricChangeListener() {
            public void dielectricConstantChanged() {
                updateValues();
            }
        };
        
        // instructions
        JLabel instructions = new JLabel( "= MOUSE OVER FOR DESCRIPTIONS =" );
        instructions.setForeground( Color.RED );
        instructions.setFont( VALUE_DISPLAY_FONT );
        
        // constants panel
        JPanel constantsPanel = new VerticalPanel();
        constantsPanel.setBorder( new TitledBorder( "Constants" ) );
        ValueDisplay epsilon0 = new ValueDisplay( CLStrings.EPSILON + "0", "F/m", "0.000E00", CLConstants.EPSILON_0  );
        epsilon0.setToolTipText( "vacuum permittivity" );
        constantsPanel.add( epsilon0 );
        ValueDisplay epsilonAir = new ValueDisplay( CLStrings.EPSILON + "_air", "", "0.00000000", CLConstants.EPSILON_AIR );
        epsilonAir.setToolTipText( "dielectric constant of air" );
        constantsPanel.add( epsilonAir );
        ValueDisplay epsilonVacuum = new ValueDisplay( CLStrings.EPSILON + "_vacuum", "", "0.0", CLConstants.EPSILON_VACUUM );
        epsilonVacuum.setToolTipText( "dielectric constant of a vacuum" );
        constantsPanel.add( epsilonVacuum );
        
        // user settings panel
        JPanel settingsPanel = new VerticalPanel();
        settingsPanel.setBorder( new TitledBorder( "User Settings" ) );
        batteryVoltage = new ValueDisplay( "V_battery", "V", "0.00" );
        batteryVoltage.setToolTipText( "<html>voltage of the battery,<br>used when battery is connected</html>" );
        settingsPanel.add( batteryVoltage );
        disconnectedCharge = new ValueDisplay( "Q_disconnected", "C", "0.000E00" );
        disconnectedCharge.setToolTipText( "<html>total plate change when<br>battery is disconnected</html>" );
        settingsPanel.add( disconnectedCharge );
        plateSideLength = new ValueDisplay( "L", "m", "0.0000" );
        plateSideLength.setToolTipText( "plate side length" );
        settingsPanel.add( plateSideLength );
        plateSeparation = new ValueDisplay( "d", "m", "0.0000" );
        plateSeparation.setToolTipText( "plate separation distance" );
        settingsPanel.add( plateSeparation );
        dielectricOffset = new ValueDisplay( "offset", "m", "0.000" );
        dielectricOffset.setToolTipText( "how far the dielectric is pulled out" ); 
        settingsPanel.add( dielectricOffset );
        dielectricConstant = new ValueDisplay( CLStrings.EPSILON + "r", "", "0.000" );
        dielectricConstant.setToolTipText( "dielectric constant" ); 
        settingsPanel.add( dielectricConstant );
        
        // derived panel
        JPanel derivedPanel = new VerticalPanel();
        derivedPanel.setBorder( new TitledBorder( "Derived" ) );
        // area
        dielectricContactArea = new ValueDisplay( "A_dielectric", "m^2", "0.000000" );
        dielectricContactArea.setToolTipText( "area of dielectric between the plates" );
        derivedPanel.add( dielectricContactArea );
        airContactArea = new ValueDisplay( "A_air", "m^2", "0.000000" );
        airContactArea.setToolTipText( "area of air between the plates" );
        derivedPanel.add( airContactArea );
        plateArea = new ValueDisplay( "A_plate", "m^2", "0.000000" );
        plateArea.setToolTipText( "plate area" );
        derivedPanel.add( plateArea );
        // capacitance
        derivedPanel.add( new JSeparator() );
        airCapacitance = new ValueDisplay( "C_air", "F", "0.000E00" );
        airCapacitance.setToolTipText( "capacitance due to air" );
        derivedPanel.add( airCapacitance );
        dielectricCapacitance = new ValueDisplay( "C_dielectric", "F", "0.000E00" );
        dielectricCapacitance.setToolTipText( "capacitor due to dielectric" );
        derivedPanel.add( dielectricCapacitance );
        totalCapacitance = new ValueDisplay( "C_total", "F", "0.000E00" );
        totalCapacitance.setToolTipText( "total capacitance" );
        derivedPanel.add( totalCapacitance );
        // voltage
        derivedPanel.add( new JSeparator() );
        plateVoltage = new ValueDisplay( "V_plate", "V", "0.00" );
        plateVoltage.setToolTipText( "voltage difference between plates" );
        derivedPanel.add( plateVoltage );
        // charge
        derivedPanel.add( new JSeparator() );
        airCharge = new ValueDisplay( "Q_air", "C", "0.000E00" );
        airCharge.setToolTipText( "plate charge due to air" );
        derivedPanel.add( airCharge );
        dielectricCharge = new ValueDisplay( "Q_dielectric", "C", "0.000E00" );
        dielectricCharge.setToolTipText( "plate charge due to dielectric" );
        derivedPanel.add( dielectricCharge );
        totalCharge = new ValueDisplay( "Q_total", "C", "0.000E00" );
        totalCharge.setToolTipText( "total charge on top plate" );
        derivedPanel.add( totalCharge );
        excessAirCharge = new ValueDisplay( "Q_excess_air", "C", "0.000E00" );
        excessAirCharge.setToolTipText( "excess charge due to air" );
        derivedPanel.add( excessAirCharge );
        excessDielectricCharge = new ValueDisplay( "Q_excess_dielectric", "C", "0.000E00" );
        excessDielectricCharge.setToolTipText( "excess charge due to dielectric" );
        derivedPanel.add( excessDielectricCharge );
        // surface charge density
        derivedPanel.add( new JSeparator() );
        airSurfaceChargeDensity = new ValueDisplay( CLStrings.SIGMA + "_air", "C/m^2", "0.000E00" );
        airSurfaceChargeDensity.setToolTipText( "surface charge density due to air" );
        derivedPanel.add( airSurfaceChargeDensity );
        dielectricSurfaceChargeDensity = new ValueDisplay( CLStrings.SIGMA + "_dielectric", "C/m^2", "0.000E00" );
        dielectricSurfaceChargeDensity.setToolTipText( "surface charge density due to dielectric" );
        derivedPanel.add( dielectricSurfaceChargeDensity );
        // E-field
        derivedPanel.add( new JSeparator() );
        eEffective = new ValueDisplay( "E_effective", "V/m", "0.000E00" );
        eEffective.setToolTipText( "effective field between plates" );
        derivedPanel.add( eEffective );
        ePlates = new ValueDisplay( "E_plates", "V/m", "0.000E00" );
        ePlates.setToolTipText( "field due to plates alone" );
        derivedPanel.add( ePlates );
        eAir = new ValueDisplay( "E_air", "V/m", "0.000E00" );
        eAir.setToolTipText( "field in air volume" );
        derivedPanel.add( eAir );
        eDielectric = new ValueDisplay( "E_dielectric", "V/m", "0.000E00" );
        eDielectric.setToolTipText( "field in dielectric volume" );
        derivedPanel.add( eDielectric );
        // energy
        derivedPanel.add( new JSeparator() );
        energyStored = new ValueDisplay( "U", "J", "0.000E00" );
        energyStored.setToolTipText( "stored energy" );
        derivedPanel.add( energyStored );
        
        // layout
        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add( instructions );
        mainPanel.add( constantsPanel );
        mainPanel.add( settingsPanel );
        mainPanel.add( derivedPanel );
        mainPanel.add( Box.createHorizontalStrut( 180 ) ); // HACK to keep the labels from jumping around
        
        setLayout( new BorderLayout() );
        add( mainPanel, BorderLayout.WEST );
        
        updateDielectricListener();
        updateValues();
    }
    
    /**
     * Unregister for notifications.
     */
    public void cleanup() {
        model.getCircuit().removeBatteryCapacitorCircuitChangeListener( circuitChangeListener );
        if ( customDielectric != null ) {
            customDielectric.removeCustomDielectricChangeListener( customDielectricChangeListener );
            customDielectric = null;
        }
    }
    
    /*
     * Synchronizes the display to match the model.
     */
    private void updateValues() {
        
        BatteryCapacitorCircuit circuit = model.getCircuit();
        Battery battery = circuit.getBattery();
        Capacitor capacitor = circuit.getCapacitor();
        
        /* user settings */
        batteryVoltage.setValue( battery.getVoltage() );
        disconnectedCharge.setValue( circuit.getManualPlateCharge() );
        plateSideLength.setValue( capacitor.getPlateSideLength() );
        plateSeparation.setValue( capacitor.getPlateSeparation() );
        dielectricOffset.setValue( capacitor.getDielectricOffset() );
        dielectricConstant.setValue( capacitor.getDielectricMaterial().getDielectricConstant() );
        
        /* derived */
        // area
        plateArea.setValue( capacitor.getPlateArea() );
        dielectricContactArea.setValue( capacitor.getDielectricContactArea() );
        airContactArea.setValue( capacitor.getAirContactArea() );
        // capacitance
        airCapacitance.setValue( capacitor.getAirCapacitance() );
        dielectricCapacitance.setValue( capacitor.getDieletricCapacitance() );
        totalCapacitance.setValue( capacitor.getTotalCapacitance() );
        // voltage
        plateVoltage.setValue( circuit.getPlateVoltage() );
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
        energyStored.setValue( model.getCircuit().getStoredEnergy() );
    }
    
    /*
     * Manages change notification for custom dielectrics.
     */
    private void updateDielectricListener() {
        // unregister for notification from previous custom dielectric
        if ( customDielectric != null ) {
            customDielectric.removeCustomDielectricChangeListener( customDielectricChangeListener );
            customDielectric = null;
        }
        // register for notification from current custom dielectric
        DielectricMaterial material = model.getCapacitor().getDielectricMaterial();
        if ( material instanceof CustomDielectricMaterial ) {
            customDielectric = (CustomDielectricMaterial) material;
            customDielectric.addCustomDielectricChangeListener( customDielectricChangeListener );
        }
    }
    
    /*
     * Displays a value, in the format "label = value units".
     */
    private static class ValueDisplay extends JLabel {
        
        private final String label;
        private final String units;
        private final NumberFormat format;
        
        public ValueDisplay( String label, String units, String pattern ) {
            this( label, units, pattern, 0 );
        }
        
        public ValueDisplay( String label, String units, String pattern, double value ) {
            setFont( VALUE_DISPLAY_FONT );
            this.label = label;
            this.units = units;
            this.format = new DecimalFormat( pattern );
            setValue( value );
        }
        
        public void setValue( double value ) {
            String valueString = ( value == 0 ) ? "0" : format.format( value  );
            setText( label + " = " +  valueString + " " + units );
        }
    }
    
    private static class VerticalPanel extends JPanel {
        
        private final JPanel innerPanel; // trick to get nested panels to left justify
        private final EasyGridBagLayout layout;
        private int row;
        
        public VerticalPanel() {
            
            // components will be added to any inner panel
            innerPanel = new JPanel();
            layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            row = 0;
            
            setLayout( new BorderLayout() );
            super.add( innerPanel, BorderLayout.WEST );
        }
        
        @Override
        public Component add( Component component ) {
            layout.addComponent( component, row++, 0 );
            return component;
        }
    }
}
