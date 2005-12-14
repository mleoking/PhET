/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo.detectorscreen;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:43:24 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class SmoothIntensityDisplay {
    private SchrodingerPanel schrodingerPanel;
    private IntensityGraphic intensityGraphic;
    private double[] histogram;
    private Photon photon;
    private double brightness = 1.0f;

    public SmoothIntensityDisplay( SchrodingerPanel schrodingerPanel, IntensityGraphic intensityGraphic ) {
        this.schrodingerPanel = schrodingerPanel;
        this.intensityGraphic = intensityGraphic;
        histogram = new double[getWavefunction().getWidth()];
        schrodingerPanel.addListener( new SchrodingerPanel.Adapter() {
            //really, this is unnecessary now.
            public void fadeStateChanged() {
                synchronizeFadeState();
            }
        } );
    }

    private void synchronizeFadeState() {
    }

    private Wavefunction getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    private DiscreteModel getDiscreteModel() {
        return getSchrodingerPanel().getDiscreteModel();
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return this.intensityGraphic.getSchrodingerPanel();
    }

    public void updateValues() {

        Wavefunction sub = intensityGraphic.getDetectionRegion();
        if( histogram.length != sub.getWidth() ) {
            histogram = new double[sub.getWidth()];
        }
        for( int i = 0; i < sub.getWidth(); i++ ) {
            double sum = 0.0;
            for( int j = 0; j < sub.getHeight(); j++ ) {
                sum += sub.valueAt( i, j ).abs() * intensityGraphic.getProbabilityScaleFudgeFactor();
            }
            histogram[i] += sum;
        }
        paintSheet();
    }

    private void paintSheet() {
        BufferedImage sheet = intensityGraphic.getDetectorSheet().getBufferedImage();
        Graphics2D sheetGraphics = sheet.createGraphics();
        sheetGraphics.setColor( Color.white );
        sheetGraphics.fillRect( 0, 0, 1000, 1000 );
        Function.LinearFunction modelViewTx = intensityGraphic.getModelToViewTransform1d();
        if( isFadeEnabled() ) {
            fadeHistogram();
        }
        for( int i = 0; i < histogram.length; i++ ) {
            Color color = toColorBlackBackground( histogram[i] );
            int x = (int)( modelViewTx.evaluate( i ) * getWaveImageScaleX() );
            int x1 = (int)( modelViewTx.evaluate( i + 1 ) * getWaveImageScaleX() );
            sheetGraphics.setColor( color );
            sheetGraphics.fillRect( x, 0, x1 - x, 100 );
        }
        intensityGraphic.getDetectorSheet().repaint();
    }

    private boolean isFadeEnabled() {
        return getSchrodingerPanel().isFadeEnabled();
    }

    private double getWaveImageScaleX() {
        return getSchrodingerPanel().getWavefunctionGraphic().getWaveImageScaleX();
    }

    private Color toColorBlackBackground( double x ) {
        float v = (float)( x / 10.0 );
        v *= brightness;
        v = (float)MathUtil.clamp( 0, v, 1.0 );
        if( photon == null ) {
            return new Color( v * 0.8f, v * 0.8f, v );
        }
        else {
            return photon.getRootColor().toColor( v );
        }
    }

    private Color toColorLightBackground( double x ) {
        float v = (float)( x / 10.0 );
        v = (float)MathUtil.clamp( 0, v, 1.0 );
        return new Color( 1 - v, 1 - v, 1.0f );
    }

    private void fadeHistogram() {
        for( int i = 0; i < histogram.length; i++ ) {
            histogram[i] *= 0.9;
        }
    }

    private void normalizeHistogram() {
        double max = getMax();
        for( int i = 0; i < histogram.length; i++ ) {
            histogram[i] /= max;
        }
    }

    private double getMax() {
        double max = 0;
        for( int i = 0; i < histogram.length; i++ ) {
            if( histogram[i] > max ) {
                max = histogram[i];
            }
        }
        if( max < 1 ) {
            return 1;
        }
        return max;
    }

    public void reset() {
        for( int i = 0; i < histogram.length; i++ ) {
            histogram[i] = 0.0;
        }
        paintSheet();
    }

    public void setPhotonColor( Photon photon ) {
        this.photon = photon;
    }

    public void setBrightness( double brightness ) {
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 0.1, 0, 1 );
        this.brightness = linearFunction.evaluate( brightness );
    }

}
