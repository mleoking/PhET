// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.math.PhetVector;

import java.awt.*;
import java.awt.geom.GeneralPath;

// Referenced classes of package edu.colorado.phet.semiconductor.common:
//            DoubleGeneralPath

public class ArrowShape {

    public ArrowShape( PhetVector phetvector, PhetVector phetvector1, double d, double d1, double d2 ) {
        direction = phetvector1.getSubtractedInstance( phetvector ).getNormalizedInstance();
        double d3 = phetvector1.getSubtractedInstance( phetvector ).getMagnitude();
        if( d3 < d ) {
            throw new RuntimeException( "Head too big." );
        }
        else {
            norm = direction.getNormalVector();
            tipLocation = phetvector1;
            PhetVector phetvector2 = getPoint( -1D * d, -d1 / 2D );
            PhetVector phetvector3 = getPoint( -1D * d, d1 / 2D );
            PhetVector phetvector4 = getPoint( -1D * d, -d2 / 2D );
            PhetVector phetvector5 = getPoint( -1D * d, d2 / 2D );
            PhetVector phetvector6 = getPoint( -1D * d3, -d2 / 2D );
            PhetVector phetvector7 = getPoint( -1D * d3, d2 / 2D );
            DoubleGeneralPath doublegeneralpath = new DoubleGeneralPath( phetvector1 );
            doublegeneralpath.lineTo( phetvector2 );
            doublegeneralpath.lineTo( phetvector4 );
            doublegeneralpath.lineTo( phetvector6 );
            doublegeneralpath.lineTo( phetvector7 );
            doublegeneralpath.lineTo( phetvector5 );
            doublegeneralpath.lineTo( phetvector3 );
            doublegeneralpath.lineTo( phetvector1 );
            arrowPath = doublegeneralpath.getGeneralPath();
            return;
        }
    }

    private PhetVector getPoint( double d, double d1 ) {
        PhetVector phetvector = direction.getScaledInstance( d ).getAddedInstance( norm.getScaledInstance( d1 ) );
        PhetVector phetvector1 = tipLocation.getAddedInstance( phetvector );
        return phetvector1;
    }

    public GeneralPath getArrowPath() {
        return arrowPath;
    }

    GeneralPath arrowPath;
    PhetVector tipLocation;
    private PhetVector direction;
    private PhetVector norm;
}
