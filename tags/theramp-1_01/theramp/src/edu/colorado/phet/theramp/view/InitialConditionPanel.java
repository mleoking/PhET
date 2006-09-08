/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.RampPhysicalModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 6, 2005
 * Time: 4:23:38 AM
 * Copyright (c) Aug 6, 2005 by Sam Reid
 */

public class InitialConditionPanel extends VerticalLayoutPanel {
    public InitialConditionPanel( final RampModule rampModule ) {
        double maxValue = 3000;
        final ModelSlider modelSlider = new ModelSlider( TheRampStrings.getString( "applied.force" ), TheRampStrings.getString( "newtons" ), -maxValue, maxValue, 0, new DecimalFormat( "0.00" ) );
        modelSlider.setModelTicks( new double[]{-maxValue, 0, maxValue} );
//        PSwing pSwing = new PSwing( rampPanel, modelSlider );
//        addChild( pSwing );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rampModule.setAppliedForce( modelSlider.getValue() );
            }
        } );
        rampModule.getRampPhysicalModel().addListener( new RampPhysicalModel.Adapter() {
            public void appliedForceChanged() {
                modelSlider.setValue( rampModule.getRampPhysicalModel().getAppliedForceScalar() );
            }

        } );
        addFullWidth( modelSlider );

        final ModelSlider rampAngleSlider = new ModelSlider( TheRampStrings.getString( "ramp.angle" ), TheRampStrings.getString( "degrees" ), 0, 90, rampModule.getRampAngle() * 180.0 / Math.PI );
        rampAngleSlider.setModelTicks( new double[]{0, 45, 90} );
        rampAngleSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double radians = rampAngleSlider.getValue() * Math.PI * 2.0 / 360.0;
                rampModule.setRampAngle( radians );
            }
        } );
        rampModule.getRampPhysicalModel().getRamp().addObserver( new SimpleObserver() {
            public void update() {
                rampAngleSlider.setValue( rampModule.getRampAngle() * 180.0 / Math.PI );
            }
        } );
        addFullWidth( rampAngleSlider );

        final ModelSlider blockPosition = new ModelSlider( TheRampStrings.getString( "position" ), TheRampStrings.getString( "meters" ), rampModule.getGlobalMinPosition(), rampModule.getGlobalMaxPosition(), rampModule.getGlobalBlockPosition() );
        blockPosition.setModelTicks( new double[]{rampModule.getGlobalMinPosition(), ( rampModule.getGlobalMaxPosition() + rampModule.getGlobalMinPosition() ) / 2, rampModule.getGlobalMaxPosition()} );
        blockPosition.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double position = blockPosition.getValue();
                rampModule.setGlobalBlockPosition( position );
            }
        } );
        rampModule.getBlock().addListener( new Block.Adapter() {
            public void surfaceChanged() {
                blockPosition.setValue( rampModule.getGlobalBlockPosition() );
            }

            public void positionChanged() {
                blockPosition.setValue( rampModule.getGlobalBlockPosition() );
            }
        } );
        addFullWidth( blockPosition );

//        final ModelSlider blockVelocity = new ModelSlider( "Velocity", "meters/second", -20, 20, rampModule.getBlock().getVelocity() );
        final ModelSlider blockVelocity = new ModelSlider( TheRampStrings.getString( "velocity" ), TheRampStrings.getString( "meters.second" ), -20, 20, rampModule.getBlock().getVelocity() );
        blockVelocity.setModelTicks( new double[]{-20, 0, 20} );
        blockVelocity.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rampModule.getBlock().setVelocity( blockVelocity.getValue() );
            }
        } );
        rampModule.getBlock().addListener( new Block.Adapter() {
            public void velocityChanged() {
                blockVelocity.setValue( rampModule.getBlock().getVelocity() );
            }
        } );
        addFullWidth( blockVelocity );
    }

//    private void addFullWidth( ModelSlider modelSlider ) {
//        add( modelSlider );
//    }
}
