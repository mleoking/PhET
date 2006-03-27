/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
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

public class WaveModelGraphic extends PNode {
    private ColorGridNode colorGridNode;
    private PPath borderGraphic;
    private ColorMap colorMap;
    private WaveModel waveModel;
    private LatticeScreenCoordinates latticeScreenCoordinates;

    public WaveModelGraphic( WaveModel waveModel ) {
        this( waveModel, 1, 1 );
    }

    public WaveModelGraphic( WaveModel waveModel, int dx, int dy ) {
        this( waveModel, dx, dy, new IndexColorMap( waveModel.getLattice() ) );
    }

    public WaveModelGraphic( WaveModel waveModel, int dx, int dy, ColorMap colorMap ) {
        this.waveModel = waveModel;
        colorGridNode = new ColorGridNode( new ColorGrid( dx, dy, waveModel.getWidth(), waveModel.getHeight() ) );
        latticeScreenCoordinates = new WaveGraphicCoordinates( waveModel, this );
        addChild( colorGridNode );

        this.colorMap = colorMap;

        borderGraphic = new PPath( colorGridNode.getFullBounds() );
        borderGraphic.setStroke( new BasicStroke( 2 ) );
        borderGraphic.setStrokePaint( Color.darkGray );
        addChild( borderGraphic );

        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                borderGraphic.setPathTo( colorGridNode.getFullBounds() );
                notifyMappingChanged();
            }
        };
        colorGridNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, pcl );
        colorGridNode.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, pcl );
        waveModel.addListener( new WaveModel.Listener() {
            public void sizeChanged() {
                update();
            }
        } );
        update();
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return latticeScreenCoordinates;
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
        colorGridNode.setGridDimensions( waveModel.getWidth(), waveModel.getHeight() );
        colorGridNode.paint( colorMap );
        decorateBuffer();
        repaint();
        notifyMappingChanged();
    }

    private void notifyMappingChanged() {
        latticeScreenCoordinates.notifyMappingChanged();
    }

    public ColorGridNode getColorGridNode() {
        return colorGridNode;
    }

    protected void decorateBuffer() {
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
        ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, waveModel.getWidth(), waveModel.getHeight() ),
                                                                              new Rectangle2D.Double( 0, 0, getFullBounds().getWidth(), getFullBounds().getHeight() ) );
        Point2D val = modelViewTransform2D.viewToModel( localLocation );
        return new Point( (int)val.getX(), (int)val.getY() );
    }

    public Dimension getCellDimensions() {
        return new Dimension( colorGridNode.getCellWidth(), colorGridNode.getCellHeight() );
    }

    public void setColor( Color color ) {
        setColorMap( new IndexColorMap( waveModel.getLattice(), color ) );
    }

    public void setBorderPaint( Color color ) {
        borderGraphic.setStrokePaint( color );
    }

    public ColorMap getColorMap() {
        return colorMap;
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }
}
