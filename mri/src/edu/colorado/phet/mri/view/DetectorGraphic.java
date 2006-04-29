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

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.mri.model.Detector;
import edu.colorado.phet.mri.util.RoundGradientPaint;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.common.util.SimpleObserver;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;

/**
 * DetectorGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DetectorGraphic extends PNode implements SimpleObserver {
    private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
    private Paint detectorPaint;
    private PPath displayPNode;
    private Detector detector;

    public DetectorGraphic( Detector detector ) {
        detector.addObserver( this );
        this.detector = detector;
        RoundRectangle2D bezel = new RoundRectangle2D.Double( 0,
                                                         0,
                                                         detector.getBounds().getWidth(),
                                                         detector.getBounds().getHeight(),
                                                         15,
                                                         15 );
        PPath bezelPNode = new PPath( bezel );
        bezelPNode.setStroke( new BasicStroke( 3 ) );
        bezelPNode.setPaint( new Color( 150, 150, 150 ) );
        addChild( bezelPNode );

        PText label = new PText( "Detector" );
        label.setFont( font );
        label.setTextPaint( Color.white );
        label.rotate( -Math.PI / 2 );
        label.setJustification( JLabel.CENTER_ALIGNMENT );
        label.setOffset( 5, bezel.getHeight() / 2 );
        RegisterablePNode labelPNode = new RegisterablePNode( label );
        labelPNode.setRegistrationPoint( 0, -label.getWidth() / 2 );
        addChild( labelPNode );

        double inset = 30;
        RoundRectangle2D display = new RoundRectangle2D.Double( inset, inset,
                                                      detector.getBounds().getWidth() - inset * 2,
                                                      detector.getBounds().getHeight() - inset * 2,
                                                      10,
                                                      10);
        displayPNode = new PPath( display );
        displayPNode.setStroke( new BasicStroke( 3 ) );
        displayPNode.setStrokePaint( Color.black );
        displayPNode.setPaint( Color.white );
        addChild( displayPNode );

        setOffset( detector.getBounds().getX(), detector.getBounds().getY() );
    }

    public void update() {
        double ratio = Math.max( 1 - (double)detector.getNumDetected() / 30, 0 );
        System.out.println( "ratio = " + ratio );
        Color color = new Color( (int)(ratio * 255), (int)(ratio * 255), (int)(ratio * 255) );
        System.out.println( "color = " + color );
        double x = detector.getBounds().getWidth() / 2;
        double y = detector.getBounds().getHeight() / 2;

        RoundGradientPaint paint = new RoundGradientPaint( x, y, color, new Point2D.Double( 0, 20), Color.white );
        displayPNode.setPaint( paint );
    }
}
