/**
 * Class: WaveMediumView
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf.view;

//import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.waves.model.WaveMedium;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

public class WaveMediumView implements Observer {
//public class WaveMediumView implements ObservingGraphic {

    private Rectangle2D.Double viewArea;
    private double gridSpacing;
    private double[] sampleValues;
    private int numValues;

    public WaveMediumView( Rectangle2D.Double viewArea, double gridSpacing ) {
        this.viewArea = viewArea;
        this.gridSpacing = gridSpacing;
        numValues = (int)( viewArea.getWidth() / gridSpacing );
        sampleValues = new double[numValues];
    }

    public void paint( Graphics2D g2 ) {
        for( int j = 0; j < 20; j++ ) {
            for( int i = 0; i < numValues; i++ ) {
                g2.drawArc( (int)( viewArea.getMinX() + i * gridSpacing ),
                            100 + j * 30, 3, 3, 0, 360 );
                g2.drawLine( (int)( viewArea.getMinX() + i * gridSpacing ), 100 + j * 30,
                             (int)( viewArea.getMinX() + i * gridSpacing ) /*+ 10*/, 100  + j * 30 + (int)sampleValues[i] );
            }
        }
    }

    public void update( Observable o, Object arg ) {
        WaveMedium waveMedium = (WaveMedium)o;
        double sampleSpacing = ( waveMedium.getMaxX() / numValues );
        for( int i = 0; i < numValues; i++ ) {
            sampleValues[i] = 15 * waveMedium.getAmplitudeAt( (float)( i * sampleSpacing ), 0 );
        }
    }
}
