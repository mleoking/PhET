/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.SchrodingerLookAndFeel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private PSwing closeGraphic;
    private Color darkGreen;
    private static Color fill = new Color( 200, 180, 150, 0 );

    public DetectorGraphic( final SchrodingerPanel schrodingerPanel, final Detector detector ) {
        super( schrodingerPanel, detector, fill );
        this.detector = detector;

        darkGreen = new Color( 50, 230, 75 );
        probDisplay = new PText();
        probDisplay.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        probDisplay.setTextPaint( darkGreen );
        probDisplay.setPickable( false );
        probDisplay.setChildrenPickable( false );
        addChild( probDisplay );
        detector.addObserver( new SimpleObserver() {
            public void update() {
                DetectorGraphic.this.update();
            }
        } );

        JButton closeButton = SchrodingerLookAndFeel.createCloseButton();
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                schrodingerPanel.removeDetectorGraphic( DetectorGraphic.this );
            }
        } );
        closeGraphic = new PSwing( schrodingerPanel, closeButton );
        addChild( closeGraphic );

        update();
    }

    public void setCloseButtonVisible( boolean visible ) {
        closeGraphic.setVisible( visible );
    }

    public void setPercentDisplayVisible( boolean visible ) {
        probDisplay.setVisible( visible );
    }

    protected Rectangle getViewRectangle() {
        return super.getViewRectangle( detector.getBounds() );
    }

    protected void update() {
        super.update();
        if( probDisplay != null ) {
            String formatted = format.format( detector.getProbability() * 100.0 );
            probDisplay.setText( formatted + " %" );
            probDisplay.setOffset( getViewRectangle().x, getViewRectangle().y );
            probDisplay.setVisible( detector.isEnabled() );
            getAreaGraphic().setStrokePaint( detector.isEnabled() ? Color.blue : Color.gray );
            closeGraphic.setOffset( (int)( getViewRectangle().x - closeGraphic.getWidth() ), getViewRectangle().y - closeGraphic.getHeight() );
        }
    }

    public Detector getDetector() {
        return detector;
    }
}
