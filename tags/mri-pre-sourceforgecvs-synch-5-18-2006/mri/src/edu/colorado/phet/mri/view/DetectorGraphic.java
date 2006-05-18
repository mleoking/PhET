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

    static Color[] grayScale = new Color[256];
    static {
        for( int i = 0; i < grayScale.length; i++ ) {
            grayScale[i] = new Color( i,i,i);
        }
    }
    static RoundGradientPaint[] gradientPaints =  new RoundGradientPaint[256];

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
        double displaySize = detector.getBounds().getWidth() - inset * 2;
        RoundRectangle2D display = new RoundRectangle2D.Double( detector.getBounds().getWidth() / 2  -displaySize / 2,
                                                                detector.getBounds().getHeight() / 2 -displaySize / 2,
                                                                displaySize,
                                                                displaySize,
                                                                10,
                                                                10 );
        displayPNode = new PPath( display );
        displayPNode.setStroke( new BasicStroke( 3 ) );
        displayPNode.setStrokePaint( Color.black );
        displayPNode.setPaint( Color.white );
        addChild( displayPNode );

        setOffset( detector.getBounds().getX(), detector.getBounds().getY() );

        // Initialize the gradient paints
        double x = detector.getBounds().getWidth() / 2;
        double y = detector.getBounds().getHeight() / 2;
        for( int i = 0; i < gradientPaints.length; i++ ) {
//            double radius = (gradientPaints.length - i) * 0.5;
            double radius = (detector.getBounds().getWidth() - detector.getBounds().getWidth() * i / gradientPaints.length ) / 2;
            gradientPaints[i] = new RoundGradientPaint( x, y,
                                                        grayScale[i],
                                                        new Point2D.Double( 0, radius ),
//                                                        new Point2D.Double( 0, 20 ),
                                                        Color.white );;
        }
    }

    public void update() {
        double ratio = Math.max( 1 - (double)detector.getNumDetected() / 30, 0 );
        Paint paint = gradientPaints[ (int)( ratio * 255)];
        displayPNode.setPaint( paint );
    }
}
