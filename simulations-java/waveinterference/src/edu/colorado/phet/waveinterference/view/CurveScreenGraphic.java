/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 10:35:03 PM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class CurveScreenGraphic extends AbstractScreenGraphic {
    private PPath path;
    private PPath axis;
    private int intensityScale = 100;

    public int getIntensityScale() {
        return intensityScale;
    }

    public void setIntensityScale( int intensityScale ) {
        this.intensityScale = intensityScale;
    }

    public CurveScreenGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( waveModel, latticeScreenCoordinates );
        path = new PPath();
        path.setStroke( new BasicStroke( 1 ) );
        path.setStrokePaint( Color.black );
        axis = new PPath();
        axis.setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{40, 20}, 0 ) );
        axis.setStrokePaint( Color.black );
        addChild( axis );
        axis.setVisible( false );
        addChild( path );
        update();
    }

    public void update() {
        axis.setPathTo( new Line2D.Double( 0, getYValue( 0 ), 0, getYValue( getWaveModel().getHeight() ) ) );
        path.reset();
        for( int j = 0; j < getWaveModel().getHeight(); j++ ) {
            float y = getYValue( j );
//            double waveValue = getWaveModel().getValue( getWaveModel().getWidth() - 1, j );
            double waveValue = getWaveModel().getAverageValue( getWaveModel().getWidth() - 1, j, 2 );
            Vector2D.Double vec = new Vector2D.Double( getDx(), -getDy() );
            double mag = vec.getMagnitude();
            vec.normalize();

            vec.scale( waveValue * intensityScale );
            if( vec.getMagnitude() > mag ) {
                vec = new Vector2D.Double( getDx(), -getDy() );
                if( waveValue < 0 ) {
                    vec.scale( -1 );
                }
            }
            Point2D result = vec.getDestination( new Point2D.Double( 0, y ) );
            if( j == 0 ) {
                path.moveTo( (float)result.getX(), (float)result.getY() );
            }
            else {
                path.lineTo( (float)result.getX(), (float)result.getY() );
            }
        }
        path.setOffset( 0, super.getCellHeight() / 2.0 );
    }

}
