/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.tests.graphics;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.BasicGraphicsSetup;
import edu.colorado.phet.common.view.util.GraphicsSetup;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Dec 6, 2004
 * Time: 5:19:19 PM
 * Copyright (c) Dec 6, 2004 by Sam Reid
 */

public class TestSingleGraphicBuffer extends JFrame {
    private ApparatusPanel panel;
    private IClock clock;
    private OutlineTextGraphic outlineTextGraphic;

    static interface TestPhetGraphicSource {
        public PhetGraphic createGraphic( ApparatusPanel panel );
    }

    public TestSingleGraphicBuffer() throws HeadlessException {
        super( "Test PhetGraphics" );
        panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        TestPhetGraphicSource[] graphics = new TestPhetGraphicSource[]{
                new TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( ApparatusPanel panel ) {
                        Stroke stroke = new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4, new float[]{6, 6}, 0 );
                        Font font = new Font( "Lucida Sans", Font.ITALIC, 68 );
                        outlineTextGraphic = new OutlineTextGraphic( panel, "Outline Text", font, Color.yellow, stroke, Color.black );
                        outlineTextGraphic.setBorderPaint( new GradientPaint( 0, 0, Color.red, 300, 300, Color.blue ) );
                        return outlineTextGraphic;
                    }
                },
                new TestSingleGraphicBuffer.TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( ApparatusPanel panel ) {
                        return new PhetImageGraphic( panel, "images/Phet-Flatirons-logo-3-small.gif" );
                    }
                }
        };
//        final BufferedPhetGraphic2 bufferedPhetGraphic = new BufferedPhetGraphic2( panel, Color.yellow );
        GraphicLayerSet gls = new GraphicLayerSet( panel );
        for( int i = 0; i < graphics.length; i++ ) {
            TestPhetGraphicSource graphic = graphics[i];
            final PhetGraphic pg = graphic.createGraphic( panel );

//            Paint paint=Color.blue;
            Paint paint = new Color( 255, 255, 255, 0 );
            GraphicsSetup graphicsSetup = new BasicGraphicsSetup();
            final PhetGraphic buffered = BufferedPhetGraphic2.createBuffer( pg, graphicsSetup, BufferedImage.TYPE_INT_ARGB, paint );
//            pg=buffered;
            buffered.setCursorHand();
            buffered.addMouseInputListener( new MouseInputAdapter() {
                // implements java.awt.event.MouseMotionListener
                public void mouseDragged( MouseEvent e ) {
                    if( SwingUtilities.isRightMouseButton( e ) ) {
                        Point ctr = RectangleUtils.getCenter( buffered.getBounds() );
                        buffered.transform( AffineTransform.getRotateInstance( Math.PI / 36, ctr.x, ctr.y ) );
                    }
                }
            } );
            buffered.addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    if( SwingUtilities.isLeftMouseButton( translationEvent.getMouseEvent() ) ) {
                        buffered.transform( AffineTransform.getTranslateInstance( translationEvent.getDx(), translationEvent.getDy() ) );
                    }
                }
            } );
//            bufferedPhetGraphic.addGraphic( pg );

            gls.addGraphic( buffered );
        }
        panel.addGraphic( gls );

        setContentPane( panel );
        setSize( 600, 600 );
        panel.requestFocus();
        panel.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                panel.requestFocus();
            }
        } );
        panel.addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
//                bufferedPhetGraphic.repaintBuffer();
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        clock = new SwingClock( 30, 1.0 );
        panel.addGraphic( new PhetShapeGraphic( panel, new Rectangle( 5, 5, 5, 5 ), Color.black ) );
        final RepaintDebugGraphic rdg = new RepaintDebugGraphic( panel, clock );
        panel.addGraphic( rdg );

        rdg.setActive( false );
        rdg.setVisible( false );

        panel.addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
                    rdg.setActive( !rdg.isActive() );
                    rdg.setVisible( rdg.isActive() );
                }
                else if( e.getKeyCode() == KeyEvent.VK_R ) {
//                    bufferedPhetGraphic.repaintBuffer();
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    }


    public static void main( String[] args ) {
        new TestSingleGraphicBuffer().start();
    }

    private void start() {
        clock.start();
        panel.requestFocus();
        setVisible( true );
    }

    public static class OutlineTextGraphic extends PhetShapeGraphic {
        private String text;
        private Font font;

        public OutlineTextGraphic( Component component, String text, Font font, Color fillColor, Stroke stroke, Color strokeColor ) {
            super( component );
            this.text = text;
            this.font = font;
            setShape( createTextShape() );
            setColor( fillColor );
            setStroke( stroke );
            setBorderColor( strokeColor );
//            component.addComponentListener( new ComponentAdapter() {
//                public void componentShown( ComponentEvent e ) {
//                    setShape( createTextShape() );
//                }
//            } );
//            component.addComponentListener( new ComponentAdapter() {
//                public void componentResized( ComponentEvent e ) {
//                    setShape( createTextShape() );
//                }
//            } );
//            setRegistrationPoint( );
//            setLocation( x, y );
        }

        private Shape createTextShape() {
//            Graphics2D g2 = (Graphics2D)getComponent().getGraphics();
//            if( g2 != null ) {
//                FontRenderContext frc = g2.getFontRenderContext();
            FontRenderContext frc = new FontRenderContext( new AffineTransform(), false, false );
            if( frc != null ) {
                TextLayout textLayout = new TextLayout( text, font, frc );
                return textLayout.getOutline( new AffineTransform() );
//                }
            }
            return null;
        }

    }

}