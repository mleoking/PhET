/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.PlumPuddingModel;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * PlumPuddingNode is the visual representation of the "plum pudding" model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PlumPuddingNode extends AbstractHydrogenAtomNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean SHOW_MODEL_SHAPE = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PlumPuddingModel _atom;
    private ElectronNode _electronNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public PlumPuddingNode( PlumPuddingModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        PImage puddingNode = PImageFactory.create( HAConstants.IMAGE_PLUM_PUDDING );
        double imageHeight = puddingNode.getHeight();
        double atomHeight = 2 * atom.getRadius();
        double imageScale = atomHeight / imageHeight;
        puddingNode.scale( imageScale );
        
        _electronNode = new ElectronNode();
        
        addChild( puddingNode );
        addChild( _electronNode );

        if ( SHOW_MODEL_SHAPE ) {
            PPath shapeNode = new PPath();
            double r = _atom.getRadius();
            double d = ( 2 * r );
            shapeNode.setPathTo( new Ellipse2D.Double( -r, -r, d, d ) );
            shapeNode.setStroke( new BasicStroke( 1f ) );
            shapeNode.setStrokePaint( Color.GREEN );
            addChild( shapeNode );
        }
        
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            OriginNode originNode = new OriginNode( Color.GREEN );
            addChild( originNode );
        }
        
        PBounds pb = puddingNode.getFullBounds();
        puddingNode.setOffset( -pb.getWidth()/2, -pb.getHeight()/2 );
        
        Point2D atomPosition = _atom.getPosition();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition );
        
        update( _atom, AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET ) {
                // the electron has moved
                Point2D electronOffset = _atom.getElectronOffset();
                // treat coordinates as distances, since _electronNode is a child node
                double nodeX = ModelViewTransform.transform( electronOffset.getX() );
                double nodeY = ModelViewTransform.transform( electronOffset.getY() );
                _electronNode.setOffset( nodeX, nodeY );
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_IONIZED ) {
                //XXX
            }
        }
    }
}
