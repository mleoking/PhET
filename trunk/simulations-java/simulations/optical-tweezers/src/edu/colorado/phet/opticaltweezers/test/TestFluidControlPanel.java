/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.opticaltweezers.control.FluidControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.GlobalDefaults;
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
                GlobalDefaults.FLUID_SPEED_RANGE,
                GlobalDefaults.FLUID_DIRECTION,
                GlobalDefaults.FLUID_VISCOSITY_RANGE, 
                GlobalDefaults.FLUID_TEMPERATURE_RANGE );
        
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
