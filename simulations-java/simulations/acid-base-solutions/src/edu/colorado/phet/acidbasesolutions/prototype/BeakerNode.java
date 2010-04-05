/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker that is filled to the top with a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeakerNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final DecimalFormat PH_FORMAT = new DecimalFormat( "0.00" );
    
    private final Beaker beaker;
    private final WeakAcid solution;
    private final PPath outlineNode, solutionNode;
    private final GeneralPath outlinePath; 
    private final Rectangle2D solutionRectangle;
    private final PText pHNode;
    
    public BeakerNode( Beaker beaker, WeakAcid solution ) {
        super();
        
        this.beaker = beaker;
        beaker.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        } );
        
        solutionRectangle = new Rectangle2D.Double();
        solutionNode = new PPath();
        solutionNode.setPaint( solution.getColor() );
        solutionNode.setStroke( null );
        addChild( solutionNode );
        
        outlinePath = new GeneralPath();
        outlineNode = new PPath();
        outlineNode.setPaint( null );
        outlineNode.setStroke( STROKE );
        outlineNode.setStrokePaint( STROKE_COLOR );
        addChild( outlineNode );
        
        pHNode = new PText( "?" );
        pHNode.scale( 3 );
        addChild( pHNode );
        
        update();
    }
    
    private void update() {
        updateBeaker();
        updateSolution();
        updatePH();
    }
    
    private void updateBeaker() {
        double width = beaker.getWidth();
        double height = beaker.getHeight();
        double rimOffset = 20;
        outlinePath.reset();
        outlinePath.moveTo( (float) -width/2 - rimOffset, (float) -height/2 - rimOffset );
        outlinePath.lineTo( (float) -width/2, (float) -height/2 );
        outlinePath.lineTo( (float) -width/2, (float) +height/2 );
        outlinePath.lineTo( (float) +width/2, (float) +height/2 );
        outlinePath.lineTo( (float) +width/2, (float) -height/2 );
        outlinePath.lineTo( (float) +width/2 + rimOffset, (float) -height/2 - rimOffset );
        outlineNode.setPathTo( outlinePath );
    }
    
    private void updateSolution() {
        solutionNode.setPaint( solution.getColor() );
        double width = beaker.getWidth();
        double height = beaker.getHeight();
        solutionRectangle.setRect( -width/2, -height/2, width, height );
        solutionNode.setPathTo( solutionRectangle );
    }
    
    private void updatePH() {
        pHNode.setText( "pH = " + PH_FORMAT.format( solution.getPH() ) );
        double x = -pHNode.getFullBoundsReference().getWidth()/2;
        double y = -beaker.getHeight()/2 - pHNode.getFullBoundsReference().getHeight() - 5;
        pHNode.setOffset( x, y );
    }
}