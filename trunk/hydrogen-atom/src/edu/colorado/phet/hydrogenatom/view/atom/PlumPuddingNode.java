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


public class PlumPuddingNode extends AbstractAtomNode implements Observer {

    private PlumPuddingModel _hydrogenAtom;
    
    public PlumPuddingNode( PlumPuddingModel hydrogenAtom ) {
        super();
        
        _hydrogenAtom = hydrogenAtom;
        _hydrogenAtom.addObserver( this );
        
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
    
    public void update( Observable o, Object arg ) {
        setOffset( ModelViewTransform.translate( _hydrogenAtom.getPosition() ) ); 
    }
}
