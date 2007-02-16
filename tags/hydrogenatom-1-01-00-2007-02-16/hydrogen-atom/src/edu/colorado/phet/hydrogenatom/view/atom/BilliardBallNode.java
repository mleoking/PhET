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
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.model.BilliardBallModel;
import edu.colorado.phet.hydrogenatom.util.RoundGradientPaint;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.SphericalNode;
import edu.umd.cs.piccolo.PNode;

/**
 * BilliardBallNode is the visual representation of the "billiard ball" model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BilliardBallNode extends AbstractHydrogenAtomNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color COLOR = new Color( 196, 78, 14 ); // orange
    private static final Color HILITE_COLOR = new Color( 255, 141, 21 ); // lighter orange
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BilliardBallModel _atom; // model element
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public BilliardBallNode( BilliardBallModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        double radius = ModelViewTransform.transform( _atom.getRadius() );
        double diameter = 2 * radius;
        Paint roundGradient = new RoundGradientPaint( 0, diameter/6, HILITE_COLOR, new Point2D.Double( diameter/4, diameter/4 ), COLOR );
        PNode billiardBallNode = new SphericalNode( diameter, roundGradient, true /* convertToImage */ );
        addChild( billiardBallNode );

        setOffset( ModelViewTransform.transform( _atom.getPositionRef() ) );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     * Since this model doesn't show any animation of the atom,
     * there is nothing to be done.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {}
}
