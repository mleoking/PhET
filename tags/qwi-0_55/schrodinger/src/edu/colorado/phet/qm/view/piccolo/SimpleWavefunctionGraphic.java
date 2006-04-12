/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.colorgrid.ColorGrid;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMapAdapter;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeColorMap;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
        this( wavefunction, 1, 1 );
    }

    public SimpleWavefunctionGraphic( Wavefunction wavefunction, int dx, int dy ) {
        this( wavefunction, dx, dy, new ComplexColorMapAdapter( wavefunction, new MagnitudeColorMap() ) );
    }

    public SimpleWavefunctionGraphic( Wavefunction wavefunction, int dx, int dy, ColorMap colorMap ) {
        this.wavefunction = wavefunction;
        colorGridNode = new ColorGridNode( new ColorGrid( dx, dy, wavefunction.getWidth(), wavefunction.getHeight() ) );
        addChild( colorGridNode );

        this.colorMap = colorMap;

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

    public void setWavefunction( Wavefunction wavefunction ) {
        this.wavefunction = wavefunction;
        colorGridNode.setGridDimensions( wavefunction.getWidth(), wavefunction.getHeight() );
        update();
    }

    public void setWavefunction( Wavefunction wavefunction, ComplexColorMap complexColorMap ) {
        this.wavefunction = wavefunction;
        this.colorMap = new ComplexColorMapAdapter( wavefunction, complexColorMap );
        update();
    }

    public void setComplexColorMap( ComplexColorMap complexColorMap ) {
        setColorMap( new ComplexColorMapAdapter( getWavefunction(), complexColorMap ) );
    }

    public void setColorMap( ColorMap colorMap ) {
        this.colorMap = colorMap;
        update();
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

    public Point getGridCoordinates( Point2D localLocation ) {
        ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, wavefunction.getWidth(), wavefunction.getHeight() ),
                                                                              new Rectangle2D.Double( 0, 0, getFullBounds().getWidth(), getFullBounds().getHeight() ) );
        Point2D val = modelViewTransform2D.viewToModel( localLocation );
        return new Point( (int)val.getX(), (int)val.getY() );
    }

    public Dimension getCellDimensions() {
        return new Dimension( colorGridNode.getCellWidth(), colorGridNode.getCellHeight() );
    }

}
