/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples;


/**
 * User: Sam Reid
 * Date: Dec 20, 2003
 * Time: 4:17:19 AM
 * Copyright (c) Dec 20, 2003 by Sam Reid
 */
public class TestGraphics {
//    private static boolean didInit;
//
//    public static void main( String[] args ) {
//        final ApparatusPanel panel = new ApparatusPanel();
//        final Font font = new Font( "Lucida Sans", 0, 68 );
//        final Area area = new Area( new Ellipse2D.Double( 100, 100, 100, 100 ) );
//        final Area area2 = new Area( new Ellipse2D.Double( 100, 100, 100, 100 ) );
//        Graphic init = new Graphic() {
//            public void paint( Graphics2D g ) {
//                if( !didInit ) {
//                    GlyphVector gv = font.createGlyphVector( g.getFontRenderContext(), "PhET" );
//                    area.subtract( area );
//                    area.add( new Area( gv.getOutline() ) );
//                    AffineTransform at = AffineTransform.getTranslateInstance( 50, 50 );
//                    area.transform( at );
//                    didInit = true;
//                }
//            }
//        };
//        Graphic rend = new Graphic() {
//            public void paint( Graphics2D g ) {
//                GraphicsUtil.setAntiAliasingOn( g );
//            }
//        };
//        panel.addGraphic( rend );
//        panel.addGraphic( init );
//
//        ShapeGraphic sg = new ShapeGraphic( area, Color.red );
//        //        ShapeBoundary sb = new ShapeBoundary( area );
//        final DefaultInteractiveGraphic graphic = new DefaultInteractiveGraphic( sg );
//        final JCheckBox visible = new JCheckBox( "Visible", true );
//        visible.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                graphic.setVisible( visible.isSelected() );
//            }
//        } );
//        panel.add( visible );
//        visible.reshape( 200, 200, 80, 80 );
//        panel.addGraphic( graphic );
//
//        //        HandCursorControl hand=new HandCursorControl();
//        graphic.addCursorHandBehavior();
//        graphic.addTranslationBehavior( new Translatable() {
//            public void translate( double dx, double dy ) {
//                area.transform( AffineTransform.getTranslateInstance( dx, dy ) );
//                panel.repaint();
//            }
//        } );
//        graphic.addMouseInputListener( new BringToFront( panel.getGraphic(), graphic ) );
//
//        DefaultInteractiveGraphic circlo = new DefaultInteractiveGraphic( new ShapeGraphic( area2, Color.green ) );
//        circlo.addCursorHandBehavior();
//
//        circlo.addMouseInputListener( new BringToFront( panel.getGraphic(), circlo ) );
//        circlo.addTranslationBehavior( new Translatable() {
//            public void translate( double dx, double dy ) {
//                area2.transform( AffineTransform.getTranslateInstance( dx, dy ) );
//                panel.repaint();
//            }
//        } );
//        panel.addGraphic( circlo );
//
//        JFrame jf = new JFrame();
//        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//        jf.setContentPane( panel );
//        jf.setSize( 400, 400 );
//        jf.setVisible( true );
//
//    }
}
