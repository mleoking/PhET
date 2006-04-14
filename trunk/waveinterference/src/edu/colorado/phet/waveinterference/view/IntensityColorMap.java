/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 5:19:04 AM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class IntensityColorMap implements ColorMap {
    private WaveModel waveModel;
    private ColorMap colorMap;
//    private MultiMap multiMap = new MultiMap();
    private ArrayList[][] history = new ArrayList[0][0];
    private int maxHistory = 100;
    private double brightness = 3.5 * 2;

    public IntensityColorMap( WaveModel waveModel, ColorMap colorMap ) {
        this.waveModel = waveModel;
        this.colorMap = colorMap;
        waveModel.addListener( new WaveModel.Listener() {
            public void sizeChanged() {
                updateSize();
            }
        } );
        updateSize();
    }

    private void updateSize() {
        history = new ArrayList[waveModel.getWidth()][waveModel.getHeight()];
    }

    public Color getColor( int i, int k ) {
        if( history[i][k] == null ) {
            history[i][k] = new ArrayList();
        }
        Color c = colorMap.getColor( i, k );
        history[i][k].add( c );
        if( history[i][k].size() > maxHistory ) {
            history[i][k].remove( 0 );
        }
        ColorVector colorVector = new ColorVector( 0, 0, 0 );
        for( int j = 0; j < history[i][k].size(); j++ ) {
            colorVector = colorVector.add( new ColorVector( (Color)history[i][k].get( j ) ) );
        }
        colorVector = colorVector.scale( (float)( brightness / history[i][k].size() ) );
        return colorVector.toColor();
    }

    public Color getRootColor() {
        return colorMap.getRootColor();
    }

    public void setColorMap( ColorMap colorMap ) {
        this.colorMap = colorMap;
    }
}
