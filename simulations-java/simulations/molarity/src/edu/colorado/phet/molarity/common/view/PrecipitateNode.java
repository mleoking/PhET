// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.common.view;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.molarity.common.model.Solution;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This node manages the precipitate that forms on the bottom of the beaker when the solution is saturated.
 * It assumes that the beaker is represented as a cylinder, with elliptical top and bottom.
 * Origin is at the upper-left corner of this cylinder.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateNode extends PComposite {

    private final Solution solution;
    private final PDimension cylinderSize;
    private final double cylinderEndHeight;
    private final ArrayList<PrecipitateParticleNode> particleNodes;
    private final PText valueNode;

    /**
     * Constructor
     *
     * @param solution          solution whose precipitate this node represents
     * @param cylinderSize      width and height of the beaker cylinder
     * @param cylinderEndHeight height of the 2D projection of the 3D beaker cylinder's end cap
     */
    public PrecipitateNode( Solution solution, PDimension cylinderSize, double cylinderEndHeight ) {
        this.solution = solution;
        this.cylinderSize = cylinderSize;
        this.cylinderEndHeight = cylinderEndHeight;
        this.particleNodes = new ArrayList<PrecipitateParticleNode>();
        valueNode = new PText( "?" ) {{
            setFont( new PhetFont( 12 ) );
        }};
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( valueNode );
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

        // layout after value has been set
        valueNode.setOffset( ( cylinderSize.getWidth() - valueNode.getFullBoundsReference().getWidth() ) / 2,
                             cylinderSize.getHeight() + cylinderEndHeight );
    }

    private void removeAllParticles() {
        for ( PrecipitateParticleNode particleNode : particleNodes ) {
            removeChild( particleNode );
        }
        particleNodes.clear();
    }

    // Updates the number of particles to match the saturation of the solution.
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

    // Updates the debug output to show how we're mapping saturation to number of particles.
    private void updateValue() {
        double precipitateAmount = solution.getPrecipitateAmount();
        int numberOfParticles = getNumberOfParticles();
        valueNode.setText( "precipitate: " + new DecimalFormat( "0.00000" ).format( precipitateAmount ) + " mol = " + numberOfParticles + " particles" );
    }

    // Gets the number of particles used to represent the solution's saturation.
    private int getNumberOfParticles() {
        int numberOfParticles = (int) ( solution.solute.get().particlesPerMole * solution.getPrecipitateAmount() );
        if ( numberOfParticles == 0 && solution.getPrecipitateAmount() > 0 ) {
            numberOfParticles = 1;
        }
        return numberOfParticles;
    }

    // Gets a random offset for a particle on the bottom of the beaker.
    protected Point2D getRandomOffset( PrecipitateParticleNode particleNode ) {
        double xMargin = particleNode.getFullBoundsReference().getWidth();
        double yMargin = particleNode.getFullBoundsReference().getHeight();
        double angle = Math.random() * 2 * Math.PI;
        Point2D p = getRandomPointInsideEllipse( angle, cylinderSize.getWidth() - ( 2 * xMargin ), cylinderEndHeight - ( 2 * yMargin ) );
        double x = ( cylinderSize.getWidth() / 2 ) + p.getX();
        double y = cylinderSize.getHeight() - p.getY() - ( yMargin / 2 );
        return new Point2D.Double( x, y );
    }

    // Gets a random point inside an ellipse with origin at its center.
    private static Point2D getRandomPointInsideEllipse( double theta, double width, double height ) {

        // Generate a random point inside a circle of radius 1.
        // Since circle area is a function of radius^2, taking sqrt provides a uniform distribution.
        double x = Math.sqrt( Math.random() ) * Math.cos( theta );
        double y = Math.sqrt( Math.random() ) * Math.sin( theta );

        // Scale x and y to the dimensions of the ellipse
        return new Point2D.Double( x * width / 2, y * height / 2 );
    }
}
