// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.model.Beaker;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This node manages the precipitate that forms on the bottom of the beaker when the solution is saturated.
 * Origin is at bottom center of the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateNode extends PComposite {

    private final Solution solution;
    private final Beaker beaker;
    private final ArrayList<PrecipitateParticleNode> particleNodes;

    public PrecipitateNode( Solution solution, Beaker beaker ) {
        this.solution = solution;
        this.beaker = beaker;
        this.particleNodes = new ArrayList<PrecipitateParticleNode>();

        // when the saturation changes, update the number of precipitate particles
        solution.addPrecipitateAmountObserver( new SimpleObserver() {
            public void update() {
                updateParticles();
            }
        } );

        // when the solute changes, remove all particles and create new particles for the solute
        solution.solute.addObserver( new SimpleObserver() {
            public void update() {
                removeAllParticles();
                updateParticles();
            }
        } );
    }

    private void removeAllParticles() {
        for ( PrecipitateParticleNode particleNode : particleNodes ) {
            removeChild( particleNode );
        }
        particleNodes.clear();
    }

    private void updateParticles() {
        int numberOfParticles = solution.getNumberOfPrecipitateParticles();
        if ( numberOfParticles == 0 ) {
            removeAllParticles();
        }
        else if ( numberOfParticles > particleNodes.size() ) {
            // add particles
            while ( numberOfParticles > particleNodes.size() ) {
                PrecipitateParticleNode particleNode = new PrecipitateParticleNode( solution.solute.get() );
                addChild( particleNode );
                particleNodes.add( particleNode );

                particleNode.setOffset( getRandomOffset( particleNode ) );
            }
        }
        else {
            // remove particles
            while ( numberOfParticles < particleNodes.size() ) {
                PrecipitateParticleNode particleNode = particleNodes.get( particleNodes.size() - 1 );
                removeChild( particleNode );
                particleNodes.remove( particleNode );
            }
        }
    }

    // Gets a random position on the bottom of the beaker.
    private Point2D getRandomOffset( PrecipitateParticleNode particleNode ) {
        // x offset
        double xMargin = particleNode.getFullBoundsReference().getWidth();
        double width = beaker.getWidth() - particleNode.getFullBoundsReference().getWidth() - ( 2 * xMargin );
        double x = xMargin + ( Math.random() * width ) - ( beaker.getWidth() / 2 );
        // y offset
        double yMargin = particleNode.getFullBoundsReference().getHeight();
        double y = -yMargin;
        // offset
        return new Point2D.Double( x, y );
    }
}
