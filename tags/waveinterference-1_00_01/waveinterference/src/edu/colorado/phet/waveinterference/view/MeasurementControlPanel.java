/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.phetcommon.IconComponent;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:22:47 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MeasurementControlPanel extends VerticalLayoutPanel {
    private MeasurementToolSet measurementToolSet;

    public MeasurementControlPanel( final MeasurementToolSet measurementToolSet ) {
        this.measurementToolSet = measurementToolSet;
//        setBorder( BorderFactory.createTitledBorder( "Tools" ) );

        final JCheckBox measuringTape = new JCheckBox( WIStrings.getString( "measuring.tape" ), measurementToolSet.isMeasuringTapeVisible() );
        measuringTape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                measurementToolSet.setMeasuringTapeVisible( measuringTape.isSelected() );
            }
        } );
        add( new IconComponent( measuringTape, getTapeIcon() ) );


        final JCheckBox stopwatch = new JCheckBox( WIStrings.getString( "stopwatch" ), measurementToolSet.isStopwatchVisible() );
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
            return ImageLoader.loadBufferedImage( "images/stopwatch-thumb.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage getTapeIcon() {
        try {
            return ( ImageLoader.loadBufferedImage( "images/ruler-thumb.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }
}
