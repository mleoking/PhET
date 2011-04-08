// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.MultiStateButton;
import edu.colorado.phet.waveinterference.view.RotationWaveGraphic;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 5, 2007
 * Time: 9:40:41 PM
 */
public class WaveSizeButton extends MultiStateButton {
    private RotationWaveGraphic rotationWaveGraphic;
    private Maximizable maximizable;
    private String BIG_KEY = "big";
    private String SMALL_KEY = "small";

    public WaveSizeButton( RotationWaveGraphic rotationWaveGraphic, final Maximizable maximizable ) {
        this.rotationWaveGraphic = rotationWaveGraphic;
        this.maximizable = maximizable;
        if ( !PhetUtilities.isMacintosh() ) {
            setMargin( new Insets( 0, 0, 0, 0 ) );
        }

        addMode( BIG_KEY, null, new ImageIcon( PhetCommonResources.getMaximizeButtonImage() ) );
        addActionListener( BIG_KEY, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                maximizable.setWaveMaximized( true );
            }
        } );
        addMode( SMALL_KEY, null, new ImageIcon( PhetCommonResources.getMinimizeButtonImage() ) );
        addActionListener( SMALL_KEY, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                maximizable.setWaveMaximized( false );

            }
        } );
    }
}
