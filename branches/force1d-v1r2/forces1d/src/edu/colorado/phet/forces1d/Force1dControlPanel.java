/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.view.components.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.forces1d.model.Force1DModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 22, 2004
 * Time: 11:11:57 AM
 * Copyright (c) Nov 22, 2004 by Sam Reid
 */
public class Force1dControlPanel extends VerticalLayoutPanel {
    Force1DModule module;
    private Force1DModel model;
    public static final double MAX_KINETIC_FRICTION = 1.0;

    public Force1dControlPanel( Force1DModule module ) {
        this.module = module;
        model = module.getForceModel();
        JPanel mass = createSpinner( 5, 0.1, 1000, 1.0, "Mass", "kg", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setMass( value );
            }
        } );
        JPanel gravity = createSpinner( 9.8, 0, 100, .2, "Gravity", "N/kg", new SpinnerHandler() {
            public void changed( double value ) {
                model.setGravity( value );
            }
        } );
        JPanel appliedForce = createSpinner( 0, -100, 100, .5, "Applied Force", "N", new SpinnerHandler() {
            public void changed( double value ) {
                model.setAppliedForce( value );
            }
        } );
        JPanel staticFriction = createSpinner( 0.10, 0, MAX_KINETIC_FRICTION, .01, "Static Friction", "", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setStaticFriction( value );
            }
        } );
        JPanel kineticFriction = createSpinner( 0.05, 0, MAX_KINETIC_FRICTION, .01, "Kinetic Friction", "", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setKineticFriction( value );
            }
        } );

        VerticalLayoutPanel controls = new VerticalLayoutPanel();
        controls.add( mass );
        controls.add( gravity );
        controls.add( appliedForce );
        controls.add( staticFriction );
        controls.add( kineticFriction );

        controls.setBorder( Force1DUtil.createTitledBorder( "Controls" ) );
        setAnchor( GridBagConstraints.CENTER );
        setFill( GridBagConstraints.NONE );
        add( controls );
    }

    private JPanel createSpinner( double value, double min, double max, double spacing, String name, String units, final SpinnerHandler handler ) {
        SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, spacing );
        final JSpinner sp = new JSpinner( model );
        JPanel panel = new HorizontalLayoutPanel();
        String unitStr = "";
        if( !units.trim().equals( "" ) ) {
            unitStr = "(" + units + ")";
        }
        sp.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = ( (Double)sp.getValue() ).doubleValue();
                handler.changed( value );
            }
        } );
        handler.changed( value );
        JLabel label = new JLabel( name + " " + unitStr );
        panel.add( label );
        panel.add( sp );
        return panel;
    }

}

interface SpinnerHandler {
    void changed( double value );
}