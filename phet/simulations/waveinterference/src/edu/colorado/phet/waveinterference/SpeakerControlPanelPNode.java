/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.phetcommon.ShinyPanel;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 2:07:24 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class SpeakerControlPanelPNode extends PNode {
    private Oscillator oscillator;
    private WaveModelGraphic waveModelGraphic;

    public SpeakerControlPanelPNode( PSwingCanvas pSwingCanvas, Oscillator oscillator, WaveModelGraphic waveModelGraphic ) {
        this.oscillator = oscillator;
        this.waveModelGraphic = waveModelGraphic;

        SpeakerControlPanel speakerControlPanel = new SpeakerControlPanel( oscillator );
//        addChild( new PSwing( pSwingCanvas, speakerControlPanel ) );
        PSwing speakerControlPSwing = new PSwing( pSwingCanvas, new ShinyPanel( speakerControlPanel ) );
        ResizeHandler.getInstance().setResizable( pSwingCanvas, speakerControlPSwing, 0.9 );
        addChild( speakerControlPSwing );

        waveModelGraphic.addPropertyChangeListener( "fullBounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
        updateLocation();
    }

    private void updateLocation() {
//        setOffset( waveModelGraphic.getFullBounds().getX(), waveModelGraphic.getLatticeScreenCoordinates().toScreenCoordinates( 0, waveModelGraphic.getWaveModel().getHeight() ).getY() );
//        setOffset( 0, waveModelGraphic.getLatticeScreenCoordinates().toScreenCoordinates( 0, waveModelGraphic.getWaveModel().getHeight() ).getY() );
        setOffset( 0, waveModelGraphic.getLatticeScreenCoordinates().getScreenRect().getMaxY() - getFullBounds().getHeight() );
    }

}
