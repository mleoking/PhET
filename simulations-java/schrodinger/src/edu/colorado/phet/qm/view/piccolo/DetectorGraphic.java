/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.qm.QWILookAndFeel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

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
    private boolean probDisplayAllowedToBeVisible = true;
    private QWIPanel qwiPanel;
    private Color enabledStrokeColor = new Color( 0, 185, 255 );

    public DetectorGraphic( final QWIPanel QWIPanel, final Detector detector ) {
        super( QWIPanel, detector, fill );
        this.qwiPanel = QWIPanel;
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
                updateDetectorReadouts();
                DetectorGraphic.this.update();
            }
        } );

        JButton closeButton = QWILookAndFeel.createCloseButton();
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIPanel.removeDetectorGraphic( DetectorGraphic.this );
            }
        } );
        closeGraphic = new PSwing( QWIPanel, closeButton );
        addChild( closeGraphic );

        updateDetectorReadouts();
        update();
    }

    private void updateDetectorReadouts() {
        qwiPanel.updateDetectorReadouts();
    }

    public void setCloseButtonVisible( boolean visible ) {
        closeGraphic.setVisible( visible );
    }

    public void setPercentDisplayVisible( boolean visible ) {
        this.probDisplayAllowedToBeVisible = visible;
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
            probDisplay.setVisible( detector.isEnabled() && probDisplayAllowedToBeVisible );
            getAreaGraphic().setStrokePaint( detector.isEnabled() ? enabledStrokeColor : Color.gray );
            closeGraphic.setOffset( (int)( getViewRectangle().x - closeGraphic.getWidth() ), getViewRectangle().y - closeGraphic.getHeight() );
        }
    }

    public Detector getDetector() {
        return detector;
    }
}
