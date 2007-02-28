/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 2, 2005
 * Time: 6:57:30 PM
 * Copyright (c) Feb 2, 2005 by Sam Reid
 */

public class Button3D extends CompositePhetGraphic {
    private double shearX = -0.35;
    private int origDepth = 7;
    private String text;
    private Paint enterColor = Color.orange;
    private Paint buttonColor;
    private PhetShapeGraphic downRectGraphic;
    private PhetShapeGraphic sideGraphic;
    private static int id = 0;
    private PhetShapeGraphic boundGraphic;
    private PhetShapeGraphic baseGraphic;
    private PhetTextGraphic textGraphic;
    private boolean pressed = false;

    public Button3D( Component component, String text ) {
        super( component );
        this.text = text;

        Font font = new Font( "Lucida Sans", Font.BOLD, 12 );
        textGraphic = new PhetTextGraphic( component, font, text, Color.black, 0, 0 );
        Rectangle origBounds = new Rectangle( textGraphic.getBounds() );
        origBounds = RectangleUtils.expand( origBounds, 3, 3 );
        textGraphic.shear( shearX, 0 );

        AbstractVector2D downleft = new ImmutableVector2D.Double( shearX, 1 );
        downleft = downleft.getNormalizedInstance();

        DoubleGeneralPath outlinePath = new DoubleGeneralPath( origBounds.x, origBounds.y );
        outlinePath.lineToRelative( downleft.getInstanceOfMagnitude( origBounds.height ) );
        Point2D BL = outlinePath.getCurrentPoint();
        outlinePath.lineToRelative( origBounds.width, 0 );
        Point2D BR = outlinePath.getCurrentPoint();
        outlinePath.lineToRelative( downleft.getScaledInstance( -1 ).getInstanceOfMagnitude( origBounds.height ) );
        Point2D TR = outlinePath.getCurrentPoint();
        outlinePath.lineTo( origBounds.x, origBounds.y );

        Rectangle2D downRect = new Rectangle2D.Double( BL.getX(), BL.getY(), BR.getX() - BL.getX(), origDepth );
        Color lightRed = new Color( 200, 20, 40 );
        Color lightGreen = new Color( 120, 200, 80 );
        Color lightWhite = new Color( 240, 230, 255 );
        buttonColor = new GradientPaint( origBounds.x, origBounds.y, lightRed, origBounds.x + origBounds.width, origBounds.y + origBounds.height, lightWhite );
        this.enterColor = new GradientPaint( origBounds.x, origBounds.y, lightGreen, origBounds.x + origBounds.width, origBounds.y + origBounds.height, lightWhite );
        this.downRectGraphic = new PhetShapeGraphic( component, downRect, buttonColor, new BasicStroke( 1 ), Color.black );
        this.boundGraphic = new PhetShapeGraphic( component, outlinePath.getGeneralPath(), buttonColor, new BasicStroke( 1 ), Color.black );

        DoubleGeneralPath sidePath = new DoubleGeneralPath( TR );
        sidePath.lineTo( BR );
        sidePath.lineToRelative( 0, origDepth );
        sidePath.lineToRelative( downleft.getScaledInstance( -1 ).getInstanceOfMagnitude( origBounds.height ) );
        sidePath.lineTo( TR );

        sideGraphic = new PhetShapeGraphic( component, sidePath.getGeneralPath(), buttonColor, new BasicStroke( 1 ), Color.black );

        int baseDX = 4;
        int baseDY = baseDX;
        Point2D outerBL = new Point2D.Double( BL.getX() - baseDX, BL.getY() + baseDY + origDepth );
        DoubleGeneralPath base = new DoubleGeneralPath( outerBL );
        base.lineToRelative( downleft.getScaledInstance( -1 ).getInstanceOfMagnitude( origBounds.height + baseDY * 2 ) );
        base.lineToRelative( origBounds.width + baseDX * 2, 0 );
        base.lineToRelative( downleft.getScaledInstance( 1 ).getInstanceOfMagnitude( origBounds.height + baseDY * 2 ) );
        base.lineTo( outerBL );

        this.baseGraphic = new PhetShapeGraphic( component, base.getGeneralPath(), Color.blue, new BasicStroke( 1 ), Color.black );
        addGraphic( baseGraphic );
        addGraphic( boundGraphic );

        addGraphic( downRectGraphic );
        addGraphic( sideGraphic );
        addGraphic( textGraphic );

        addMouseInputListener( new MouseInputAdapter() {
            public void mouseEntered( MouseEvent e ) {
                boundGraphic.setPaint( enterColor );
                downRectGraphic.setPaint( enterColor );
                sideGraphic.setPaint( enterColor );
            }

            public void mouseExited( MouseEvent e ) {
                boundGraphic.setPaint( buttonColor );
                downRectGraphic.setPaint( buttonColor );
                sideGraphic.setPaint( buttonColor );
            }

            public void mousePressed( MouseEvent e ) {
                press();
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                release( e );
            }
        } );
        setCursorHand();
        setRegistrationPoint( -13, -7 );
    }

    private void press() {
        if( !pressed ) {
            pressed = true;
            downRectGraphic.setVisible( false );
            sideGraphic.setVisible( false );
            boundGraphic.translate( 0, origDepth );
            textGraphic.translate( 0, origDepth );
        }
    }

    private void release( MouseEvent e ) {
        if( pressed ) {
            pressed = false;
            downRectGraphic.setVisible( true );
            sideGraphic.setVisible( true );
            boundGraphic.translate( 0, -origDepth );
            textGraphic.translate( 0, -origDepth );
            ActionEvent event = new ActionEvent( Button3D.this, id++, "pressed", e.getWhen(), e.getModifiers() );
            for( int i = 0; i < listeners.size(); i++ ) {
                java.awt.event.ActionListener actionListener = (java.awt.event.ActionListener)listeners.get( i );
                actionListener.actionPerformed( event );
            }
        }
    }

    private ArrayList listeners = new ArrayList();

    public void addActionListener( ActionListener actionListener ) {
        listeners.add( actionListener );
    }
}
