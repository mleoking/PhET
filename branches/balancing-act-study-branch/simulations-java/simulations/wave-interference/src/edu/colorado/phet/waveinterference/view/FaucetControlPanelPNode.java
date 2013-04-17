// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 3:45:13 PM
 */

public class FaucetControlPanelPNode extends PNode {
    private FaucetGraphic faucetGraphic;
    private WaveModelGraphic waveModelGraphic;

    public FaucetControlPanelPNode( PhetPCanvas phetPCanvas, JComponent faucetControlPanel, final FaucetGraphic faucetGraphic, WaveModelGraphic waveModelGraphic ) {
        this.waveModelGraphic = waveModelGraphic;
        PSwing pSwing = new PSwing( faucetControlPanel );

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

        addChild( pSwing );
        updateLocation();
    }

    private void updateLocation() {
//        setOffset( faucetGraphic.getFullBounds().getX(), waveModelGraphic.getLatticeScreenCoordinates().toScreenCoordinates( 0, waveModelGraphic.getWaveModel().getHeight() ).getY() );
//        setOffset( 0, waveModelGraphic.getLatticeScreenCoordinates().toScreenCoordinates( 0, waveModelGraphic.getWaveModel().getHeight() ).getY() );
        setOffset( 0, waveModelGraphic.getLatticeScreenCoordinates().getScreenRect().getMaxY() - getFullBounds().getHeight() );
    }
}
