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

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;


public class SchrodingerNode extends AbstractAtomNode implements Observer {
    
    private SchrodingerModel _hydrogenAtom;
    
    public SchrodingerNode( SchrodingerModel hydrogenAtom ) {
        super();
        
        _hydrogenAtom = hydrogenAtom;
        _hydrogenAtom.addObserver( this );
        
        PImage imageNode = PImageFactory.create( HAConstants.IMAGE_SCHRODINGER_ATOM );
        addChild( imageNode );
        
        update( null, null );
    }
    
    public void update( Observable o, Object arg ) {
        Point2D p = ModelViewTransform.transform( _hydrogenAtom.getPosition() );
        PBounds fb = getFullBounds();
        double x = p.getX() - ( fb.getWidth() / 2 );
        double y = p.getY() - ( fb.getHeight() / 2 );
        setOffset( x, y );
    }
}
