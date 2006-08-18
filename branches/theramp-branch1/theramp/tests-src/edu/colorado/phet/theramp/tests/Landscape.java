/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.tests;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Feb 27, 2005
 * Time: 4:04:37 PM
 * Copyright (c) Feb 27, 2005 by Sam Reid
 */

public class Landscape extends GraphicLayerSet {
    ElementMaker[] elementMakers = new ElementMaker[]{
        new RectangleMaker(), new EllipseMaker(), new TriangleMaker()
    };
    Random random = new Random();
    private Color[] colors = new Color[]{
        Color.red, Color.blue, Color.green, Color.black, Color.yellow
    };
    private Stroke stroke = new BasicStroke( 1 );

    public Landscape( Component component ) {
        super( component );
        int width = 100;
        for( int i = 0; i < 100; i++ ) {
            addElement( i * width, 0, width, random.nextInt( 200 ) + 200 );
        }
    }

    static interface ElementMaker {

        Shape newShape( int x, int y, int width, int height );

    }

    static class RectangleMaker implements ElementMaker {

        public Shape newShape( int x, int y, int width, int height ) {
            return new Rectangle( x, y, width, height );
        }
    }

    static class TriangleMaker implements ElementMaker {

        public Shape newShape( int x, int y, int width, int height ) {
            DoubleGeneralPath path = new DoubleGeneralPath( x, y );
            path.lineToRelative( width, 0 );
            path.lineToRelative( 0, height );
            path.closePath();
            return path.getGeneralPath();
        }
    }

    static class EllipseMaker implements ElementMaker {

        public Shape newShape( int x, int y, int width, int height ) {
            return new Ellipse2D.Double( x, y, width, height );
        }
    }

    private void addElement( int x, int y, int width, int height ) {
        int index = random.nextInt( elementMakers.length );
        ElementMaker maker = elementMakers[index];

        Shape shape = maker.newShape( x, y, width, height );
        int colorIndex = random.nextInt( colors.length );
        final PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( getComponent(), shape, colors[colorIndex], stroke, Color.black );
        phetShapeGraphic.addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                int colorIndex = random.nextInt( colors.length );
                phetShapeGraphic.setColor( colors[colorIndex] );
            }
        } );
        phetShapeGraphic.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                phetShapeGraphic.translate( translationEvent.getDx(), translationEvent.getDy() );
            }
        } );
        phetShapeGraphic.setCursorHand();
        addGraphic( phetShapeGraphic );

    }
}
