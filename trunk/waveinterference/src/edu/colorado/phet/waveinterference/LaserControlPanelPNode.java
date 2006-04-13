/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.view.LaserControlPanel;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 8:08:11 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class LaserControlPanelPNode extends PNode {
    private WaveModelGraphic waveModelGraphic;
    private Oscillator secondaryOscillator;
    private Oscillator primaryOscillator;

    public LaserControlPanelPNode( LightSimulationPanel lightSimulationPanel, WaveModelGraphic waveModelGraphic, final Oscillator oscillator, final Oscillator secondaryOscillator ) {
        this.primaryOscillator = oscillator;
        this.waveModelGraphic = waveModelGraphic;
        this.secondaryOscillator = secondaryOscillator;
        LaserControlPanel laserControlPanel = new LaserControlPanel( waveModelGraphic, oscillator );
        oscillator.addListener( new Oscillator.Adapter() {
            public void frequencyChanged() {
                updateFrequency();
            }
        } );
        PSwing pSwing = new PSwing( lightSimulationPanel, laserControlPanel );
        addChild( pSwing );
        waveModelGraphic.addPropertyChangeListener( "fullBounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
        waveModelGraphic.getLatticeScreenCoordinates().addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocation();
            }
        } );
        updateLocation();
        updateFrequency();
    }

    private void updateFrequency() {
        secondaryOscillator.setFrequency( primaryOscillator.getFrequency() );
    }

    private void updateLocation() {
        setOffset( waveModelGraphic.getLatticeScreenCoordinates().toScreenCoordinates( 0, 0 ).getX(), waveModelGraphic.getLatticeScreenCoordinates().toScreenCoordinates( 0, waveModelGraphic.getWaveModel().getHeight() ).getY() );
    }
}
