/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.model.Detector;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class DetectorGraphic extends RectangleGraphic {
    private Detector detector;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    private PText probDisplay;
    private PhetGraphic closeGraphic;
    private Color darkGreen;
//    private static Color fill = new Color( 200, 180, 150, 65 );
    private static Color fill = new Color( 200, 180, 150, 0 );
//    private static Color fill = null;

    public DetectorGraphic( final SchrodingerPanel schrodingerPanel, final Detector detector ) {
//        super( schrodingerPanel, detector, new Color( 0, 0, 0, 0 ) );
        super( schrodingerPanel, detector, fill );
        this.detector = detector;

        darkGreen = new Color( 50, 230, 75 );
        probDisplay = new PText();
        // todo piccolo
        // schrodingerPanel, new Font( "Lucida Sans", Font.BOLD, 14 ), "", darkGreen
        probDisplay.setPickable( false );
        probDisplay.setChildrenPickable( false );
        addChild( probDisplay );
        detector.addObserver( new SimpleObserver() {
            public void update() {
                DetectorGraphic.this.update();
            }
        } );

        //todo piccolo
//        JButton closeButton = SchrodingerLookAndFeel.createCloseButton();
//        closeButton.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                schrodingerPanel.removeDetectorGraphic( DetectorGraphic.this );
//            }
//        } );
//        closeGraphic = PhetJComponent.newInstance( schrodingerPanel, closeButton );
//        addChild( closeGraphic );

        update();
    }

    public void setCloseButtonVisible( boolean visible ) {
        closeGraphic.setVisible( visible );
    }

    public void setPercentDisplayVisible( boolean visible ) {
        probDisplay.setVisible( visible );
    }

    private void update() {
        Rectangle viewRect = super.getViewRectangle( detector.getBounds() );

        double probPercent = detector.getProbability() * 100.0;
//        System.out.println( "probPercent = " + probPercent );
        String formatted = format.format( probPercent );
        probDisplay.setText( formatted + " %" );
        probDisplay.setOffset( (int)viewRect.getX(), (int)viewRect.getY() );
        if( detector.isEnabled() ) {
            probDisplay.setTextPaint( darkGreen );
        }
        else {
            probDisplay.setTextPaint( Color.gray );
        }
        closeGraphic.setLocation( (int)( viewRect.getX() - closeGraphic.getWidth() ), (int)viewRect.getY() - closeGraphic.getHeight() );
    }

    public Detector getDetector() {
        return detector;
    }
}
