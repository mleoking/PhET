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
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;


public class SolarSystemNode extends AbstractAtomNode implements Observer {

    private SolarSystemModel _hydrogenAtom;
    
    public SolarSystemNode( SolarSystemModel hydrogenAtom ) {
        super();
        
        _hydrogenAtom = hydrogenAtom;
        _hydrogenAtom.addObserver( this );
        
        ProtonNode protonNode = new ProtonNode();
        ElectronNode electronNode = new ElectronNode();
       
        addChild( protonNode );
        addChild( electronNode );
          
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }

        protonNode.setOffset( 0, 0 );
        electronNode.setOffset( 100, -100 );
        
        update( null, null );
    }

    public void update( Observable o, Object arg ) {
        setOffset( ModelViewTransform.transform( _hydrogenAtom.getPosition() ) ); 
    }
}
