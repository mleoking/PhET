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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.AtomicState;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * EnergyLifetimeSlider
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyLifetimeSlider extends JSlider implements AtomicState.Listener {
    // Needs to be accessible to the EnergyLevelGraphic class
    public final static int sliderHeight = 47;

    private EnergyLevelGraphic graphic;
    private int maxSliderWidth = 100;
    private int sliderWidthPadding = 20;
    private int sliderWidth;
    private Container container;

    public EnergyLifetimeSlider( final AtomicState atomicState, EnergyLevelGraphic graphic, int maxLifetime, Container container ) {
        super();
        this.container = container;
        atomicState.addListener( this );
        setMinimum( 0 );
        setMaximum( maxLifetime );
        sliderWidth = (int)( (double)( maxSliderWidth - sliderWidthPadding ) * ( (double)getMaximum() / LaserConfig.MAXIMUM_STATE_LIFETIME ) ) + sliderWidthPadding;
        sliderWidth = Math.min( sliderWidth, maxSliderWidth );
        setValue( maxLifetime / 2 );
        setMajorTickSpacing( maxLifetime );
        setMinorTickSpacing( maxLifetime / 10 );
//            setPaintTicks( true );
//            setPaintLabels( true );
//            setPaintTrack( true );
        this.graphic = graphic;
        this.setLayout( new BorderLayout() );
        this.add( new JLabel( SimStrings.get( "EnergyLevelMonitorPanel.sliderLabel" ), JLabel.CENTER ), BorderLayout.NORTH );

        this.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                atomicState.setMeanLifetime( EnergyLifetimeSlider.this.getValue() );
            }
        } );
        this.setEnabled( false );
        setValue( (int)atomicState.getMeanLifeTime() );
        this.setEnabled( true );

        this.setBorder( new BevelBorder( BevelBorder.RAISED ) );
        update();
    }

    /**
     * Positions the slider on the screen
     */
    public void update() {
        this.setBounds( (int)( container.getWidth() - maxSliderWidth ),
                        (int)graphic.getPosition().getY(),
                        sliderWidth, sliderHeight );
    }

    public void energyLevelChanged( AtomicState.Event event ) {
        // noop
    }

    public void meanLifetimechanged( AtomicState.Event event ) {
        this.setEnabled( false );
        this.setValue( (int)event.getMeanLifetime() );
        this.setEnabled( true );
    }

    public void meanLifetimeChanged( AtomicState.MeanLifetimeChangeEvent event ) {
        this.setEnabled( false );
        this.setValue( (int)event.getMeanLifetime() );
        this.setEnabled( true );
    }
}

