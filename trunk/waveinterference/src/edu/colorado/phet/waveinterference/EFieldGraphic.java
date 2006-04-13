/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 31, 2006
 * Time: 7:47:46 PM
 * Copyright (c) Mar 31, 2006 by Sam Reid
 */

public class EFieldGraphic extends AbstractVectorViewGraphic {

    public EFieldGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, int distBetweenSamples ) {
        super( waveModel, latticeScreenCoordinates, distBetweenSamples );
    }

    public void update() {
//        System.out.println( "EFieldGraphic.update" );
        super.update();
    }

    protected void addArrow( float x, float y ) {
        Arrow arrow = new Arrow( new Point2D.Double( x, 0 ), new Point2D.Double( x, y ), 8, 8, 4, 0.5, true );
        PPath arrowPath = new PPath( arrow.getShape() );
//        if (y>0){
        arrowPath.setPaint( Color.white );
//        }
        addChild( arrowPath );
    }
}
