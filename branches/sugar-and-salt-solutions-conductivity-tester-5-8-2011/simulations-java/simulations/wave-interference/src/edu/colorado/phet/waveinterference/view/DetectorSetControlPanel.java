// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.phetcommon.IconComponent;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:16:55 PM
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
        JButton addDetector = new JButton( WIStrings.getString( "controls.add-detector" ) );
        addDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addIntensityReader( title );
            }
        } );
        add( new IconComponent( addDetector, getDetectorImage() ) );
    }

    private BufferedImage getDetectorImage() {
        try {
            return ImageLoader.loadBufferedImage( "wave-interference/images/detector-thumb.gif" );
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
