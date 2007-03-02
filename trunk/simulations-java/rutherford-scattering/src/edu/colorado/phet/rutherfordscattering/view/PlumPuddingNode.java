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
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.model.AbstractHydrogenAtom;
import edu.colorado.phet.rutherfordscattering.model.PlumPuddingModel;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * PlumPuddingNode is the visual representation of the "plum pudding" 
 * model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PlumPuddingNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean SHOW_MODEL_SHAPE = false;
    
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
        
        PImage puddingNode = PImageFactory.create( RSConstants.IMAGE_PLUM_PUDDING );
        double imageRadius = Math.max( puddingNode.getWidth(), puddingNode.getHeight() );
        double atomRadius = 2 * ModelViewTransform.transform( atom.getRadius() );
        double imageScale = atomRadius / imageRadius;
        puddingNode.scale( imageScale );
        
        addChild( puddingNode );

        if ( SHOW_MODEL_SHAPE ) {
            PPath shapeNode = new PPath();
            double r = atom.getRadius();
            double d = ( 2 * r );
            shapeNode.setPathTo( new Ellipse2D.Double( -r, -r, d, d ) );
            shapeNode.setStroke( new BasicStroke( 1f ) );
            shapeNode.setStrokePaint( Color.GREEN );
            addChild( shapeNode );
        }
        
        PBounds pb = puddingNode.getFullBounds();
        puddingNode.setOffset( -pb.getWidth()/2, -pb.getHeight()/2 );
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
    }
}
