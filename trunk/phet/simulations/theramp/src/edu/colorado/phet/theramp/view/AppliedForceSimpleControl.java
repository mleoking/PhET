/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Aug 4, 2005
 * Time: 6:38:01 PM
 * Copyright (c) Aug 4, 2005 by Sam Reid
 */

public class AppliedForceSimpleControl extends PNode {
    private RampModule module;
    private RampPanel rampPanel;

    public AppliedForceSimpleControl( final RampModule module, RampPanel rampPanel ) {
        this.module = module;
        this.rampPanel = rampPanel;
        double maxValue = 3000;
        HorizontalLayoutPanel horizontalLayoutPanel = new HorizontalLayoutPanel();
//        horizontalLayoutPanel.add( new JLabel( "Applied Force (N)" ) );

        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        verticalLayoutPanel.add( new JLabel( TheRampStrings.getString( "applied.force.n" ) ) );

        SpinnerNumberModel model = new SpinnerNumberModel( module.getRampPhysicalModel().getAppliedForceScalar(), -maxValue, maxValue, 100 );

        final JSpinner spinner = new JSpinner( model );
        spinner.setEditor( new JSpinner.NumberEditor( spinner, "0.00" ) );
        verticalLayoutPanel.add( spinner );
        horizontalLayoutPanel.add( verticalLayoutPanel );
        PSwing pSwing = new PSwing( rampPanel, horizontalLayoutPanel );
        addChild( pSwing );
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Number value = (Number)spinner.getValue();
                module.setAppliedForce( value.doubleValue() );
            }
        } );
        module.getRampPhysicalModel().addListener( new RampPhysicalModel.Adapter() {
            public void appliedForceChanged() {
                Double value = new Double( module.getRampPhysicalModel().getAppliedForceScalar() );
                if( !spinner.getValue().equals( value ) ) {
                    spinner.setValue( value );
                    repaint();
                }
            }

        } );
    }
}
