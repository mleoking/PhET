/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelListener;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassListener;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Visual representation of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnifyingGlassNode extends PhetPNode {
    
    private static final Stroke GLASS_STROKE = new BasicStroke( 18f );
    private static final Color GLASS_STROKE_COLOR = Color.BLACK;
    
    private static final Stroke HANDLE_STROKE = new BasicStroke( 2f );
    private static final Color HANDLE_STROKE_COLOR = Color.BLACK;
    private static final Color HANDLE_FILL_COLOR = new Color( 85, 55, 33 ); // brown
    private static final double HANDLE_ARC_WIDTH = 40;
    
    private final ABSModel model;
    private final PPath handleNode;
    private final RoundRectangle2D handlePath;
    private final PPath circleNode;
    private final Ellipse2D circlePath;
    private final MoleculesNode moleculesNode;
    
    public MagnifyingGlassNode( ABSModel model ) {
        super();
        
        this.model = model;
        model.addModelListener( new ModelListener() {
            public void solutionChanged() {
                updateColor();
            }
        });
        model.getMagnifyingGlass().addMagnifyingGlassListener( new MagnifyingGlassListener() {
            public void diameterChanged() {
                updateGeometry();
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
        
        moleculesNode = new MoleculesNode( model );
        circleNode.addChild( moleculesNode ); // clip images to circle
        
        updateGeometry();
        updateColor();
    }
    
    public MoleculesNode getMoleculesNode() {
        return moleculesNode;
    }
    
    private void updateGeometry() {
        double diameter = model.getMagnifyingGlass().getDiameter();
        // glass
        circlePath.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
        circleNode.setPathTo( circlePath );
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
    }
    
    private void updateColor() {
        circleNode.setPaint( model.getSolution().getColor() );
    }
    
}
