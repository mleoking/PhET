/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.Ramp;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Oct 7, 2005
 * Time: 9:00:54 PM
 * Copyright (c) Oct 7, 2005 by Sam Reid
 */

public class PositionController {
    private RampModule rampModule;
    private ModelSlider modelSlider;

    public PositionController( final RampModule rampModule ) {
        this.rampModule = rampModule;
        this.modelSlider = new ModelSlider( TheRampStrings.getString( "position" ), TheRampStrings.getString( "meters" ), -getGroundLength(), rampModule.getRampPhysicalModel().getRamp().getLength(), getBlockPosition() );
        rampModule.getRampPhysicalModel().getBlock().addListener( new Block.Adapter() {
            public void positionChanged() {
                modelSlider.setValue( getBlockPosition() );
            }
        } );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setBlockPosition();
            }
        } );
        modelSlider.setModelTicks( new double[]{-getGroundLength(), 0, rampModule.getRampPhysicalModel().getRamp().getLength()} );
    }

    private double getGroundLength() {
        return rampModule.getRampPhysicalModel().getGround().getLength();
    }

    private void setBlockPosition() {
        if( modelSlider.getValue() >= 0 ) {
            rampModule.getBlock().setSurface( rampModule.getRampPhysicalModel().getRamp() );
            rampModule.getBlock().setPositionInSurface( modelSlider.getValue() );
        }
        else {
            double distAlongSurface = getGroundLength() + modelSlider.getValue();
//            System.out.println( "distAlongSurface = " + distAlongSurface );
            rampModule.getBlock().setSurface( rampModule.getRampPhysicalModel().getGround() );
            rampModule.getBlock().setPositionInSurface( distAlongSurface );
        }
    }

    private double getBlockPosition() {
        double val = rampModule.getRampPhysicalModel().getBlock().getPositionInSurface();
        if( rampModule.getBlock().getSurface() instanceof Ramp ) {
            return val;
        }
        else {
            return val - getGroundLength();
        }
    }

    public JComponent getComponent() {
        return modelSlider;
    }
}
