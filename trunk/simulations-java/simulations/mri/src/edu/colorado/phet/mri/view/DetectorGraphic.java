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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.Detector;
import edu.colorado.phet.mri.util.RoundGradientPaint;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

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
            grayScale[i] = new Color( i, i, i );
        }
    }

    static RoundGradientPaint[] gradientPaints = new RoundGradientPaint[256];

    private Font font = new PhetFont( Font.BOLD, 16 );
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

        PText label = new PText( MriResources.getString( "Misc.Detector" ) );
        label.setFont( font );
        label.setTextPaint( Color.white );
        label.rotate( -Math.PI / 2 );
        label.setJustification( JLabel.CENTER_ALIGNMENT );
        label.setOffset( 5, bezel.getHeight() / 2 );
        RegisterablePNode labelPNode = new RegisterablePNode( label );
        labelPNode.setRegistrationPoint( 0, -label.getWidth() / 2 );

        double displaySize = 40;
        RoundRectangle2D display = new RoundRectangle2D.Double( detector.getBounds().getWidth() + 20,
                                                                detector.getBounds().getHeight() / 2 - displaySize / 2,
                                                                displaySize,
                                                                displaySize,
                                                                10,
                                                                10 );

        double connectorThickness = 20;
        Rectangle2D connector = new Rectangle2D.Double( detector.getBounds().getWidth() / 2,
                                                        detector.getBounds().getHeight() / 2 - connectorThickness / 2,
                                                        display.getBounds2D().getX(),
                                                        connectorThickness );
        PPath connectorPNode = new PPath( connector );
        connectorPNode.setStroke( new BasicStroke( 3 ) );
        connectorPNode.setStrokePaint( Color.black );
        connectorPNode.setPaint( new Color( 150, 150, 150 ) );

        displayPNode = new PPath( display );
        displayPNode.setStroke( new BasicStroke( 3 ) );
        displayPNode.setStrokePaint( Color.black );
        displayPNode.setPaint( Color.white );

        addChild( connectorPNode );
        addChild( bezelPNode );
        addChild( labelPNode );
        addChild( displayPNode );

        setOffset( detector.getBounds().getX(), detector.getBounds().getY() );

        // Initialize the gradient paints
        double x = display.getBounds().getMinX() + display.getBounds().getWidth() / 2;
        double y = display.getBounds().getMinY() + display.getBounds().getHeight() / 2;
        for( int i = 0; i < gradientPaints.length; i++ ) {
            double radius = ( display.getBounds().getWidth() - display.getBounds().getWidth() * i / gradientPaints.length );
            gradientPaints[i] = new RoundGradientPaint( x, y,
                                                        grayScale[i],
                                                        new Point2D.Double( 0, radius ),
                                                        Color.white );
            ;
        }
    }

    public void update() {
        double ratio = Math.max( 1 - (double)detector.getNumDetected() / 30, 0 );
        Paint paint = gradientPaints[(int)( ratio * 255 )];
        displayPNode.setPaint( paint );
    }
}
