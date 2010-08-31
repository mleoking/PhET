/* Copyright 2003-2010, University of Colorado */

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
 */
public class EnergyLifetimeSlider extends JSlider implements AtomicState.Listener {
    
    private final static int MAX_SLIDER_WIDTH = 60;
    public final static int SLIDER_HEIGHT = 20;
    private static final int SLIDER_WIDTH_PADDING = 20;

    private final AtomicState atomicState;
    private final EnergyLevelGraphic graphic;
    private final Container container;
    
    private int sliderWidth;
    private boolean enableNotification = true;

    public EnergyLifetimeSlider( final AtomicState atomicState, EnergyLevelGraphic graphic, int maxLifetime, int minLifetime, Container container ) {
        
        this.atomicState = atomicState;
        this.graphic = graphic;
        this.container = container;
        
        // slider properties
        setMinimum( minLifetime );
        setMaximum( maxLifetime );
        setValue( maxLifetime / 2 );
        setMajorTickSpacing( maxLifetime );
        setMinorTickSpacing( maxLifetime );
        setPaintTicks( true );
        setBorder( new BevelBorder( BevelBorder.RAISED ) );
        
        // slider width, proportional to range
        sliderWidth = (int) ( (double) ( MAX_SLIDER_WIDTH - SLIDER_WIDTH_PADDING ) * ( (double) getMaximum() / LasersConfig.MAXIMUM_STATE_LIFETIME ) ) + SLIDER_WIDTH_PADDING;
        sliderWidth = Math.min( sliderWidth, MAX_SLIDER_WIDTH );

        // listeners
        atomicState.addListener( this );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setModelValue();
            }
        } );
        
        // layout
        setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        setValue( (int) atomicState.getMeanLifeTime() );
        
        // default state
        setModelValue();
        update();
    }

    private void setModelValue() {
        atomicState.setMeanLifetime( this.getValue() );
    }
    
    public double getModelValue() {
        return atomicState.getMeanLifeTime();
    }

    // Positions the slider on the screen, relative to its container
    public void update() {
        this.setBounds( container.getWidth() - MAX_SLIDER_WIDTH, (int) graphic.getPosition().getY(), sliderWidth, SLIDER_HEIGHT );
    }

    @Override
    protected void fireStateChanged() {
        if ( enableNotification ) {
            super.fireStateChanged();
        }
    }

    // implements AtomicState.Listener
    public void energyLevelChanged( AtomicState.Event event ) {
        update();
        repaint();
    }

    // implements AtomicState.Listener
    public void meanLifetimeChanged( AtomicState.Event event ) {
        enableNotification = false;
        this.setValue( (int) event.getMeanLifetime() );
        enableNotification = true;
    }
}

