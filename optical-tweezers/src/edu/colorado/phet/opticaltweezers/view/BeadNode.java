/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.util.RoundGradientPaint;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;


public class BeadNode extends SphericalNode implements Observer {

    private static final int ALPHA = 200;
    private static final Color PRIMARY_COLOR = new Color( 200, 200, 0, ALPHA );
    private static final Color HILITE_COLOR = new Color( 255, 255, 0, ALPHA );
    private static final Stroke STROKE = new BasicStroke( 0.5f );
    private static final Paint STROKE_PAINT = Color.BLACK;
    
    private Bead _bead;
    private ModelViewTransform _modelViewTransform;
    
    public BeadNode( Bead bead, ModelViewTransform modelViewTransform ) {
        super( true /* convertToImage */ );
        
        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final double diameter = _modelViewTransform.transform( bead.getDiameter() );
        Point2D position = _modelViewTransform.transform( bead.getPositionRef() );
        
        setDiameter( diameter );
        Paint paint = new RoundGradientPaint( 0, diameter/6, HILITE_COLOR, new Point2D.Double( diameter/4, diameter/4 ), PRIMARY_COLOR );
        setPaint( paint );
        setStroke( STROKE );
        setStrokePaint( STROKE_PAINT );
        
        setOffset( position.getX(), position.getY() );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() ); //XXX constrain to bounds of glass slide
    }
    
    public void cleanup() {
        _bead.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        //XXX
    }
}
