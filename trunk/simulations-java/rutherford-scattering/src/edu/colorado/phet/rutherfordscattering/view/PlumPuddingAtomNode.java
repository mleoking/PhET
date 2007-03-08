/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.model.PlumPuddingAtom;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * PlumPuddingAtomNode is the visual representation of the "plum pudding" atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlumPuddingAtomNode extends PhetPNode {
    
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
     * @param atom
     */
    public PlumPuddingAtomNode( PlumPuddingAtom atom ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        // Read the pudding image and scale it to match the atom model
        PImage puddingNode = PImageFactory.create( RSConstants.IMAGE_PLUM_PUDDING );
        final double imageDiameter = Math.max( puddingNode.getWidth(), puddingNode.getHeight() );
        final double atomDiameter = 2 * ModelViewTransform.transform( atom.getRadius() );
        final double imageScale = atomDiameter / imageDiameter;
        puddingNode.scale( imageScale );
        puddingNode.setOffset( -puddingNode.getFullBounds().getWidth()/2, -puddingNode.getFullBounds().getHeight()/2 );
        addChild( puddingNode );
        
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
        assert( a >= b );
        
        /*
         * Draw NUMBER_OF_ELECTRONS chords through the center of the ellipse.
         * Randomly place 1 electron on each chord.
         */
        final int numberOfElectrons = atom.getNumberOfElectrons();
        final double deltaTheta = Math.toRadians( 360 ) / numberOfElectrons;
        Random randomDistance = new Random( 11111 ); // use a seed to provide the same "good looking" result each time
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
                addChild( lineNode );
            }
            
            // convert to Polar coodinates
            final double r = Math.sqrt( ( x * x ) + ( y * y ) );
            
            // pick a point on the chord
            final double d = r * randomDistance.nextDouble();
            final double ex = d * Math.cos( theta );
            final double ey = d * Math.sin( theta );
            
            // create the electron node
            ElectronNode electronNode = new ElectronNode();
            electronNode.setOffset( ex, ey );
            addChild( electronNode );
        }
        
        if ( DEBUG_ELECTRONS ) {
            // Draw outline of the ellipse that encloses all electrons
            Ellipse2D ellipse = new Ellipse2D.Double( -w / 2, -h / 2, w, h );
            PPath ellipseNode = new PPath( ellipse );
            ellipseNode.setStrokePaint( Color.WHITE );
            addChild( ellipseNode );
        }
        
        // Flatten everything to an image
        PImage imageNode = new PImage( this.toImage() ) {
            //XXX for verifying performance bottleneck in JProfiler
            protected void paint( PPaintContext paintContext ) {
                super.paint( paintContext );
            }
        };
        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
        removeAllChildren();
        addChild( imageNode );
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
    }
}
