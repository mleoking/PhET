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
import edu.colorado.phet.lasers.controller.LaserConfig;
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
    // Needs to be accessible to the EnergyLevelGraphic class
    //    public final static int sliderHeight = 47;
    public final static int sliderHeight = 20;

    private EnergyLevelGraphic graphic;
    private int maxSliderWidth = 60;
    private int sliderWidthPadding = 20;
    private int sliderWidth;
    private Container container;
    private boolean enableNotification = true;

    /**
     * @param atomicState
     * @param graphic
     * @param maxLifetime
     * @param minLifetime
     * @param container
     */
    public EnergyLifetimeSlider( final AtomicState atomicState, EnergyLevelGraphic graphic,
                                 int maxLifetime, int minLifetime, Container container ) {
        this.container = container;
        atomicState.addListener( this );
        setMinimum( minLifetime );
        setMaximum( maxLifetime );
        sliderWidth = (int) ( (double) ( maxSliderWidth - sliderWidthPadding ) * ( (double) getMaximum() / LaserConfig.MAXIMUM_STATE_LIFETIME ) ) + sliderWidthPadding;
        sliderWidth = Math.min( sliderWidth, maxSliderWidth );
        setValue( maxLifetime / 2 );
        setMajorTickSpacing( maxLifetime );
        setMinorTickSpacing( maxLifetime / 10 );
//            setPaintTicks( true );
//            setPaintLabels( true );
//            setPaintTrack( true );
        this.graphic = graphic;

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
//        this.add( new JLabel( SimStrings.getInstance().getString( "EnergyLevelMonitorPanel.sliderLabel" ), JLabel.CENTER ), gbc );
        setValue( (int) atomicState.getMeanLifeTime() );

        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                atomicState.setMeanLifetime( EnergyLifetimeSlider.this.getValue() );
            }
        } );


        this.setBorder( new BevelBorder( BevelBorder.RAISED ) );
        update();
    }

    /**
     * Positions the slider on the screen
     */
    public void update() {
        this.setBounds( container.getWidth() - maxSliderWidth,
                        (int) graphic.getPosition().getY(),
                        sliderWidth, sliderHeight );
    }

    public void setValue( int n ) {
        super.setValue( n );
    }

    protected void fireStateChanged() {
        if ( enableNotification ) {
            super.fireStateChanged();
        }
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void energyLevelChanged( AtomicState.Event event ) {
        update();
        repaint();
    }

    public void meanLifetimechanged( AtomicState.Event event ) {
        enableNotification = false;
        this.setValue( (int) event.getMeanLifetime() );
        enableNotification = true;
    }

    public void meanLifetimeChanged( AtomicState.MeanLifetimeChangeEvent event ) {
        enableNotification = false;
        this.setValue( (int) event.getMeanLifetime() );
        enableNotification = true;
    }
}

