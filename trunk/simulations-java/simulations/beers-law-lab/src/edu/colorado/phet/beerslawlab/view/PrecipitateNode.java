// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.model.Beaker;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;
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
    private final PText valueNode;

    public PrecipitateNode( Solution solution, Beaker beaker ) {
        this.solution = solution;
        this.beaker = beaker;
        this.particleNodes = new ArrayList<PrecipitateParticleNode>();
        valueNode = new PText( "?" ) {{
            setFont( new PhetFont( 12 ) );
        }};
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( valueNode );
            // below bottom left corner of beaker
            valueNode.setOffset( beaker.getX() - beaker.getWidth(), 10 );
        }

        // when the saturation changes, update the number of precipitate particles
        solution.addPrecipitateAmountObserver( new SimpleObserver() {
            public void update() {
                updateParticles();
                updateValue();
            }
        } );

        // when the solute changes, remove all particles and create new particles for the solute
        solution.solute.addObserver( new SimpleObserver() {
            public void update() {
                removeAllParticles();
                updateParticles();
                updateValue();
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
        int numberOfParticles = getNumberOfParticles();
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

    private void updateValue() {
        double precipitateAmount = solution.getPrecipitateAmount();
        int numberOfParticles = getNumberOfParticles();
        valueNode.setText( "precipitate: " + new DecimalFormat( "0.00000" ).format( precipitateAmount ) + " M (" + numberOfParticles + " particles)" ); // dev only, no i18n needed
    }

    private int getNumberOfParticles() {
        int numberOfParticles = (int) ( solution.solute.get().precipitateParticlesPerMole * solution.getPrecipitateAmount() );
        if ( numberOfParticles == 0 && solution.getPrecipitateAmount() > 0 ) {
            numberOfParticles = 1;
        }
        return numberOfParticles;
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
