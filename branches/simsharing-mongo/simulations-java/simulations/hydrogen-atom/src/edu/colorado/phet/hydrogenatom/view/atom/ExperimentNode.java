// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.hydrogenatom.model.ExperimentModel;
import edu.colorado.phet.hydrogenatom.view.HAModelViewTransform;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


public class ExperimentNode extends PhetPNode implements Observer {

    private static final Font FONT = new PhetFont( Font.PLAIN, 100 );
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BOX_FILL_COLOR = new Color( 20, 20, 20 );
    private static final Color BOX_STROKE_COLOR = Color.WHITE;
    private static final Stroke BOX_STROKE = new BasicStroke( 2f );
    private static final Dimension BOX_SIZE = new Dimension( 125, 125 );
    
    private ExperimentModel _atom;
    
    public ExperimentNode( ExperimentModel atom ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _atom = atom;
        _atom.addObserver( this );
        
        double w = BOX_SIZE.width;
        double h = BOX_SIZE.height;
        PPath boxNode = new PPath( new Rectangle2D.Double( -w / 2, -h / 2, w, h ) );
        boxNode.setPaint( BOX_FILL_COLOR );
        boxNode.setStroke( BOX_STROKE );
        boxNode.setStrokePaint( BOX_STROKE_COLOR );
        boxNode.setOffset( 0, 0 );
        
        PText textNode = new PText( "?" );
        textNode.setFont( FONT );
        textNode.setTextPaint( TEXT_COLOR );
        double x = boxNode.getX() + ( ( boxNode.getWidth() / 2 ) - ( textNode.getWidth() / 2 ) );
        double y = boxNode.getY() + ( ( boxNode.getHeight() / 2 ) - ( textNode.getHeight() / 2 ) );
        textNode.setOffset( x, y );
        
        addChild( boxNode );
        addChild( textNode );
        
        setOffset( HAModelViewTransform.transform( _atom.getPositionRef() ) );
    }
    
    /**
     * Updates the view to match the model.
     * Since this model doesn't show any animation of the atom,
     * there is nothing to be done.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {}
}
