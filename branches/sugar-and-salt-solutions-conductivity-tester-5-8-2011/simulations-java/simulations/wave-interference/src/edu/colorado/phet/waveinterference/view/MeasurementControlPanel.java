// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.phetcommon.IconComponent;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:22:47 PM
 */

public class MeasurementControlPanel extends VerticalLayoutPanel {
    private MeasurementToolSet measurementToolSet;

    public MeasurementControlPanel( final MeasurementToolSet measurementToolSet ) {
        this.measurementToolSet = measurementToolSet;
//        setBorder( BorderFactory.createTitledBorder( "Tools" ) );

        final JCheckBox measuringTape = new JCheckBox( WIStrings.getString( "controls.measuring-tape" ), measurementToolSet.isMeasuringTapeVisible() );
        measuringTape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                measurementToolSet.setMeasuringTapeVisible( measuringTape.isSelected() );
            }
        } );
        add( new IconComponent( measuringTape, getTapeIcon() ) );


        final JCheckBox stopwatch = new JCheckBox( WIStrings.getString( "controls.stopwatch" ), measurementToolSet.isStopwatchVisible() );
        stopwatch.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                measurementToolSet.setStopwatchVisible( stopwatch.isSelected() );
            }
        } );
        add( new IconComponent( stopwatch, getClockThumb() ) );

        measurementToolSet.addListener( new MeasurementToolSet.Listener() {
            public void toolVisibilitiesChanged() {
                measuringTape.setSelected( measurementToolSet.isMeasuringTapeVisible() );
                stopwatch.setSelected( measurementToolSet.isStopwatchVisible() );
            }
        } );
    }

    private BufferedImage getClockThumb() {
        try {
            return ImageLoader.loadBufferedImage( "wave-interference/images/stopwatch-thumb.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage getTapeIcon() {
        try {
            return ( ImageLoader.loadBufferedImage( "wave-interference/images/ruler-thumb.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }
}
