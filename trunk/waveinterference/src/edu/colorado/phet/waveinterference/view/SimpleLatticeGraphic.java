/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.waveinterference.model.Lattice2D;
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

public class SimpleLatticeGraphic extends PNode {
    private ColorGridNode colorGridNode;
    private PPath borderGraphic;
    private Lattice2D lattice2D;
    private ColorMap colorMap;

    public SimpleLatticeGraphic( Lattice2D lattice2D ) {
        this( lattice2D, 1, 1 );
    }

    public SimpleLatticeGraphic( Lattice2D lattice2D, int dx, int dy ) {
        this( lattice2D, dx, dy, new IndexColorMap( lattice2D ) );
    }

    public SimpleLatticeGraphic( Lattice2D lattice2D, int dx, int dy, ColorMap colorMap ) {
        this.lattice2D = lattice2D;
        colorGridNode = new ColorGridNode( new ColorGrid( dx, dy, lattice2D.getWidth(), lattice2D.getHeight() ) );
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

    public LatticeScreenCoordinates getLatticeScreenCoordinates( WaveModel waveModel ) {
        return new DefaultLatticeScreenCoordinates( waveModel, this );
    }

    public void setWavefunction( Lattice2D lattice2D ) {
        this.lattice2D = lattice2D;
        colorGridNode.setGridDimensions( lattice2D.getWidth(), lattice2D.getHeight() );
        update();
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
        colorGridNode.setGridDimensions( lattice2D.getWidth(), lattice2D.getHeight() );
        colorGridNode.paint( colorMap );
        decorateBuffer();
        repaint();
    }

    public ColorGridNode getColorGridNode() {
        return colorGridNode;
    }

    protected void decorateBuffer() {
    }

    public Lattice2D getWavefunction() {
        return lattice2D;
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
        ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, lattice2D.getWidth(), lattice2D.getHeight() ),
                                                                              new Rectangle2D.Double( 0, 0, getFullBounds().getWidth(), getFullBounds().getHeight() ) );
        Point2D val = modelViewTransform2D.viewToModel( localLocation );
        return new Point( (int)val.getX(), (int)val.getY() );
    }

    public Dimension getCellDimensions() {
        return new Dimension( colorGridNode.getCellWidth(), colorGridNode.getCellHeight() );
    }

    public void setColor( Color color ) {
        setColorMap( new IndexColorMap( lattice2D, color ) );
    }
}
