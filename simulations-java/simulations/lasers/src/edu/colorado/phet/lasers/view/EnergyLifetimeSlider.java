// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.lasers.view;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.lasers.LasersConfig;

/**
 * EnergyLifetimeSlider
 * <p/>
 * A JSlider that is bound to a specific AtomicState. It controls the mean lifetime of the
 * state and displays in a location related to the energy level of that state.
 *
 * @author Ron LeMaster
 */
public class EnergyLifetimeSlider extends JSlider {
    
    private final static int MAX_SLIDER_WIDTH = 60;
    private final static int MIN_SLIDER_WIDTH = 45; // sliders that are too narrow are unusable on Mac, see #2190
    public final static int SLIDER_HEIGHT = 25; // this is just plain nasty, but too difficult to fix

    private final AtomicState atomicState;
    private final EnergyLevelGraphic graphic;
    private final Container container;
    private final int sliderWidth;
    
    private boolean enableNotification = true;

    public EnergyLifetimeSlider( final AtomicState atomicState, EnergyLevelGraphic graphic, int maxLifetime, int minLifetime, Container container ) {
        
        this.atomicState = atomicState;
        this.graphic = graphic;
        this.container = container;
        
        // slider properties
        setMinimum( minLifetime );
        setMaximum( maxLifetime );
        setMajorTickSpacing( maxLifetime - minLifetime );
        setPaintTicks( true );
        setBorder( new LineBorder( Color.BLACK, 1 ) ); // use a simple border to conserve space, see #2190
        putClientProperty( "JComponent.sizeVariant", "small" );  // use a smaller knob on Mac to address usability problem, see #2190
        
        // slider width, proportional to range
        sliderWidth = MIN_SLIDER_WIDTH + (int) ( ( MAX_SLIDER_WIDTH - MIN_SLIDER_WIDTH ) * ( (double) maxLifetime / LasersConfig.MAXIMUM_STATE_LIFETIME ) );

        // when the slider changes...
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateModel();
            }
        } );
        
        // when the atomic state changes...
        atomicState.addListener( new AtomicState.Listener() {
            
            public void energyLevelChanged( AtomicState.Event event ) {
                updateBounds();
                repaint();
            }

            public void meanLifetimeChanged( AtomicState.Event event ) {
                enableNotification = false;
                setValue( (int) event.getMeanLifetime() );
                enableNotification = true;
            }
        });
        
        // default state
        setValue( (int) atomicState.getMeanLifeTime() );
        updateModel();
        updateBounds();
    }

    public double getModelValue() {
        return atomicState.getMeanLifeTime();
    }
    
    // Positions the slider on the screen.
    // x location is relative to its container (the energy graph).
    // y location is vertically aligned with its associated energy level graphic.
    public void updateBounds() {
        this.setBounds( container.getWidth() - MAX_SLIDER_WIDTH - 2, (int) graphic.getPosition().getY(), sliderWidth, SLIDER_HEIGHT );
    }
    
    // Update the model to match the slider value.
    private void updateModel() {
        atomicState.setMeanLifetime( this.getValue() );
    }

    @Override
    protected void fireStateChanged() {
        if ( enableNotification ) {
            super.fireStateChanged();
        }
    }
}

