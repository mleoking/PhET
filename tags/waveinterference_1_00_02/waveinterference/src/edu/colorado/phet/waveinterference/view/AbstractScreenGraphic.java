/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 10:34:49 PM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class AbstractScreenGraphic extends PNode {
    private WaveModel waveModel;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private PPath path;
    private float dx = 50;
    private float dy = 30;
    private float cellHeight = 0;

    public AbstractScreenGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.waveModel = waveModel;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        path = new PPath();
        addChild( path );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateBounds();
            }
        } );
        updateBounds();
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public void update() {
    }

    private void updateBounds() {
        Point2D topRight = latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth() + 1, 0 );
        Point2D bottomRight = latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth() + 1, waveModel.getHeight() );
        float latticeGraphicHeight = (float)( bottomRight.getY() - topRight.getY() );
        path.reset();
        path.moveTo( 0, 0 );
        path.lineTo( dx, -dy );
        path.lineTo( dx, latticeGraphicHeight - dy );
        path.lineTo( -dx, latticeGraphicHeight + dy );
        path.lineTo( -dx, dy );
        path.closePath();
        path.setStroke( new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        path.setPaint( Color.white );
        path.setStrokePaint( Color.black );

//        setOffset( getWaveModelGraphic().getFullBounds().getMaxX(), getWaveModelGraphic().getFullBounds().getY() );
        setOffset( latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth(), 0 ) );
        this.cellHeight = computeCellHeight();
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return latticeScreenCoordinates;
    }

    public float getYValue( int latticeY ) {
        return (float)latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth() + 1, latticeY ).getY() - getOffsetY();
    }

    private float getOffsetY() {
        return (float)latticeScreenCoordinates.toScreenCoordinates( 0, 0 ).getY();
    }

    public void setColorMap( ColorMap colorMap ) {
    }

    private float computeCellHeight() {
        return (float)( latticeScreenCoordinates.toScreenCoordinates( 0, 1 ).getY() - latticeScreenCoordinates.toScreenCoordinates( 0, 0 ).getY() );
    }

    public float getCellHeight() {
        return cellHeight;
    }
}
