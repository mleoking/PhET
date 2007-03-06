/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.model.PlumPuddingModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * PlumPuddingNode is the visual representation of the "plum pudding" 
 * model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlumPuddingNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean SHOW_MODEL_SHAPE = false;
    
    private static final int NUMBER_OF_ELECTRONS = 79;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public PlumPuddingNode( PlumPuddingModel atom ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        // Read the pudding image and scale it to match the atom model
        PImage puddingNode = PImageFactory.create( RSConstants.IMAGE_PLUM_PUDDING );
        double imageDiameter = Math.max( puddingNode.getWidth(), puddingNode.getHeight() );
        double atomDiameter = 2 * ModelViewTransform.transform( atom.getRadius() );
        double imageScale = atomDiameter / imageDiameter;
        puddingNode.scale( imageScale );
        puddingNode.setOffset( -puddingNode.getFullBounds().getWidth()/2, -puddingNode.getFullBounds().getHeight()/2 );
        addChild( puddingNode );
      
        // Create electrons at random locations throughout the pudding
//        Random randomDistance = new Random();
//        Random randomAngle = new Random();
//        double maxDistance = atomDiameter / 2;
//        for ( int i = 0; i < NUMBER_OF_ELECTRONS; i++ ) {
//            ElectronNode electronNode = new ElectronNode();
//            // choose a random location in polar coordinates
//            double distance = randomDistance.nextDouble() * maxDistance;
//            double angle = randomAngle.nextDouble() * ( 2 * Math.PI );
//            // convert to Cartesian coordinates
//            double x = distance * Math.cos( angle );
//            double y = distance * Math.sin( angle );
//            System.out.println( "adding electron at (" + x + "," + y + ")" );//XXX
//            electronNode.setOffset( x, y );
//            addChild( electronNode );
//        }
        
        // Flatten everything to an image
        PImage imageNode = new PImage( this.toImage() );
        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
        removeAllChildren();
        addChild( imageNode );
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
    }
}
