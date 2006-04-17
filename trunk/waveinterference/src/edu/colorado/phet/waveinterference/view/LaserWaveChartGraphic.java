/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Apr 16, 2006
 * Time: 8:51:43 PM
 * Copyright (c) Apr 16, 2006 by Sam Reid
 */

public class LaserWaveChartGraphic extends WaveChartGraphic {
    private VectorView vectorView;
    private PSwingCanvas pSwingCanvas;
    private LaserWaveChartControl laserWaveChartControl;

    public LaserWaveChartGraphic( PSwingCanvas pSwingCanvas, String title, LatticeScreenCoordinates latticeScreenCoordinates, WaveModel waveModel, MutableColor strokeColor ) {
        super( title, latticeScreenCoordinates, waveModel, strokeColor );

        this.pSwingCanvas = pSwingCanvas;
        vectorView = new VectorView();
        vectorView.setVisible( false );
        addChild( vectorView );
        updateChart();
        laserWaveChartControl = new LaserWaveChartControl( pSwingCanvas, this );
        addChild( laserWaveChartControl );
    }

    public boolean isVectorsVisible() {
        return vectorView.getVisible();
    }

    public void setVectorsVisible( boolean vectorsVisible ) {
        vectorView.setVisible( vectorsVisible );
        updateChart();
    }

    protected void updateLocation() {
        super.updateLocation();
        if( vectorView != null ) {
            vectorView.setOffset( getPathLocation() );
        }
        if( laserWaveChartControl != null ) {
            laserWaveChartControl.setOffset( getjFreeChartNode().getFullBounds().getMaxX(), getjFreeChartNode().getFullBounds().getCenterY() - laserWaveChartControl.getFullBounds().getHeight() / 2 );
        }
    }

    public void updateChart() {
        super.updateChart();
        updateVectorView();
    }

    private void updateVectorView() {
        if( vectorView != null ) {
            vectorView.update();
        }
    }

    protected void updateColor() {
        super.updateColor();
        updateVectorView();
    }

    public class VectorView extends PhetPNode {
        public VectorView() {
        }

        public void update() {
            removeAllChildren();
            Point2D[] pts = readValues();
            for( int i = 0; i < pts.length; i++ ) {
                Point2D pt = pts[i];
                addArrow( (float)pt.getX(), (float)pt.getY() );
            }
        }

        private void addArrow( float x, float y ) {
            double tailY = -y / 4;
            Arrow arrow = new Arrow( new Point2D.Double( x, tailY ), new Point2D.Double( x, y ), 8, 8, 4, 0.5, true );
            PPath arrowPath = new PPath( arrow.getShape() );
            arrowPath.setPaint( getStrokeColor().getColor() );
            addChild( arrowPath );
        }
    }
}
