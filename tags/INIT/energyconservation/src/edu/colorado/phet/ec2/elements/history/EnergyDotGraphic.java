/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.history;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.graphics.IdeaGraphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.ec2.EC2Module;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 14, 2003
 * Time: 10:40:43 AM
 * Copyright (c) Jul 14, 2003 by Sam Reid
 */
public class EnergyDotGraphic implements InteractiveGraphic {
    CenteredCircleGraphic3 circle = new CenteredCircleGraphic3( 3, Color.yellow );
    private EC2Module module;
    EnergyDot dot;
    private int y;
    private int x;
    IdeaGraphic display;
    Graphic circleGraphic;

    public EnergyDotGraphic( EC2Module module, EnergyDot dot ) {
        this.module = module;
        this.dot = dot;
        Point viewPt = module.getTransform().modelToView( dot.getX(), dot.getY() );
        this.x = viewPt.x;
        this.y = viewPt.y;
        module.getTransform().addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                viewChanged( modelViewTransform2d );
            }
        } );
        DecimalFormat df = new DecimalFormat( "#0.0#" );

        Graphics2D gr = (Graphics2D)module.getApparatusPanel().getGraphics();

        if( EC2Module.WENDY_MODE ) {
            display = new IdeaGraphic( false, 0, 0, new String[]{"Height=" + df.format( dot.getHeight() ) + " meters", "Time=" + df.format( dot.getTime() ) + " seconds",
//                                                                "KE=" + df.format(dot.getKineticEnergy()) + " Joules", "PE=" + df.format(dot.getPotentialEnergy()) + " Joules",
                                                                 "Distance from Left Wall=" + df.format( dot.getX() ) + " meters", "Speed=" + df.format( dot.getSpeed() ) + " m/s"},
                                       gr.getFontRenderContext(), new Font( "dialog", 0, 18 ),
                                       Color.black, module.getIdeaImage() );
        }
        else {
            display = new IdeaGraphic( false, 0, 0, new String[]{"Height=" + df.format( dot.getHeight() ) + " meters", "Time=" + df.format( dot.getTime() ) + " seconds",
                                                                 "KE=" + df.format( dot.getKineticEnergy() ) + " Joules", "PE=" + df.format( dot.getPotentialEnergy() ) + " Joules",
                                                                 "Distance from Left Wall=" + df.format( dot.getX() ) + " meters", "Speed=" + df.format( dot.getSpeed() ) + " m/s"},
                                       gr.getFontRenderContext(), new Font( "dialog", 0, 18 ),
                                       Color.black, module.getIdeaImage() );
        }
        circleGraphic = circle.createGraphic( this.x, this.y );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return circle.containsPoint( event.getX(), event.getY(), x, y );
    }

    public void mousePressed( MouseEvent event ) {
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        display.setX( x + 20 );
        display.setY( y - 50 );
        display.setVisible( true );
        module.getApparatusPanel().addGraphic( display, 100 );
    }

    public void mouseExited( MouseEvent event ) {
        display.setVisible( false );
        module.getApparatusPanel().removeGraphic( display );
    }

    public void paint( Graphics2D g ) {
        circleGraphic.paint( g );
    }

    public IdeaGraphic getDisplay() {
        return display;
    }

    public void viewChanged( ModelViewTransform2d transform ) {
        Point viewPt = module.getTransform().modelToView( dot.getX(), dot.getY() );
        this.x = viewPt.x;
        this.y = viewPt.y;
        circleGraphic = circle.createGraphic( this.x, this.y );
    }

    public EnergyDot getEnergyDot() {
        return dot;
    }
}
