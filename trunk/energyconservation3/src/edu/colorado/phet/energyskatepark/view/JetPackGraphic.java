/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.Body;

import java.io.IOException;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 12:54:19 PM
 * Copyright (c) May 26, 2006 by Sam Reid
 */

public class JetPackGraphic extends BodyGraphic {
    private Body origBody;

    public JetPackGraphic( EnergySkateParkModule ec3Module, Body body ) {
        super( ec3Module, body );
        this.origBody = body;
        debugCenter = true;
        try {
            setImage( ImageLoader.loadBufferedImage( "images/rocket5.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        update();
    }

    public void update() {
        if( origBody != null ) {
            Body b = origBody.copyState();
//            b.translate( );
            double dist = 0.3;
            AbstractVector2D vec = AbstractVector2D.Double.parseAngleAndMagnitude( dist, origBody.getAngle() + Math.PI / 2 );
//            b.translate( 0,1);
            b.translate( -vec.getX(), -vec.getY() );

            AbstractVector2D thrust = b.getThrust();
            setVisible( thrust.getMagnitude() != 0 );
//            double angle = System.currentTimeMillis() / 1000.0 * 0.5;
            b.setAngle( thrust.getAngle() + Math.PI / 2 );
            super.setBodyNoUpdate( b );
            super.update();
        }
    }
}
