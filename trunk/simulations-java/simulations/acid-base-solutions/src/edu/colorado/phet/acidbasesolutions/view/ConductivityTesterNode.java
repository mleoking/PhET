/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSResources;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester;
import edu.colorado.phet.acidbasesolutions.model.ConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


public class ConductivityTesterNode extends PhetPNode {
    
    private static final Color POSITIVE_PROBE_FILL_COLOR = Color.RED;
    private static final Color NEGATIVE_PROBE_FILL_COLOR = Color.BLACK;
    private static final Color PROBE_STROKE_COLOR = Color.BLACK;
    private static final Stroke PROBE_STROKE = new BasicStroke( 1f );

    private final ConductivityTester tester;
    private final ProbeNode positiveProbeNode, negativeProbeNode;
    
    public ConductivityTesterNode( final ConductivityTester tester ) {
        
        this.tester = tester;
        tester.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            public void visibilityChanged() {
                setVisible( tester.isVisible() );
            }
        });
        
        tester.addConductivityTesterChangeListener( new ConductivityTesterChangeListener() {

            public void positiveProbeLocationChanged() {
                updatePositiveProbeLocation();
            }
            
            public void negativeProbeLocationChanged() {
                updateNegativeProbeLocation();
            }
        });
        
        PImage imageNode = new PImage( ABSResources.getBufferedImage( "uncleMalley.png" ) ); //XXX
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth()/2, 0 );
        
        // positive probe
        positiveProbeNode = new ProbeNode( tester.getProbeSizeReference(), POSITIVE_PROBE_FILL_COLOR );
        addChild( positiveProbeNode );
        positiveProbeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        positiveProbeNode.addInputEventListener( new PDragSequenceEventHandler() {

            private double clickYOffset; // y-offset of mouse click from meter's origin, in parent's coordinate frame

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                clickYOffset = pMouse.getY() - tester.getPositiveProbeLocationReference().getY() + tester.getLocationReference().getY();
            }

            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                double y = pMouse.getY() - clickYOffset;
                //TODO map y from view to model coordinate frame
                y += tester.getLocationReference().getY();
                tester.setPositiveProbeLocation( tester.getPositiveProbeLocationReference().getX(), y );
            }
        } );

        // negative probe
        negativeProbeNode = new ProbeNode( tester.getProbeSizeReference(), NEGATIVE_PROBE_FILL_COLOR );
        addChild( negativeProbeNode );
        negativeProbeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        negativeProbeNode.addInputEventListener( new PDragSequenceEventHandler() {

            private double clickYOffset; // y-offset of mouse click from meter's origin, in parent's coordinate frame

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                clickYOffset = pMouse.getY() - tester.getNegativeProbeLocationReference().getY()  + tester.getLocationReference().getY();
            }

            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                double y = pMouse.getY() + getYOffset() - clickYOffset;
                //TODO map y from view to model coordinate frame
                tester.setNegativeProbeLocation( tester.getNegativeProbeLocationReference().getX(), y );
            }
        } );
        

        // layout 
        //XXX mvt needed here?
        setOffset( tester.getLocationReference() );
        updatePositiveProbeLocation();
        updateNegativeProbeLocation();
        
        setVisible( tester.isVisible() );
    }
    
    private void updatePositiveProbeLocation() {
        //XXX mvt
        double x = tester.getPositiveProbeLocationReference().getX() - tester.getLocationReference().getX();
        double y = tester.getPositiveProbeLocationReference().getY() - tester.getLocationReference().getY();
        positiveProbeNode.setOffset( x, y );
    }
    
    private void updateNegativeProbeLocation() {
        //XXX mvt
        double x = tester.getNegativeProbeLocationReference().getX() - tester.getLocationReference().getX();
        double y = tester.getNegativeProbeLocationReference().getY() - tester.getLocationReference().getY();
        negativeProbeNode.setOffset( x, y );
    }
    
    /*
     * Probes, origin at bottom center.
     */
    private static class ProbeNode extends PPath {
        
        public ProbeNode( PDimension size, Color color ) {
            setPathTo( new Rectangle2D.Double( -size.getWidth() / 2, -size.getHeight(), size.getWidth(), size.getHeight() ) );
            setStroke( PROBE_STROKE );
            setStrokePaint( PROBE_STROKE_COLOR );
            setPaint( color );
        }
    }
}
