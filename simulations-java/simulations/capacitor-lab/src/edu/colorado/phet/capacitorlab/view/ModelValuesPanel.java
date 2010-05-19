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
 * Variable names correspond to the variables in the design specification,
 * and therefore violation Java naming conventions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelValuesPanel extends JPanel {
    
    private static final Font VALUE_DISPLAY_FONT = new PhetFont( 10 );
    
    private final CLModel model;
    
    // user settings
    private final ValueDisplay V_battery;
    private final ValueDisplay Q_disconnected;
    private final ValueDisplay L;
    private final ValueDisplay d;
    private final ValueDisplay offset;
    private final ValueDisplay epsilon_dielectric;
    
    // derived values
    private final ValueDisplay A_dielectric, A_air, A_plate;
    private final ValueDisplay C_air, C_dielectric, C;
    private final ValueDisplay V_plates;
    private final ValueDisplay Q_air, Q_dielectric, Q_total, Q_excess_air, Q_excess_dielectric;
    private final ValueDisplay sigma_air, sigma_dielectric;
    private final ValueDisplay E_effective, E_plates_air, E_plates_diectric, E_air, E_dielectric;
    private final ValueDisplay U;
    
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
        ValueDisplay epsilon0 = new ValueDisplay( CLStrings.EPSILON + "_0", "F/m", "0.000E00", CLConstants.EPSILON_0  );
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
        V_battery = new ValueDisplay( "V_battery", "V", "0.00" );
        V_battery.setToolTipText( "battery voltage" );
        settingsPanel.add( V_battery );
        Q_disconnected = new ValueDisplay( "Q_disconnected", "C", "0.000E00" );
        Q_disconnected.setToolTipText( "<html>total plate change when<br>battery is disconnected</html>" );
        settingsPanel.add( Q_disconnected );
        L = new ValueDisplay( "L", "m", "0.0000" );
        L.setToolTipText( "plate side length" );
        settingsPanel.add( L );
        d = new ValueDisplay( "d", "m", "0.0000" );
        d.setToolTipText( "plate separation distance" );
        settingsPanel.add( d );
        offset = new ValueDisplay( "offset", "m", "0.000" );
        offset.setToolTipText( "how far the dielectric is pulled out" ); 
        settingsPanel.add( offset );
        epsilon_dielectric = new ValueDisplay( CLStrings.EPSILON + "_dielectric", "", "0.000" );
        epsilon_dielectric.setToolTipText( "dielectric constant" ); 
        settingsPanel.add( epsilon_dielectric );
        
        // derived panel
        JPanel derivedPanel = new VerticalPanel();
        derivedPanel.setBorder( new TitledBorder( "Derived" ) );
        // area
        A_dielectric = new ValueDisplay( "A_dielectric", "m^2", "0.000000" );
        A_dielectric.setToolTipText( "area of dielectric between the plates" );
        derivedPanel.add( A_dielectric );
        A_air = new ValueDisplay( "A_air", "m^2", "0.000000" );
        A_air.setToolTipText( "area of air between the plates" );
        derivedPanel.add( A_air );
        A_plate = new ValueDisplay( "A_plate", "m^2", "0.000000" );
        A_plate.setToolTipText( "plate area" );
        derivedPanel.add( A_plate );
        // capacitance
        derivedPanel.add( new JSeparator() );
        C_air = new ValueDisplay( "C_air", "F", "0.000E00" );
        C_air.setToolTipText( "capacitance due to air" );
        derivedPanel.add( C_air );
        C_dielectric = new ValueDisplay( "C_dielectric", "F", "0.000E00" );
        C_dielectric.setToolTipText( "capacitor due to dielectric" );
        derivedPanel.add( C_dielectric );
        C = new ValueDisplay( "C_total", "F", "0.000E00" );
        C.setToolTipText( "total capacitance" );
        derivedPanel.add( C );
        // voltage
        derivedPanel.add( new JSeparator() );
        V_plates = new ValueDisplay( "V_plate", "V", "0.00" );
        V_plates.setToolTipText( "voltage difference between plates" );
        derivedPanel.add( V_plates );
        // charge
        derivedPanel.add( new JSeparator() );
        Q_air = new ValueDisplay( "Q_air", "C", "0.000E00" );
        Q_air.setToolTipText( "plate charge due to air" );
        derivedPanel.add( Q_air );
        Q_dielectric = new ValueDisplay( "Q_dielectric", "C", "0.000E00" );
        Q_dielectric.setToolTipText( "plate charge due to dielectric" );
        derivedPanel.add( Q_dielectric );
        Q_total = new ValueDisplay( "Q_total", "C", "0.000E00" );
        Q_total.setToolTipText( "total charge on top plate" );
        derivedPanel.add( Q_total );
        Q_excess_air = new ValueDisplay( "Q_excess_air", "C", "0.000E00" );
        Q_excess_air.setToolTipText( "excess charge due to air" );
        derivedPanel.add( Q_excess_air );
        Q_excess_dielectric = new ValueDisplay( "Q_excess_dielectric", "C", "0.000E00" );
        Q_excess_dielectric.setToolTipText( "excess charge due to dielectric" );
        derivedPanel.add( Q_excess_dielectric );
        // surface charge density
        derivedPanel.add( new JSeparator() );
        sigma_air = new ValueDisplay( CLStrings.SIGMA + "_air", "C/m^2", "0.000E00" );
        sigma_air.setToolTipText( "surface charge density due to air" );
        derivedPanel.add( sigma_air );
        sigma_dielectric = new ValueDisplay( CLStrings.SIGMA + "_dielectric", "C/m^2", "0.000E00" );
        sigma_dielectric.setToolTipText( "surface charge density due to dielectric" );
        derivedPanel.add( sigma_dielectric );
        // E-field
        derivedPanel.add( new JSeparator() );
        E_effective = new ValueDisplay( "E_effective", "V/m", "0.000E00" );
        E_effective.setToolTipText( "effective field between plates" );
        derivedPanel.add( E_effective );
        E_plates_air = new ValueDisplay( "E_plates_air", "V/m", "0.000E00" );
        E_plates_air.setToolTipText( "<html>field due to the plates in the<br>capacitor volume that contains air</html>" );
        derivedPanel.add( E_plates_air );
        E_plates_diectric = new ValueDisplay( "E_plates_dielectric", "V/m", "0.000E00" );
        E_plates_diectric.setToolTipText( "<html>field due to the plates in the<br>capacitor volume that contains dielectric</html>" );
        derivedPanel.add( E_plates_diectric );
        E_air = new ValueDisplay( "E_air", "V/m", "0.000E00" );
        E_air.setToolTipText( "field in air volume" );
        derivedPanel.add( E_air );
        E_dielectric = new ValueDisplay( "E_dielectric", "V/m", "0.000E00" );
        E_dielectric.setToolTipText( "field in dielectric volume" );
        derivedPanel.add( E_dielectric );
        // energy
        derivedPanel.add( new JSeparator() );
        U = new ValueDisplay( "U", "J", "0.000E00" );
        U.setToolTipText( "stored energy" );
        derivedPanel.add( U );
        
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
        V_battery.setValue( battery.getVoltage() );
        Q_disconnected.setValue( circuit.getDisconnectedPlateCharge() );
        L.setValue( capacitor.getPlateSideLength() );
        d.setValue( capacitor.getPlateSeparation() );
        offset.setValue( capacitor.getDielectricOffset() );
        epsilon_dielectric.setValue( capacitor.getDielectricMaterial().getDielectricConstant() );
        
        /* derived */
        // area
        A_plate.setValue( capacitor.getPlateArea() );
        A_dielectric.setValue( capacitor.getDielectricContactArea() );
        A_air.setValue( capacitor.getAirContactArea() );
        // capacitance
        C_air.setValue( capacitor.getAirCapacitance() );
        C_dielectric.setValue( capacitor.getDieletricCapacitance() );
        C.setValue( capacitor.getTotalCapacitance() );
        // voltage
        V_plates.setValue( circuit.getPlatesVoltage() );
        // charge
        Q_air.setValue( circuit.getAirPlateCharge() );
        Q_dielectric.setValue( circuit.getDielectricPlateCharge() );
        Q_total.setValue( circuit.getTotalPlateCharge() );
        Q_excess_air.setValue( circuit.getExcessAirPlateCharge() );
        Q_excess_dielectric.setValue( circuit.getExcessDielectricPlateCharge() );
        // surface charge density
        sigma_air.setValue( model.getCircuit().getAirSurfaceChargeDensity() );
        sigma_dielectric.setValue( model.getCircuit().getDielectricSurfaceChargeDensity() );
        // E-field
        E_effective.setValue( model.getCircuit().getEffectiveEfield() );
        E_plates_air.setValue( model.getCircuit().getPlatesAirEField() );
        E_plates_diectric.setValue( model.getCircuit().getPlatesDielectricEField() );
        E_air.setValue( model.getCircuit().getAirEField() );
        E_dielectric.setValue( model.getCircuit().getDielectricEField() );
        // energy
        U.setValue( model.getCircuit().getStoredEnergy() );
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
