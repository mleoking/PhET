// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.model.PHPaper;
import edu.colorado.phet.acidbasesolutions.model.PHPaper.PHPaperChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * pH paper, changes color based on the pH of a solution. 
 * Origin is at the top center.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPaperNode extends PhetPNode {
    
    private static final Stroke STROKE = new BasicStroke( 0.5f );
    private static final Color STROKE_COLOR = new Color( 150, 150, 150 );
    
    private PHPaper paper;
    private PPath paperBodyNode;
    private PPath dippedPathNode;
    private Rectangle2D dippedRectangle;

    public PHPaperNode( final PHPaper paper ) {

        this.paper = paper;
        paper.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {

            @Override
            public void locationChanged() {
                //TODO map location from model to view coordinate frame
                setOffset( paper.getLocationReference() );
            }

            @Override
            public void visibilityChanged() {
                setVisible( paper.isVisible() );
            }
        } );
        
        paper.addPHPaperChangeListener( new PHPaperChangeListener() {

            public void dippedColorChanged() {
                updateColor();
            }

            public void dippedHeightChanged() {
                updateGeometry();
                updateColor();
            }
        } );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragSequenceEventHandler() {

            private double clickXOffset; // x-offset of mouse click from meter's origin, in parent's coordinate frame
            private double clickYOffset; // y-offset of mouse click from meter's origin, in parent's coordinate frame

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                clickXOffset = pMouse.getX() - paper.getLocationReference().getX();
                clickYOffset = pMouse.getY() - paper.getLocationReference().getY();
            }

            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                double x = pMouse.getX() - clickXOffset;
                double y = pMouse.getY() - clickYOffset;
                paper.setLocation( x, y );
            }
        } );
        
        Rectangle2D r = new Rectangle2D.Double( -paper.getWidth() / 2, 0, paper.getWidth(), paper.getHeight() );
        paperBodyNode = new PPath( r );
        paperBodyNode.setStroke( null );
        paperBodyNode.setPaint( paper.getPaperColor() );
        addChild( paperBodyNode );
        
        dippedRectangle = new Rectangle2D.Double();
        dippedPathNode = new PPath();
        dippedPathNode.setStroke( null );
        dippedPathNode.setPaint( paper.getDippedColor() );
        addChild( dippedPathNode );

        PPath outlineNode = new PPath( r );
        outlineNode.setStroke( STROKE );
        outlineNode.setStrokePaint( STROKE_COLOR );
        addChild( outlineNode );
        
        setOffset( paper.getLocationReference() );
        setVisible( paper.isVisible() );
        updateGeometry();
        updateColor();
    }
    
    private void updateGeometry() {
        double x = -paper.getWidth() / 2;
        double y = paper.getHeight() - paper.getDippedHeight();
        dippedRectangle.setRect( x, y, paper.getWidth(), paper.getDippedHeight() );
        dippedPathNode.setPathTo( dippedRectangle );
    }

    private void updateColor() {
        dippedPathNode.setPaint( paper.getDippedColor() );
    }
}
