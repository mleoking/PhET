/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.waveinterference.view.FaucetControlPanel;
import edu.colorado.phet.waveinterference.view.FaucetGraphic;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 3:45:13 PM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class FaucetControlPanelPNode extends PNode {
    private FaucetGraphic faucetGraphic;
    private WaveModelGraphic waveModelGraphic;

    public FaucetControlPanelPNode( PhetPCanvas phetPCanvas, FaucetControlPanel faucetControlPanel, final FaucetGraphic faucetGraphic, WaveModelGraphic waveModelGraphic ) {
        this.waveModelGraphic = waveModelGraphic;
        PSwing pSwing = new PSwing( phetPCanvas, faucetControlPanel );
        addChild( pSwing );
        this.faucetGraphic = faucetGraphic;
        faucetGraphic.addPropertyChangeListener( "fullBounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
        waveModelGraphic.addPropertyChangeListener( "fullBounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLocation();
            }
        } );
        updateLocation();
    }

    private void updateLocation() {
        setOffset( faucetGraphic.getFullBounds().getX(), waveModelGraphic.getLatticeScreenCoordinates().toScreenCoordinates( 0, waveModelGraphic.getWaveModel().getHeight() ).getY() );
    }
}
