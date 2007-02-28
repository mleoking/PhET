/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 10:35:15 PM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class BrightnessScreenGraphic extends AbstractScreenGraphic {
    private ArrayList stripes = new ArrayList();
    private ColorMap colorMap;

    public BrightnessScreenGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, ColorMap colorMap ) {
        super( waveModel, latticeScreenCoordinates );
        this.colorMap = colorMap;
        update();
    }

    public void update() {
        removeAllStripes();
        for( int j = 0; j < getWaveModel().getHeight(); j++ ) {
            PPath path = new PPath() {
                protected void paint( PPaintContext paintContext ) {
                    paintContext.getGraphics().setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
                    paintContext.getGraphics().setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
                    super.paint( paintContext );
                }
            };
            fillPathFull( j, path );
            addBrightnessStripe( path );
            Color paint = getColor( j );
            path.setStroke( null );
            path.setPaint( paint );
        }
    }

    public Color getColor( int j ) {
        return colorMap.getColor( getWaveModel().getWidth() - 1, j );
    }

    private void fillPathFull( int j, PPath path ) {
        float y = getYValue( j );
        path.moveTo( 0 + getDx(), y - getDy() );
        path.lineTo( 0 + getDx(), y - getDy() + getCellHeight() + 1 );
        path.lineTo( -getDx(), y + getCellHeight() + 1 + getDy() );
        path.lineTo( -getDx(), y + getDy() );
        path.closePath();
    }

    private void removeAllStripes() {
        while( stripes.size() > 0 ) {
            removeStripe( (PPath)stripes.get( 0 ) );
        }
    }

    private void removeStripe( PPath path ) {
        removeChild( path );
        stripes.remove( path );
    }

    private void addBrightnessStripe( PPath path ) {
        addChild( path );
        stripes.add( path );
    }

    public void setColorMap( ColorMap colorMap ) {
        super.setColorMap( colorMap );
        this.colorMap = colorMap;
        update();
    }

    public ColorMap getColorMap() {
        return colorMap;
    }
}
