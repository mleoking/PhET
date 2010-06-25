/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeAdapter;
import edu.colorado.phet.acidbasesolutions.model.ABSModelElement.ModelElementChangeAdapter;
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
    private final PPath circleNode, backgroundNode;
    private final Ellipse2D circlePath;
    private final MoleculesNode moleculesNode;
    
    public MagnifyingGlassNode( final ABSModel model ) {
        super();
        
        this.model = model;
        model.addModelChangeListener( new ModelChangeAdapter() {
            @Override
            public void solutionChanged() {
                updateColor();
            }
        });
        model.getMagnifyingGlass().addMagnifyingGlassListener( new MagnifyingGlassListener() {
            public void diameterChanged() {
                updateGeometry();
            }
        });
        model.getMagnifyingGlass().addModelElementChangeListener( new ModelElementChangeAdapter() {
            @Override
            public void visibilityChanged() {
                setVisible( model.getMagnifyingGlass().isVisible() );
            }
        });
        
        handlePath = new RoundRectangle2D.Double();
        handleNode = new PPath();
        handleNode.setStroke( HANDLE_STROKE );
        handleNode.setStrokePaint( HANDLE_STROKE_COLOR );
        handleNode.setPaint( HANDLE_FILL_COLOR );
        
        circlePath = new Ellipse2D.Double();
        circleNode = new PClip();
        circleNode.setStroke( GLASS_STROKE );
        circleNode.setStrokePaint( GLASS_STROKE_COLOR );
        
        /*
         * Use an opaque background node so that we can't see other things that go behind
         * the magnifying glass (eg, pH meter and other tools).  The color of this background
         * node is the same as the canvas color, so that the liquid in the magnifying glass
         * will appear to be the same color as the liquid in the beaker.  The shape of the
         * background is identical to the shape of the magnifying glass.
         */
        backgroundNode = new PPath();
        backgroundNode.setPaint( ABSColors.CANVAS_BACKGROUND );
        
        moleculesNode = new MoleculesNode( model  );
        
        // rendering order
        addChild( handleNode );
        addChild( backgroundNode );
        addChild( circleNode );
        circleNode.addChild( moleculesNode ); // clip images to circle
        
        updateGeometry();
        updateColor();
        setOffset( model.getMagnifyingGlass().getLocationReference() );
        setVisible( model.getMagnifyingGlass().isVisible() );
    }
    
    public MoleculesNode getMoleculesNode() {
        return moleculesNode;
    }
    
    private void updateGeometry() {
        double diameter = model.getMagnifyingGlass().getDiameter();
        // glass
        circlePath.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
        circleNode.setPathTo( circlePath );
        backgroundNode.setPathTo( circlePath );
        // handle
        double width = diameter / 8;
        double height = diameter / 2.25;
        handlePath.setRoundRect( -width/2, 0, width, height, HANDLE_ARC_WIDTH, HANDLE_ARC_WIDTH );
        handleNode.setPathTo( handlePath );
        handleNode.getTransform().setToIdentity();
        PAffineTransform transform = new PAffineTransform();
        transform.rotate(  -Math.PI / 3 );
        transform.translate( 0, diameter / 2 );
        handleNode.setTransform( transform );
    }
    
    private void updateColor() {
        circleNode.setPaint( model.getSolution().getColor() );
    }
    
}
