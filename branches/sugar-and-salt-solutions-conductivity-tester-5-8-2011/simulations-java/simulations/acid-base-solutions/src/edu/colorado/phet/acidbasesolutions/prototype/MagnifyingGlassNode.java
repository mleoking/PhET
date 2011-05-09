// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.prototype.MagnifyingGlass.MoleculeRepresentation;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlassNode extends PComposite {
    
    private static final Stroke GLASS_STROKE = new BasicStroke( 18f );
    private static final Color GLASS_STROKE_COLOR = Color.BLACK;
    
    private static final Stroke HANDLE_STROKE = new BasicStroke( 2f );
    private static final Color HANDLE_STROKE_COLOR = Color.BLACK;
    private static final Color HANDLE_FILL_COLOR = new Color( 85, 55, 33 ); // brown
    private static final double HANDLE_ARC_WIDTH = 40;
    
    private final MagnifyingGlass magnifyingGlass;
    private final WeakAcid solution;
    private final PPath handleNode;
    private final RoundRectangle2D handlePath;
    private final PPath circleNode;
    private final Ellipse2D circlePath;
    private final DotsNode dotsNode;
    private final ImagesNode imagesNode;
    
    public MagnifyingGlassNode( MagnifyingGlass magnifyingGlass, WeakAcid solution, boolean showOH ) {
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
        
        handlePath = new RoundRectangle2D.Double();
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
        
        dotsNode = new DotsNode( solution, magnifyingGlass, showOH );
        circleNode.addChild( dotsNode ); // clip dots to circle
        
        imagesNode = new ImagesNode( solution, magnifyingGlass, showOH );
        circleNode.addChild( imagesNode ); // clip images to circle
        
        update();
    }
    
    public DotsNode getDotsNode() {
        return dotsNode;
    }
    
    public ImagesNode getImagesNode() {
        return imagesNode;
    }
    
    private void update() {
        double diameter = magnifyingGlass.getDiameter();
        // glass
        circlePath.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
        circleNode.setPathTo( circlePath );
        circleNode.setPaint( solution.getColor() );
        // handle
        double width = diameter / 8;
        double height = diameter / 2.25;
        handlePath.setRoundRect( -width/2, 0, width, height, HANDLE_ARC_WIDTH, HANDLE_ARC_WIDTH );
        handleNode.setPathTo( handlePath );
        handleNode.getTransform().setToIdentity();
        PAffineTransform transform = new PAffineTransform();
        transform.rotate(  -Math.PI / 4 );
        transform.translate( 0, diameter / 2 );
        handleNode.setTransform( transform );
        // representation
        dotsNode.setVisible( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.DOTS );
        imagesNode.setVisible( magnifyingGlass.getMoleculeRepresentation() == MoleculeRepresentation.IMAGES );
    }
    
}
