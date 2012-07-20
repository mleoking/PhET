// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity.common;

import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

// Referenced classes of package edu.colorado.phet.semiconductor.common:
//            DoubleGeneralPath

public class ArrowShape {

    public ArrowShape( AbstractVector2D phetvector, AbstractVector2D phetvector1, double d, double d1, double d2 ) {
        direction = phetvector1.minus( phetvector ).getNormalizedInstance();
        double d3 = phetvector1.minus( phetvector ).getMagnitude();
        if ( d3 < d ) {
            throw new RuntimeException( "Head too big." );
        }
        else {
            norm = direction.getNormalVector();
            tipLocation = phetvector1;
            Vector2D phetvector2 = getPoint( -1D * d, -d1 / 2D );
            Vector2D phetvector3 = getPoint( -1D * d, d1 / 2D );
            Vector2D phetvector4 = getPoint( -1D * d, -d2 / 2D );
            Vector2D phetvector5 = getPoint( -1D * d, d2 / 2D );
            Vector2D phetvector6 = getPoint( -1D * d3, -d2 / 2D );
            Vector2D phetvector7 = getPoint( -1D * d3, d2 / 2D );
            DoubleGeneralPath doublegeneralpath = new DoubleGeneralPath( phetvector1.getX(), phetvector1.getY() );
            doublegeneralpath.lineTo( phetvector2 );
            doublegeneralpath.lineTo( phetvector4 );
            doublegeneralpath.lineTo( phetvector6 );
            doublegeneralpath.lineTo( phetvector7 );
            doublegeneralpath.lineTo( phetvector5 );
            doublegeneralpath.lineTo( phetvector3 );
            doublegeneralpath.lineTo( phetvector1.getX(), phetvector1.getY() );
            arrowPath = doublegeneralpath.getGeneralPath();
            return;
        }
    }

    private Vector2D getPoint( double d, double d1 ) {
        Vector2D phetvector = direction.times( d ).plus( norm.times( d1 ) );
        return tipLocation.plus( phetvector );
    }

    public GeneralPath getArrowPath() {
        return arrowPath;
    }

    GeneralPath arrowPath;
    AbstractVector2D tipLocation;
    private Vector2D direction;
    private Vector2D norm;
}
