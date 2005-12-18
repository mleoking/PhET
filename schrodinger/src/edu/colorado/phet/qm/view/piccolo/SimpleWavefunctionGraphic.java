/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.colormaps.ColorGrid;
import edu.colorado.phet.qm.view.colormaps.ColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMapAdapter;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeInGrayscale3;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 12:48:33 PM
 * Copyright (c) Jun 30, 2005 by Sam Reid
 */

public class SimpleWavefunctionGraphic extends PNode {
    private ColorGridNode colorGridNode;
    private PPath borderGraphic;
    private Wavefunction wavefunction;
    private ColorMap colorMap;

    public SimpleWavefunctionGraphic( Wavefunction wavefunction ) {
        this.wavefunction = wavefunction;
        ColorGrid colorGrid = new ColorGrid( 4, 4, wavefunction.getWidth(), wavefunction.getHeight() );
        colorGridNode = new ColorGridNode( colorGrid );
        this.colorMap = new ComplexColorMapAdapter( wavefunction, new MagnitudeInGrayscale3() );
        addChild( colorGridNode );

        borderGraphic = new PPath( colorGridNode.getFullBounds() );
        borderGraphic.setStroke( new BasicStroke( 2 ) );
        borderGraphic.setStrokePaint( Color.white );
        addChild( borderGraphic );

        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                borderGraphic.setPathTo( colorGridNode.getFullBounds() );
            }
        };
        colorGridNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, pcl );
        colorGridNode.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, pcl );
        update();
    }

    public void setComplexColorMap( ComplexColorMap complexColorMap ) {
        setColorMap( new ComplexColorMapAdapter( getWavefunction(), complexColorMap ) );
        update();
    }

    public void setColorMap( ColorMap colorMap ) {
        this.colorMap = colorMap;
    }

    public void fullPaint( PPaintContext paintContext ) {
        Graphics2D g = paintContext.getGraphics();

        Object origAnt = g.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        Object origInt = g.getRenderingHint( RenderingHints.KEY_INTERPOLATION );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        super.fullPaint( paintContext );
        if( origAnt == null ) {
            origAnt = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
        }
        if( origInt == null ) {
            origInt = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        }
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, origAnt );
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, origInt );
    }

    public void update() {
        colorGridNode.setGridDimensions( wavefunction.getWidth(), wavefunction.getHeight() );
        colorGridNode.paint( colorMap );
        decorateBuffer();
        repaint();
    }

    public ColorGridNode getColorGridNode() {
        return colorGridNode;
    }

    protected void decorateBuffer() {
    }

    public Wavefunction getWavefunction() {
        return wavefunction;
    }

    public ColorGrid getColorGrid() {
        return getColorGridNode().getColorGrid();
    }

    public void setCellDimensions( int dx, int dy ) {
        colorGridNode.setCellDimensions( dx, dy );
        update();
    }

    public void setGridDimensions( int width, int height ) {
        colorGridNode.setGridDimensions( width, height );
        update();
    }
}
