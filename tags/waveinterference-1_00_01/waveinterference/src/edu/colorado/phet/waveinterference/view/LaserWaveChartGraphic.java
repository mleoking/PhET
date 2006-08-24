/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
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
    private boolean colorized = true;

    public LaserWaveChartGraphic( PSwingCanvas pSwingCanvas, String title, LatticeScreenCoordinates latticeScreenCoordinates, WaveModel waveModel, MutableColor strokeColor, String distanceUnits, double minX, double maxX ) {
        super( title, latticeScreenCoordinates, waveModel, strokeColor, distanceUnits, minX, maxX );

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

    protected double getChartOffset() {
        return 17;
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

    public void setColorized( boolean colorized ) {
        this.colorized = colorized;
        super.setColorized( colorized );
        updateColor();
    }

    public boolean getColorized() {
        return colorized;
    }

    public void reset() {
        laserWaveChartControl.reset();
    }

    public class VectorView extends PhetPNode {
        private boolean indicateOneBlackVector = false;

        public VectorView() {
        }

        public void update() {
            removeAllChildren();
            Point2D[] pts = readValues();
//            int stride=1;//original
            int stride = 3;
            for( int i = 0; i < pts.length; i += stride ) {
                Point2D pt = pts[i];
                addArrow( (float)pt.getX(), (float)pt.getY() );
//                if( i == pts.length / (4*3) ) {
                if( indicateOneBlackVector ) {
                    if( i == pts.length / 4 ) {
                        PPath path = (PPath)getChildrenReference().get( getChildrenReference().size() - 1 );
                        path.setPaint( Color.black );
                    }
                }
            }
        }

        private void addArrow( float x, float y ) {
//            double tailY = -y / 4;//not at zero
            double tailY = 0;//tail at zero.
            Arrow arrow = new Arrow( new Point2D.Double( x, tailY ), new Point2D.Double( x, y ), 8, 8, 4, 0.5, true );
            PPath arrowPath = new PPath( arrow.getShape() );
//            arrowPath.setPaint( getStrokeColor().getColor() );
            arrowPath.setStrokePaint( getColor() );
            arrowPath.setStroke( new BasicStroke( 2 ) );
            addChild( arrowPath );
        }

        private Color getColor() {
            return colorized ? getStrokeColor().getColor() : Color.black;
        }
    }
}
