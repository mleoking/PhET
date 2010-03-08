/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.lasers.LasersConfig;
import edu.colorado.phet.lasers.view.util.DefaultGridBagConstraints;

/**
 * EnergyLifetimeSlider
 * <p/>
 * A JSlider that is bound to a specific AtomicState. It controls the mean lifetime of the
 * state and displays in a location related to the energy level of that state.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyLifetimeSlider extends JSlider implements AtomicState.Listener {
    
    private final static int MAX_SLIDER_WIDTH = 60;
    public final static int SLIDER_HEIGHT = 20;

    private EnergyLevelGraphic graphic;
    private int sliderWidth;
    private Container container;
    private boolean enableNotification = true;
    private AtomicState atomicState;

    public EnergyLifetimeSlider( final AtomicState atomicState, EnergyLevelGraphic graphic, int maxLifetime, int minLifetime, Container container ) {
//        putClientProperty( "JComponent.sizeVariant", "mini" ); // see Unfuddle #2190
        this.container = container;
        this.atomicState = atomicState;
        atomicState.addListener( this );
        setMinimum( minLifetime );
        setMaximum( maxLifetime );
        int sliderWidthPadding = 20;
        sliderWidth = (int) ( (double) ( MAX_SLIDER_WIDTH - sliderWidthPadding ) * ( (double) getMaximum() / LasersConfig.MAXIMUM_STATE_LIFETIME ) ) + sliderWidthPadding;
        sliderWidth = Math.min( sliderWidth, MAX_SLIDER_WIDTH );
        setValue( maxLifetime / 2 );
        setMajorTickSpacing( maxLifetime );
        setMinorTickSpacing( maxLifetime );
        setPaintTicks( true );
//        setPaintTicks();
        this.graphic = graphic;

        setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        setValue( (int) atomicState.getMeanLifeTime() );

        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setModelValue();
            }
        } );
        setModelValue();

        setBorder( new BevelBorder( BevelBorder.RAISED ) );
        update();
    }

    private void setModelValue() {
        atomicState.setMeanLifetime( this.getValue() );
    }

    /**
     * Positions the slider on the screen
     */
    public void update() {
        this.setBounds( container.getWidth() - MAX_SLIDER_WIDTH, (int) graphic.getPosition().getY(), sliderWidth, SLIDER_HEIGHT );
    }

    protected void fireStateChanged() {
        if ( enableNotification ) {
            super.fireStateChanged();
        }
    }

    public void energyLevelChanged( AtomicState.Event event ) {
        update();
        repaint();
    }

    public void meanLifetimechanged( AtomicState.Event event ) {
        enableNotification = false;
        this.setValue( (int) event.getMeanLifetime() );
//        System.out.println( "event = " + event+", ML="+event.getMeanLifetime()+", V="+getValue() );
//        if (Math.abs(event.getMeanLifetime()-getValue())>10){
//            System.out.println( "EnergyLifetimeSlider.meanLifetimechanged" );
//        }
        enableNotification = true;
    }

    public double getModelValue() {
        return atomicState.getMeanLifeTime();
    }
}

