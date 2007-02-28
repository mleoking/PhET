/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.WaveModel;
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
 * Time: 8:16:55 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class DetectorSetControlPanel extends VerticalLayoutPanel {
    private IntensityReaderSet intensityReaderSet;
    private PhetPCanvas canvas;
    private WaveModel waveModel;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private IClock clock;

    public DetectorSetControlPanel( final String title, IntensityReaderSet intensityReaderSet, PhetPCanvas canvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, IClock clock ) {
        this.intensityReaderSet = intensityReaderSet;
        this.canvas = canvas;
        this.waveModel = waveModel;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.clock = clock;
        JButton addDetector = new JButton( WIStrings.getString( "add.detector" ) );
        addDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addIntensityReader( title );
            }
        } );
        add( new IconComponent( addDetector, getDetectorImage() ) );
    }

    private BufferedImage getDetectorImage() {
        try {
            return ImageLoader.loadBufferedImage( "images/detector-thumb.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public void addIntensityReader( String title ) {
        intensityReaderSet.addIntensityReader( title, canvas, waveModel, latticeScreenCoordinates, clock );
    }
}
