/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.PHPaper;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.AqueousSolutionChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeListener;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * pH paper, changes color based on the pH of a solution. 
 * Origin is at the top center.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPaperNode extends PhetPNode {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private PHPaper paper;
    private AqueousSolution solution;
    private AqueousSolutionChangeListener listener;
    private PPath pathNode;

    public PHPaperNode( final PHPaper paper ) {
        this( paper.getSizeReference() );

        this.paper = paper;
        paper.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeListener() {

            public void solutionChanged() {
                setSolution( paper.getSolution() );
            }
            
            public void locationChanged() {
                //TODO map location from model to view coordinate frame
                setOffset( paper.getLocationReference() );
                updateGeomerty();
                updateColor();
            }

            public void visibilityChanged() {
                setVisible( paper.isVisible() );
            }

        } );

        this.solution = paper.getSolution();
        this.listener = new AqueousSolutionChangeListener() {

            public void strengthChanged() {
                updateColor();
            }

            public void concentrationChanged() {
                updateColor();
            }
        };
        solution.addAqueousSolutionChangeListener( listener );

        addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        addInputEventListener( new PDragSequenceEventHandler() {

            private double clickYOffset; // y-offset of mouse click from meter's origin, in parent's coordinate frame

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                clickYOffset = pMouse.getY() - paper.getLocationReference().getY();
            }

            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                double x = getXOffset();
                double y = pMouse.getY() - clickYOffset;
                //TODO map y from view to model coordinate frame
                paper.setLocation( x, y );
            }
        } );

        setOffset( paper.getLocationReference() );
        setVisible( paper.isVisible() );
        updateGeomerty();
        updateColor();
    }
    
    private void updateGeomerty() {
        //TODO compute separate shapes for inside and outside solution
    }

    private void updateColor() {
        //TODO change color of inside-solution geometry
        /*
         * Questions:
         * - what should happen when paper is pulled out of solution, should it keep color?
         * - what should happen when solution is changed? with paper 0-100% dipped?
         */
        pathNode.setPaint( paper.getColor() );
    }

    /*
     * Private constructor, has no knowledge of the model.
     */
    private PHPaperNode( PDimension size ) {
        Rectangle2D r = new Rectangle2D.Double( -size.getWidth() / 2, 0, size.getWidth(), size.getHeight() );
        pathNode = new PPath( r );
        pathNode.setStroke( STROKE );
        pathNode.setStrokePaint( STROKE_COLOR );
        addChild( pathNode );
    }

    private void setSolution( AqueousSolution solution ) {
        if ( solution != this.solution ) {
            this.solution.removeAqueousSolutionChangeListener( listener );
            this.solution = solution;
            this.solution.addAqueousSolutionChangeListener( listener );
            updateColor();
        }
    }
}
