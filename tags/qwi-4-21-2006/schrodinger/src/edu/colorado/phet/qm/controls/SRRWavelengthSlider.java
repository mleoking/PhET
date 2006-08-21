/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 9:23:32 PM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class SRRWavelengthSlider extends PNode {
    private SpectrumSliderKnob spectrumSliderKnob;
    private Function.LinearFunction linearFunction;
    private PImage colorBackgroundNode;
    private PText phetTextGraphic;
    private PPath boundGraphic;

    public SRRWavelengthSlider( Component component ) {
        double minWavelength = VisibleColor.MIN_WAVELENGTH;
        double maxWavelength = VisibleColor.MAX_WAVELENGTH;

        final BufferedImage image = new BufferedImage( 175, 30, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        linearFunction = new Function.LinearFunction( 0, image.getWidth(), minWavelength, maxWavelength );
        for( int i = 0; i < image.getWidth(); i++ ) {
            double wavelength = linearFunction.evaluate( i );
            VisibleColor visibleColor = new VisibleColor( wavelength );
            g2.setColor( visibleColor );
            g2.fillRect( i, 0, 1, image.getHeight() );
        }
        g2.dispose();
        colorBackgroundNode = new PImage( image );
        spectrumSliderKnob = new SpectrumSliderKnob( component, new Dimension( 20, 20 ), 0 );
        spectrumSliderKnob.setOffset( 0, image.getHeight() );
        spectrumSliderKnob.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        spectrumSliderKnob.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseEntered( PInputEvent event ) {
                System.out.println( "SRRWavelengthSlider.mouseEntered" );
            }

            public void mouseDragged( PInputEvent event ) {
                super.mouseDragged( event );
                Point2D pt = event.getPositionRelativeTo( SRRWavelengthSlider.this );
                double newX = (int)MathUtil.clamp( 0, pt.getX(), image.getWidth() );
                spectrumSliderKnob.setOffset( new Point2D.Double( newX, image.getHeight() + getTextOffsetY() ) );
                dragPointChanged();
            }
        } );
        phetTextGraphic = new PText( "" );

        colorBackgroundNode.setOffset( 0, getTextOffsetY() );
        spectrumSliderKnob.setOffset( image.getWidth() / 2, image.getHeight() + getTextOffsetY() );
        addChild( phetTextGraphic );
        addChild( colorBackgroundNode );
        addChild( spectrumSliderKnob );
        layoutChildren();
        boundGraphic = new PPath( getFullBounds() );
        boundGraphic.setPaint( new JLabel().getBackground() );
        boundGraphic.setStrokePaint( null );
        boundGraphic.setPickable( false );
        boundGraphic.setChildrenPickable( false );
        addChild( 0, boundGraphic );

        dragPointChanged();
        setOpaque( false );
    }

    private double getTextOffsetY() {
        return phetTextGraphic.getHeight() + 5;
    }

    public VisibleColor getVisibleColor() {
        return new VisibleColor( getWavelength() );
    }

    private void dragPointChanged() {
        double wavelength = getWavelength();
        spectrumSliderKnob.setPaint( new VisibleColor( wavelength ) );
        ChangeEvent e = new ChangeEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)listeners.get( i );
            changeListener.stateChanged( e );
        }
    }

    public void setWavelength( double wavelength ) {
        double x = linearFunction.createInverse().evaluate( wavelength );
        spectrumSliderKnob.setOffset( x, spectrumSliderKnob.getOffset().getY() );
        dragPointChanged();
    }

    public double getWavelength() {
        double x = spectrumSliderKnob.getOffset().getX();
        return linearFunction.evaluate( x );
    }

    private ArrayList listeners = new ArrayList();

    public void addChangeListener( ChangeListener changeListener ) {
        listeners.add( changeListener );
    }

    public void removeChangeListener( ChangeListener changeListener ) {
        listeners.remove( changeListener );
    }

    public void setOpaque( boolean opaque ) {
        boundGraphic.setVisible( opaque );
    }
}
