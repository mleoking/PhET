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

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.PlumPuddingModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * PlumPuddingNode is the visual representation of the "plum pudding" model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PlumPuddingNode extends AbstractHydrogenAtomNode implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PlumPuddingModel _atom;
    
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
        puddingNode.scale( 0.65 );
        
        ElectronNode electronNode = new ElectronNode();
        
        addChild( puddingNode );
        addChild( electronNode );
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        PBounds pb = puddingNode.getFullBounds();
        puddingNode.setOffset( -pb.getWidth()/2, -pb.getHeight()/2 );
        electronNode.setOffset( 10, 10 );
        
        update( null, null );
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
        setOffset( ModelViewTransform.transform( _atom.getPosition() ) ); 
    }
}
