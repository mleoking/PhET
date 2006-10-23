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
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.BilliardBallModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;


public class BilliardBallNode extends AbstractAtomNode implements Observer {

    private BilliardBallModel _hydrogenAtom;
    
    public BilliardBallNode( BilliardBallModel hydrogenAtom ) {
        super();
        
        _hydrogenAtom = hydrogenAtom;
        _hydrogenAtom.addObserver( this );
        
        PImage billiardBallNode = PImageFactory.create( HAConstants.IMAGE_BILLIARD_BALL );
        double desiredRadius = _hydrogenAtom.getRadius();
        double actualRadius = billiardBallNode.getFullBounds().getWidth();
        double scale = desiredRadius / actualRadius;
        billiardBallNode.scale( scale );
        addChild( billiardBallNode );
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }

        update( null, null );
    }

    public void update( Observable o, Object arg ) {
        Point2D p = ModelViewTransform.translate( _hydrogenAtom.getPosition() );
        PBounds fb = getFullBounds();
        double x = p.getX() - ( fb.getWidth() / 2 );
        double y = p.getY() - ( fb.getHeight() / 2 );
        setOffset( x, y );
    }
}
