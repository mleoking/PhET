/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.prototype.MagnifyingGlass.MoleculeRepresentation;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Visual representation of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlassNode extends PhetPNode {
    
    private static final Stroke GLASS_STROKE = new BasicStroke( 18f );
    private static final Color GLASS_STROKE_COLOR = Color.BLACK;
    
    private static final Stroke HANDLE_STROKE = new BasicStroke( 1f );
    private static final Color HANDLE_STROKE_COLOR = Color.BLACK;
    private static final Color HANDLE_FILL_COLOR = new Color( 85, 55, 33 ); // brown
    
    private final MagnifyingGlass magnifyingGlass;
    private final WeakAcid solution;
    private final PPath handleNode;
    private final Rectangle2D handlePath;
    private final PPath circleNode;
    private final Ellipse2D circlePath;
    private final DotsNode dotsNode;
    
    public MagnifyingGlassNode( MagnifyingGlass magnifyingGlass, WeakAcid solution ) {
        super();
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        handlePath = new Rectangle2D.Double();
        handleNode = new PPath();
        handleNode.setStroke( HANDLE_STROKE );
        handleNode.setStrokePaint( HANDLE_STROKE_COLOR );
        handleNode.setPaint( HANDLE_FILL_COLOR );
        addChild( handleNode );
        
        circlePath = new Ellipse2D.Double();
        circleNode = new PClip();
        circleNode.setStroke( GLASS_STROKE );
        circleNode.setStrokePaint( GLASS_STROKE_COLOR );
        addChild( circleNode );
        
        dotsNode = new DotsNode( solution, circleNode );
        circleNode.addChild( dotsNode ); // clip dots to circle
        
        update();
    }
    
    public void setDotDiameter( double diameter ) {
        dotsNode.setDotDiameter( diameter );
    }
    
    public void setDotTransparency( float transparency ) {
        dotsNode.setDotTransparency( transparency );
    }
    
    public void setDotColorHA( Color color ) {
        dotsNode.setColorHA( color );
    }
    
    public void setDotColorA( Color color ) {
        dotsNode.setColorA( color );
    }
    
    public void setDotColorH3O( Color color ) {
        dotsNode.setColorH3O( color );
    }
    
    public void setDotColorOH( Color color ) {
        dotsNode.setColorOH( color );
    }
    
    private void update() {
        double diameter = magnifyingGlass.getDiameter();
        // glass
        circlePath.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
        circleNode.setPathTo( circlePath );
        circleNode.setPaint( solution.getColor() );
        // handle
        double width = diameter / 8;
        double height = diameter / 2;
        handlePath.setRect( -width/2, 0, width, height );
        handleNode.setPathTo( handlePath );
        handleNode.getTransform().setToIdentity();
        PAffineTransform transform = new PAffineTransform();
        transform.rotate(  -Math.PI / 4 );
        transform.translate( 0, diameter / 2 );
        handleNode.setTransform( transform );
        // representation
        dotsNode.setVisible( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.DOTS );
    }
    
}
