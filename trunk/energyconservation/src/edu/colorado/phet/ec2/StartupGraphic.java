/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.arrows.Arrow;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.ec2.common.IdeaGraphic2;
import edu.colorado.phet.ec2.elements.car.CarGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Aug 1, 2003
 * Time: 1:41:41 AM
 * Copyright (c) Aug 1, 2003 by Sam Reid
 */
public class StartupGraphic implements Graphic, TransformListener {
    IdeaGraphic2 carIdea;
    int carIdeax = 200;
    int carIdeay = 200;
    Arrow carArrow;
//    ArrowWithFixedSizeArrowhead carArrow;
//    ArrowWithFixedSizeArrowhead carArrow2;
    Arrow carArrow2;
    Font font = new Font( "Lucida Sans", Font.BOLD, 38 );
    private boolean init = false;
    private EC2Module module;

    IdeaGraphic2 splineIdea;
    private BufferedImage image;
    private int splineIdeax = 200;
    private int splineIdeay = 200;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    private boolean visible = true;


    public StartupGraphic( EC2Module module ) {

        this.module = module;
    }

    public void paint( Graphics2D graphics2D ) {
        if( !visible ) {
            return;
        }
        if( !init ) {
            image = new ImageLoader().loadBufferedImage( "images/icons/About24.gif" );
            carIdea = new IdeaGraphic2( true, carIdeax, carIdeay, new String[]{"Drag to add energy!", "(Right-drag to set velocity.)"}, graphics2D.getFontRenderContext(), font, Color.black, image, Color.yellow, module.getApparatusPanel() );
            carArrow = new Arrow( Color.yellow, 12 );
            carArrow2 = new Arrow( Color.black, 14 );

            splineIdea = new IdeaGraphic2( true, splineIdeax, splineIdeay, new String[]{"Drag to create a track."}, graphics2D.getFontRenderContext(), font, Color.black, image, Color.yellow, module.getApparatusPanel() );
            init = true;
        }

        CarGraphic cg = module.getCarGraphic();
        carArrow2.drawLine( graphics2D, carIdeax, carIdeay, cg.getX(), cg.getY() );
        carArrow.drawLine( graphics2D, carIdeax, carIdeay, cg.getX(), cg.getY() );
        carIdea.paint( graphics2D );

//        VertexGraphic vg = module.getSplineInitGraphic().vertexGraphicAt(0);
//        carArrow2.drawLine(graphics2D, splineIdeax, splineIdeay, vg.getX() + 40, vg.getY() + 20);
//        carArrow.drawLine(graphics2D, splineIdeax, splineIdeay, vg.getX() + 40, vg.getY() + 20);
//        splineIdea.paint(graphics2D);
    }

    public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
        carIdeay = (int)( modelViewTransform2d.getViewBounds().getHeight() - 600 );
        carIdeax = 200;

    }
}
