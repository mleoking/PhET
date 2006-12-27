/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.test;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Feb 27, 2004
 * Time: 3:32:14 PM
 * Copyright (c) Feb 27, 2004 by Sam Reid
 */
public class TestMVT {
    public static void main( String[] args ) {
        ApparatusPanel panel = new ApparatusPanel();
        Ellipse2D.Double ellipse = new Ellipse2D.Double( 2, 2, 1, 4 );
        Graphic g = new ShapeGraphic( ellipse, Color.blue );

        ModelViewTransform2D transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, 10, 0, 10 ), new Rectangle( 0, 0, 400, 400 ) );
        CompositeInteractiveGraphic graphic = new CompositeInteractiveGraphic();

        panel.addGraphic( graphic );
        panel.addGraphicsSetup( new BasicGraphicsSetup() );

        Graphic interactiveGraphic = new DefaultInteractiveGraphic( g, graphic );
        graphic.addGraphic( interactiveGraphic, 0 );
//        TxGraphic graphic=new TxGraphic();
    }
}
