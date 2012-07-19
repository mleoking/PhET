// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.dev;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.lwjglphet.utils.GLActionListener;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;

/**
 * Developer control that allows setting the frame rate
 */
public class PerformanceFrame extends JFrame {

    public PerformanceFrame() throws HeadlessException {
        super( "Performance" );

        JPanel container = new JPanel( new GridBagLayout() );
        setContentPane( container );

        container.add( new JLabel( "Target FPS" ), new GridBagConstraints() );
        container.add( new FrameRateButton( 1024 ), new GridBagConstraints() );
        container.add( new FrameRateButton( 60 ), new GridBagConstraints() );
        container.add( new FrameRateButton( 20 ), new GridBagConstraints() );
        container.add( new FrameRateButton( 5 ), new GridBagConstraints() );

        pack();
        setVisible( true );
    }

    private class FrameRateButton extends JButton {
        public FrameRateButton( final int frameRate ) {
            super( frameRate + "" );

            addActionListener( new GLActionListener( new Runnable() {
                public void run() {
                    PlateTectonicsConstants.framesPerSecondLimit.set( frameRate );
                }
            } ) );
        }
    }
}
