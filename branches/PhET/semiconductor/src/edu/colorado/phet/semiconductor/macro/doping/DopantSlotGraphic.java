package edu.colorado.phet.semiconductor.macro.doping;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.macro.SemiconductorModule;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 8, 2004
 * Time: 4:21:48 PM
 * Copyright (c) Feb 8, 2004 by Sam Reid
 */
public class DopantSlotGraphic implements Graphic {
    private TexturePaint green;
    private TexturePaint red;
    private Shape shape;
    private DopantType type;
//    private String name;
    private TexturePaint texture;
    static final Font font = new Font( "Lucida Sans", Font.BOLD, 18 );

    public DopantSlotGraphic( Shape shape, DopantType type ) throws IOException {
        this.shape = shape;
        this.type = type;
//        this.name = name;
        green = new TexturePaint( SemiconductorModule.imageLoader.loadImage( "images/particle-green-med.gif" ), new Rectangle2D.Double( 0, 0, 15, 15 ) );
        red = new TexturePaint( SemiconductorModule.imageLoader.loadImage( "images/particle-red-med.gif" ), new Rectangle2D.Double( 0, 0, 15, 15 ) );
    }

    public void setDopantType( DopantType type ) {
        this.type = type;
        this.texture = getTexture( type );
    }

    public void paint( Graphics2D graphics2D ) {
        if( texture == null ) {
            return;
        }
        graphics2D.setPaint( texture );
        graphics2D.fill( shape );
        graphics2D.setFont( font );
//        graphics2D.drawString(name,shape.getViewport().x,shape.getViewport().y);
    }

    private TexturePaint getTexture( DopantType type ) {
        if( type == DopantType.N ) {
            return green;
        }
        else if( type == DopantType.P ) {
            return red;
        }
        return null;
    }

    public void setShape( Shape viewShape ) {
        this.shape = viewShape;
    }

}
