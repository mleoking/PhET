// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:15:44 AM
 */
class Particle1DNode extends PNode {
    private Particle1D particle1d;
    private PhetPPath phetPPath;

    public Particle1DNode( final Particle1D particle1d ) {
        this.particle1d = particle1d;
        phetPPath = new PhetPPath( new BasicStroke( 0.01f ), Color.gray );
        phetPPath.setPathTo( new Ellipse2D.Double( 0, 0, 0.10, 0.10 ) );
        addChild( phetPPath );
        particle1d.addListener( this );

        update();
    }

    void update() {
        phetPPath.setOffset( particle1d.getX() - phetPPath.getWidth() / 2, particle1d.getY() - phetPPath.getHeight() / 2 );
    }
}
