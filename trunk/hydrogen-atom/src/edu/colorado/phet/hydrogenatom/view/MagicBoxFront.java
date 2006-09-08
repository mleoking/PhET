/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class MagicBoxFront extends PhetPNode {

    private static final Color FRONT_COLOR = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private PPath _insideNode;
    private Shape _shapeNoWindow;
    private Shape _shapeWithWindow;
    
    public MagicBoxFront( double width, double height ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _insideNode = new PPath();
        _insideNode.setPaint( FRONT_COLOR );
        _insideNode.setStroke( STROKE );
        _insideNode.setStrokePaint( STROKE_COLOR );
        addChild( _insideNode );
        
        _shapeNoWindow = new Rectangle2D.Double( 0, 0, width, height );
        
        // Use constructive geometry to punch a window in a rectangle.
        {
            Shape frontShape = new Rectangle2D.Double( 0, 0, width, height );
            Shape windowShape = new Rectangle2D.Double( 0.1 * width, 0.1 * height, width - (0.2 * width), height - (0.2 * height ) );
            Area frontArea = new Area( frontShape );
            Area windowArea = new Area( windowShape );
            frontArea.subtract( windowArea );
            _shapeWithWindow = frontArea;
        }
        
        setWindowOpen( false );
    }
    
    public void setWindowOpen( boolean open ) {
        if ( open ) {
            _insideNode.setPathTo( _shapeWithWindow );
        }
        else {
            _insideNode.setPathTo( _shapeNoWindow );   
        }
    }
}
