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
import java.util.ArrayList;

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
    private ArrayList listeners = new ArrayList();

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
        addPropertyChangeListener( "fullBounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                notifyMappingChanged();
            }
        } );
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
        //todo selection for intensity color map
        this.colorMap = colorMap;
//        this.colorMap = new IntensityColorMap( getWaveModel(), new IndexColorMap( getWaveModel().getLattice(), colorMap.getRootColor(), new WaveValueReader.AverageAbs( 1 ) ) );
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
        Dimension origGridDim = colorGridNode.getGridDimensions();
        colorGridNode.setGridDimensions( waveModel.getWidth(), waveModel.getHeight() );
        colorGridNode.paint( colorMap );
        repaint();
        if( !origGridDim.equals( colorGridNode.getGridDimensions() ) ) {
            notifyMappingChanged();
        }
        notifyColorChanged();
    }

    private void notifyColorChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.colorMapChanged();
        }
    }

    private void notifyMappingChanged() {
        latticeScreenCoordinates.notifyMappingChanged();//todo this is unnecessarily expensive
    }

    public ColorGridNode getColorGridNode() {
        return colorGridNode;
    }

    public ColorGrid getColorGrid() {
        return getColorGridNode().getColorGrid();
    }

    public void setCellDimensions( int dx, int dy ) {
        colorGridNode.setCellDimensions( dx, dy );
        update();
        notifyMappingChanged();
    }

    public void setGridDimensions( int width, int height ) {
        colorGridNode.setGridDimensions( width, height );
        update();
        notifyMappingChanged();
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

//    public void setColor( Color color ) {
//        setColorMap( new IndexColorMap( waveModel.getLattice(), color ) );
//    }

    public void setBorderPaint( Color color ) {
        borderGraphic.setStrokePaint( color );
    }

    public ColorMap getColorMap() {
        return colorMap;
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }

    public static interface Listener {
        void colorMapChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
