// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.BufferedPNode;
import edu.colorado.phet.rutherfordscattering.RSResources;
import edu.colorado.phet.rutherfordscattering.model.PlumPuddingAtom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * PlumPuddingAtomNode is the visual representation of the "plum pudding" atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlumPuddingAtomNode extends BufferedPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // set this to true to see how the electron location are chosen
    private static final boolean DEBUG_ELECTRONS = false;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param canvas the canvas, for managing the scaling transform
     * @param atom
     */
    public PlumPuddingAtomNode( PhetPCanvas canvas, PlumPuddingAtom atom ) {
        super( canvas, createManagedNode( atom ) );
        
        setPickable( false );
        setChildrenPickable( false );
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = RSModelViewTransform.transform( atomPosition );
        setOffset( nodePosition.getX() - getFullBounds().getWidth() / 2, nodePosition.getY() - getFullBounds().getHeight() / 2 );
    }

    /*
     * Creates a static PImage of the plum pudding atom.
     */
    private static PNode createManagedNode( PlumPuddingAtom atom ) {

        PNode parentNode = new PNode();
        
        // Read the pudding image and scale it to match the atom model
        Image puddingImage = RSResources.getImage(  "plumPudding.png" );
        PImage puddingNode = new PImage( puddingImage );
        final double imageDiameter = Math.max( puddingNode.getWidth(), puddingNode.getHeight() );
        final double atomDiameter = 2 * RSModelViewTransform.transform( atom.getRadius() );
        final double imageScale = atomDiameter / imageDiameter;
        puddingNode.scale( imageScale );
        puddingNode.setOffset( -puddingNode.getFullBounds().getWidth() / 2, -puddingNode.getFullBounds().getHeight() / 2 );
        parentNode.addChild( puddingNode );

        /*
         * Ellipse that defines the center of the pudding.
         * All electrons will be placed inside this ellipse.
         */
        final double m = 0.85; // tweaked to match the ellipse image!
        final double w = m * puddingNode.getFullBounds().getWidth();
        final double h = m * puddingNode.getFullBounds().getHeight();
        double a = 0; // radius of the major (larger) axis
        double b = 0; // radius of the minor (smaller) axis
        double theta0 = 0;
        if ( w > h ) {
            a = w / 2;
            b = h / 2;
            theta0 = 0;
        }
        else {
            a = h / 2;
            b = w / 2;
            theta0 = Math.toRadians( 90 );
        }
        assert ( a >= b );

        /*
         * Draw NUMBER_OF_ELECTRONS chords through the center of the ellipse.
         * Randomly place 1 electron on each chord.
         */
        final int numberOfElectrons = atom.getNumberOfElectrons();
        final double deltaTheta = Math.toRadians( 360 ) / numberOfElectrons;
        Random randomDistance = new Random();
        for ( int i = 0; i < numberOfElectrons; i++ ) {

            final double theta = theta0 + ( i * deltaTheta );

            // point on the edge of the ellipse
            final double x = a * Math.cos( theta );
            final double y = b * Math.sin( theta );

            if ( DEBUG_ELECTRONS ) {
                // Draw the chord
                Line2D line = new Line2D.Double( 0, 0, x, y );
                PPath lineNode = new PPath( line );
                lineNode.setStrokePaint( Color.WHITE );
                parentNode.addChild( lineNode );
            }

            // convert to Polar coodinates
            final double r = Math.sqrt( ( x * x ) + ( y * y ) );

            // pick a point on the chord, giving higher weight to values farther from the center
            final double d = r * Math.sqrt( randomDistance.nextDouble() );
            final double ex = d * Math.cos( theta );
            final double ey = d * Math.sin( theta );

            // create the electron node
            ElectronNode electronNode = new ElectronNode();
            electronNode.setOffset( ex, ey );
            parentNode.addChild( electronNode );
        }

        if ( DEBUG_ELECTRONS ) {
            // Draw outline of the ellipse that encloses all electrons
            Ellipse2D ellipse = new Ellipse2D.Double( -w / 2, -h / 2, w, h );
            PPath ellipseNode = new PPath( ellipse );
            ellipseNode.setStrokePaint( Color.WHITE );
            parentNode.addChild( ellipseNode );
        }

        // Flatten everything to an image
        PImage imageNode = new PImage( parentNode.toImage() ) {
            //XXX for verifying performance bottleneck in JProfiler
            protected void paint( PPaintContext paintContext ) {
                super.paint( paintContext );
            }
        };
        return imageNode;
    }
}