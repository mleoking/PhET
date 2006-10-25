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
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.BilliardBallModel;
import edu.colorado.phet.hydrogenatom.util.RoundGradientPaint;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.hydrogenatom.view.SphericalNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;


public class BilliardBallNode extends AbstractAtomNode implements Observer {

    private static final Color COLOR = new Color( 196, 78, 14 ); // orange
    private static final Color HILITE_COLOR = new Color( 255, 141, 21 ); // lighter orange
    
    private BilliardBallModel _hydrogenAtom;
    
    public BilliardBallNode( BilliardBallModel hydrogenAtom ) {
        super();
        
        _hydrogenAtom = hydrogenAtom;
        _hydrogenAtom.addObserver( this );
        
        double radius = ModelViewTransform.transform( _hydrogenAtom.getRadius() );
        double diameter = 2 * radius;
        Paint roundGradient = new RoundGradientPaint( 0, diameter/6, HILITE_COLOR, new Point2D.Double( diameter/4, diameter/4 ), COLOR );
        PNode billiardBallNode = new SphericalNode( diameter, roundGradient, true /* convertToImage */ );
        addChild( billiardBallNode );

        update( null, null );
    }

    public void update( Observable o, Object arg ) {
        setOffset( ModelViewTransform.transform( _hydrogenAtom.getPosition() ) );
    }
}
