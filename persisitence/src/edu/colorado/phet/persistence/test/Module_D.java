/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.tests.graphics.TestPhetGraphics;
import edu.colorado.phet.persistence.test.view.ParticleGraphic;
import edu.colorado.phet.persistence.test.view.SimpleGraphic;
import edu.colorado.phet.persistence.test.model.TestModel;
import edu.colorado.phet.persistence.test.model.TestParticle;
import edu.colorado.phet.common.util.persistence.PersistentGeneralPath;
import edu.colorado.phet.common.util.persistence.PersistentStroke;
import edu.colorado.phet.common.util.persistence.*;

import javax.swing.event.MouseInputAdapter;
import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serializable;

/**
 * Module_A
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Module_D extends Module implements Serializable {

    public Module_D( ApplicationModel appModel ) {
        super( "D" );

        // Create the model
        BaseModel model = new TestModel();
        setModel( model );

        // Create the apparatus panel
//        ApparatusPanel2 ap = new ApparatusPanel2( model, appModel.getClock() );
//        ap.setUseOffscreenBuffer( true );
        ApparatusPanel ap = new ApparatusPanel();
        setApparatusPanel( ap );

        // Add an image graphic
        PhetImageGraphic pig = new PhetImageGraphic( getApparatusPanel(), "images/Phet-Flatirons-logo-3-small.gif" );
        pig.setLocation( 200, 100 );
//        ap.addGraphic( pig );

        // Add a text graphic
        PhetTextGraphic ptg = new PhetTextGraphic( ap, new Font( "Lucida-sans", Font.PLAIN, 48 ),
                                                   "Yada Yada Yada!", Color.ORANGE, 50, 50 );
//        ap.addGraphic( ptg );

        // Add a composite graphic
        CompositePhetGraphic cpg = new CompositePhetGraphic( ap );
        SimpleGraphic simpleGraphic = new SimpleGraphic( ap );
        simpleGraphic.setLocation( 50, 50 );
        cpg.addGraphic( simpleGraphic );
        PhetImageGraphic pig2 = new PhetImageGraphic( ap, "images/Phet-logo-48x48.gif" );
        pig2.setLocation( 10, 10 );
        cpg.addGraphic( pig2 );
        cpg.setLocation( 350, 250 );
//        ap.addGraphic( cpg );

        init();
    }

//
    void init() {
        ApparatusPanel panel = getApparatusPanel();
//            panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );

        PhetGraphic pg1 = new PhetTextGraphic( panel, new Font( "Lucida Sans", Font.BOLD, 24 ), "PhetGraphic Test", Color.blue, 100, 100 );
        PhetGraphic pg2 = new PhetShapeGraphic( panel, new PersistentRectangle2D( new Rectangle( 50, 50, 50, 50 ) ), Color.green, new BasicStroke( 1 ), Color.black );
        PhetGraphic pg3 = new PhetImageGraphic( panel, "images/Phet-Flatirons-logo-3-small.gif" );
        PhetGraphic pg4 = new PhetMultiLineTextGraphic( panel, new String[]{"PhET", "Multi-", "Line", "TextGraphic"}, new Font( "dialog", 0, 28 ), 0, 0, Color.red, 1, 1, Color.yellow );
        PhetGraphic pg5 = new PhetShadowTextGraphic( panel, "Shadowed", new Font( "dialog", Font.BOLD, 28 ), 0, 0, Color.blue, 1, 1, Color.green );
        PhetGraphic pg6 = new PhetShapeGraphic( panel, new PersistentEllipse2D( new Ellipse2D.Double( 150, 50, 50, 150 ) ), Color.green, new BasicStroke( 1 ), Color.black );

        CompositePhetGraphic cpg = new CompositePhetGraphic( panel );
        cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 130, 30, 30, 30 ), Color.red ) );
        cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 160, 30, 30, 30 ), Color.blue ) );
//        cpg.addGraphic( new PhetShadowTextGraphic( panel, "compositegraphic", new Font( "Lucida Sans", 0, 12 ), 130, 30, Color.white, 1, 1, Color.black ) );

        BasicStroke stroke = new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2 );//, new float[]{6, 6}, 0 );
        PersistentStroke pStroke = new PersistentStroke( stroke );
        OutlineTextGraphic g = new OutlineTextGraphic( panel, "Outline Text", new Font( "Lucida Sans", Font.ITALIC, 68 ),
                                                       0, 0, Color.yellow, pStroke, Color.black );
        GradientPaint border = new GradientPaint( 0, 0, Color.red, 300, 300, Color.blue );
        g.setBorderPaint( new PersistentGradientPaint( border ) );


        // todo: mouse doesn't work
//        addGraphicToPanel( pg1, panel );
//        addGraphicToPanel( pg2, panel );   // green square: works with exceptions
//        addGraphicToPanel( pg3, panel );    // good
//        addGraphicToPanel( pg4, panel );
//        addGraphicToPanel( pg5, panel );
//        addGraphicToPanel( pg6, panel );      // works with exceptions
//        addGraphicToPanel( cpg, panel );
        addGraphicToPanel( g, panel );      // works
//        panel.addGraphic( pg2 );
//        panel.addGraphic( pg3 );
//        panel.addGraphic( pg4 );
//        panel.addGraphic( pg5 );
//        panel.addGraphic( cpg );
//        panel.addGraphic( g );

    }

    int i = 0;

    public void addGraphicToPanel( final PhetGraphic pg, ApparatusPanel panel ) {
        pg.setCursorHand();
        pg.setBoundsDirty();
        pg.setLocation( 0, 0 );
        Rectangle bounds = pg.getBounds();
        if( bounds == null ) {
            System.out.println( "error" );
        }
        Point center = RectangleUtils.getCenter( pg.getBounds() );
//            pg.setRegistrationPoint( bounds.width / 2, bounds.height / 2 );
        pg.setRegistrationPoint( center.x, center.y );
        pg.setLocation( i++ * 50, 100 );
        pg.addMouseInputListener( new Rotator( pg ) );
//        pg.addMouseInputListener( new MouseInputAdapter() {
//            // implements java.awt.event.MouseMotionListener
//            public void mouseDragged( MouseEvent e ) {
//                if( SwingUtilities.isRightMouseButton( e ) ) {
//                    pg.rotate( Math.PI / 64 );
//                }
//            }
//        } );
        pg.addTranslationListener( new Translator( pg ) );
//        pg.addTranslationListener( new TranslationListener() {
//            public void translationOccurred( TranslationEvent translationEvent ) {
//                if( SwingUtilities.isLeftMouseButton( translationEvent.getMouseEvent() ) ) {
////                        pg.transform( AffineTransform.getTranslateInstance( translationEvent.getDx(), translationEvent.getDy() ) );
//                    pg.setLocation( translationEvent.getX(), translationEvent.getY() );
//                }
//            }
//        } );
        panel.addGraphic( pg );
    }


    //////////////////////////////
    // Inner classes
    //
    public static class Rotator extends MouseInputAdapter {
        private PhetGraphic pg;

        public Rotator() {
        }

        public Rotator( PhetGraphic pg ) {
            this.pg = pg;
        }

        public void mouseDragged( MouseEvent e ) {
            if( SwingUtilities.isRightMouseButton( e ) ) {
                pg.rotate( Math.PI / 64 );
            }
        }

        public PhetGraphic getPg() {
            return pg;
        }

        public void setPg( PhetGraphic pg ) {
            this.pg = pg;
        }
    }


    public static class Translator implements TranslationListener {
        private PhetGraphic pg;

        public Translator() {
        }

        public Translator( PhetGraphic pg ) {
            this.pg = pg;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            if( SwingUtilities.isLeftMouseButton( translationEvent.getMouseEvent() ) ) {
                pg.setLocation( translationEvent.getX(), translationEvent.getY() );
            }
        }

        public PhetGraphic getPg() {
            return pg;
        }

        public void setPg( PhetGraphic pg ) {
            this.pg = pg;
        }
    }


    public static class OutlineTextGraphic extends PhetShapeGraphic {
        private String text;
        private Font font;
        private FontRenderContext fontRenderContext;
        private Color color;
        private Color borderColor;
//        Shape shape;

        public OutlineTextGraphic() {
            this.fontRenderContext = new FontRenderContext( new AffineTransform(), true, false );
        }

        public OutlineTextGraphic( Component component, String text, Font font, int x, int y, Color fillColor, Stroke stroke, Color strokeColor ) {
            super( component );
            this.text = text;
            this.font = font;
            setShape( createTextShape() );
            setColor( fillColor );
            setStroke( stroke );
            setBorderColor( strokeColor );
            this.fontRenderContext = new FontRenderContext( new AffineTransform(), true, false );
            component.addComponentListener( new ComponentAdapter() {
                public void componentShown( ComponentEvent e ) {
                    update();
                }
            } );
            component.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    update();
                }
            } );
            setLocation( x, y );
            update();
        }

        void update() {
            setShape( createTextShape() );
        }

        private Shape createTextShape() {
            FontRenderContext frc = fontRenderContext;
            if( frc != null ) {
                TextLayout textLayout = new TextLayout( text, font, frc );
                return new PersistentGeneralPath( (GeneralPath)textLayout.getOutline( new AffineTransform() ) );
            }
            return new Rectangle();
        }

        public String getText() {
            return text;
        }

        public void setText( String text ) {
            this.text = text;
        }

        public Font getFont() {
            return font;
        }

        public void setFont( Font font ) {
            this.font = font;
        }

        public void setShape( Shape shape ) {
            super.setShape( shape );
        }

        public FontRenderContext getFontRenderContext() {
            return fontRenderContext;
        }

        public void setFontRenderContext( FontRenderContext fontRenderContext ) {
            this.fontRenderContext = fontRenderContext;
        }
    }
}




