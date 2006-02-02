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
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Dec 6, 2004
 * Time: 5:19:19 PM
 * Copyright (c) Dec 6, 2004 by Sam Reid
 */

public class TestPhetGraphicsStrategy extends JFrame {
    private ApparatusPanel panel;
    private IClock clock;

    static interface TestPhetGraphicSource {
        public PhetGraphic createGraphic( Component panel );
    }

    public TestPhetGraphicsStrategy() throws HeadlessException {
        super( "Test PhetGraphics" );
        panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        TestPhetGraphicSource[] graphics = new TestPhetGraphicSource[]{
                new TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( Component panel ) {
                        return new PhetTextGraphic( panel, new Font( "Lucida Sans", Font.BOLD, 24 ), "PhetGraphic Test", Color.blue, 100, 100 );
                    }
                },
                new TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( Component panel ) {
                        return new PhetShapeGraphic( panel, new Rectangle( 50, 50, 50, 50 ), Color.green, new BasicStroke( 1 ), Color.black );
                    }
                },
                new TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( Component panel ) {
                        return new PhetImageGraphic( panel, "images/Phet-Flatirons-logo-3-small.gif" );
                    }
                },
                new TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( Component panel ) {
                        return new PhetMultiLineTextGraphic( panel, new Font( "dialog", 0, 28 ), new String[]{"PhET", "Multi-", "Line", "TextGraphic"}, Color.red, 1, 1, Color.yellow );
                    }
                },
                new TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( Component panel ) {
                        return new PhetShadowTextGraphic( panel, new Font( "dialog", Font.BOLD, 28 ), "Shadowed", Color.blue, 1, 1, Color.green );
                    }
                },
                new TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( Component panel ) {
                        CompositePhetGraphic cpg = new CompositePhetGraphic( panel );
                        cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 130, 30, 30, 30 ), Color.red ) );
                        cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 160, 30, 30, 30 ), Color.blue ) );
                        PhetShadowTextGraphic graphic = new PhetShadowTextGraphic( panel, new Font( "Lucida Sans", 0, 12 ), "compositegraphic", Color.white, 1, 1, Color.black );
                        graphic.setLocation( 130, 30 );
                        cpg.addGraphic( graphic );

                        return cpg;
                    }
                },
                new TestPhetGraphicSource() {
                    public PhetGraphic createGraphic( Component panel ) {
                        Stroke stroke = new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4, new float[]{6, 6}, 0 );
                        final OutlineTextGraphic g = new OutlineTextGraphic( panel, "Outline Text", new Font( "Lucida Sans", Font.ITALIC, 68 ), 50, 300, Color.yellow, stroke, Color.black );
                        g.setBorderPaint( new GradientPaint( 0, 0, Color.red, 300, 300, Color.blue ) );
                        return g;
                    }
                }
        };
        final RepaintStrategy rs = new RepaintStrategy.ImmediateUnion( panel );
//        final RepaintStrategy rs=new RepaintStrategy.RepaintUnion( panel );
        Component fa = new RepaintStrategy.FalseComponent( rs );
        for( int i = 0; i < graphics.length; i++ ) {
            TestPhetGraphicSource graphic = graphics[i];
            final PhetGraphic pg = graphic.createGraphic( fa );
            pg.setCursorHand();
            pg.addMouseInputListener( new MouseInputAdapter() {
                // implements java.awt.event.MouseMotionListener
                public void mouseDragged( MouseEvent e ) {
                    if( SwingUtilities.isRightMouseButton( e ) ) {
                        Point ctr = RectangleUtils.getCenter( pg.getBounds() );
                        pg.transform( AffineTransform.getRotateInstance( Math.PI / 36, ctr.x, ctr.y ) );
                    }
                }
            } );
            pg.addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    if( SwingUtilities.isLeftMouseButton( translationEvent.getMouseEvent() ) ) {
                        pg.transform( AffineTransform.getTranslateInstance( translationEvent.getDx(), translationEvent.getDy() ) );
                    }
                }
            } );
            panel.addGraphic( pg );
        }

        setContentPane( panel );
        setSize( 600, 600 );
        panel.requestFocus();
        panel.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                panel.requestFocus();
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
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        panel.addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
                rs.paintImmediately();
            }

            public void mouseMoved( MouseEvent e ) {
            }
        } );
    }


    public static void main( String[] args ) {
        new TestPhetGraphicsStrategy().start();
    }

    private void start() {
        clock.start();
        panel.requestFocus();
        setVisible( true );
    }

    public static class OutlineTextGraphic extends PhetShapeGraphic {
        private String text;
        private Font font;

        public OutlineTextGraphic( Component component, String text, Font font, int x, int y, Color fillColor, Stroke stroke, Color strokeColor ) {
            super( component );
            this.text = text;
            this.font = font;
            setShape( createTextShape() );
            setColor( fillColor );
            setStroke( stroke );
            setBorderColor( strokeColor );
            component.addComponentListener( new ComponentAdapter() {
                public void componentShown( ComponentEvent e ) {
                    setShape( createTextShape() );
                }
            } );
            component.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    setShape( createTextShape() );
                }
            } );
            setLocation( x, y );
        }

        private Shape createTextShape() {
            Graphics2D g2 = (Graphics2D)getComponent().getGraphics();
            if( g2 != null ) {
                FontRenderContext frc = g2.getFontRenderContext();
                if( frc != null ) {
                    TextLayout textLayout = new TextLayout( text, font, frc );
                    return textLayout.getOutline( new AffineTransform() );
                }
            }
            return null;
        }

    }

}