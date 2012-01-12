// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.test;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.opticaltweezers.control.FluidControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.MotorsDefaults;
import edu.colorado.phet.opticaltweezers.model.Fluid;

/**
 * Test harness for FluidControlPanel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestFluidControlPanel extends JFrame {

    public TestFluidControlPanel() {
        
        Font font = new JLabel().getFont();
        
        Fluid fluid = new Fluid( 
                MotorsDefaults.FLUID_SPEED_RANGE,
                MotorsDefaults.FLUID_DIRECTION,
                MotorsDefaults.FLUID_VISCOSITY_RANGE, 
                MotorsDefaults.FLUID_TEMPERATURE_RANGE,
                MotorsDefaults.FLUID_APT_CONCENTRATION_RANGE );
        
        JPanel panel = new FluidControlPanel( fluid, font );
        
        setContentPane( panel );
        pack();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    public static void main( String[] args ) {
        TestFluidControlPanel test = new TestFluidControlPanel();
        test.show();
    }
}
