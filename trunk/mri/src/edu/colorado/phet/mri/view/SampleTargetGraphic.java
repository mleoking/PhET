/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.mri.model.SampleTarget;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * SampleTargetGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleTargetGraphic extends PNode implements SampleTarget.ChangeListener {
    private static double LINE_LENGTH = 50;

    public SampleTargetGraphic( SampleTarget sampleTarget ) {

        sampleTarget.addChangeListener( this );

        Line2D horizontalLine = new Line2D.Double( -LINE_LENGTH / 2, 0, LINE_LENGTH / 2, 0 );
        Line2D verticalLine = new Line2D.Double( 0, -LINE_LENGTH / 2, 0, LINE_LENGTH / 2 );
        PPath horizontalLinePNode = new PPath( horizontalLine );
        PPath verticalLinePNode = new PPath( verticalLine );
        addChild( horizontalLinePNode );
        addChild( verticalLinePNode );
        Color lineColor = Color.black;
//        Color lineColor = new Color( 180, 0, 0);
        horizontalLinePNode.setStrokePaint( lineColor );
        verticalLinePNode.setStrokePaint( lineColor );

        Ellipse2D ring = new Ellipse2D.Double( -LINE_LENGTH / 3, -LINE_LENGTH / 3, LINE_LENGTH * 2 / 3, LINE_LENGTH * 2 / 3 );
        PPath ringPNode = new PPath( ring );
        addChild( ringPNode );
        Color ringColor = new Color( 0, 160, 0 );
        ringPNode.setStroke( new BasicStroke( 5 ) );
        ringPNode.setStrokePaint( ringColor );

        addMouseHandling( sampleTarget );

        update( sampleTarget );
    }

    private void addMouseHandling( final SampleTarget sampleTarget ) {
        this.addInputEventListener( new PBasicInputEventHandler() {

            public void mouseEntered( PInputEvent event ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( new Cursor( Cursor.MOVE_CURSOR ) );
            }

            public void mouseExited( PInputEvent event ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( Cursor.getDefaultCursor() );
            }

            public void mouseDragged( PInputEvent event ) {
                Point2D p = getOffset();
                setOffset( p.getX() + event.getDelta().getWidth(), p.getY() + event.getDelta().getHeight() );
                sampleTarget.setLocation( getOffset() );
            }
        } );

    }

    private void update( SampleTarget sampleTarget ) {
        setOffset( sampleTarget.getX(), sampleTarget.getY() );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SampleTarget.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void stateChanged( SampleTarget.ChangeEvent event ) {
        update( event.getSampleTarget() );
    }

}
