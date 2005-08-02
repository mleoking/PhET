/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 10:42:31 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class BarGraphSuite extends PNode {
    private RampPanel rampPanel;
    private RampModel rampModel;

    private BarGraphSet workBarGraphSet;
    private BarGraphSet energyBarGraphSet;

    public BarGraphSuite( RampPanel rampPanel, final RampModel rampModel ) {
        super();
        this.rampPanel = rampPanel;
        this.rampModel = rampModel;
        workBarGraphSet = new WorkBarGraphSet( rampPanel, rampModel );
        energyBarGraphSet = new EnergyBarGraphSet( rampPanel, rampModel );
        addChild( workBarGraphSet );
        addChild( energyBarGraphSet );

        energyBarGraphSet.translate( workBarGraphSet.getFullBounds().getWidth() + 10, 0 );
        setPickable( false );
        setChildrenPickable( false );

//        PBoundsHandle.addStickyBoundsHandlesTo( this ,rampPanel.getCamera() );
    }

    public void setLocation( Point p ) {
        super.setOffset( p );
    }

    public void setLocation( int x, int y ) {
        super.setOffset( x, y );
    }

    private Paint toEnergyPaint( Color color ) {
        int imageSize = 10;
        BufferedImage texture = new BufferedImage( imageSize, imageSize, BufferedImage.TYPE_INT_RGB );
        Graphics2D g = texture.createGraphics();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setColor( color );
        g.fillRect( 0, 0, imageSize, imageSize );
        g.setColor( color.brighter() );
        int ovalRadius = 3;
        int ovalDiameter = ovalRadius * 2;
        int ovalX = imageSize - ovalDiameter;
        g.fillOval( ovalX, ovalX, ovalDiameter, ovalDiameter );
        Paint p = new TexturePaint( texture, new Rectangle2D.Double( 0, 0, 10, 10 ) );
        return p;
    }

    public void setEnergyBarsVisible( boolean selected ) {
        energyBarGraphSet.setVisible( selected );
    }

    public void setWorkBarsVisible( boolean selected ) {
        workBarGraphSet.setVisible( selected );
    }

    public double getMaxDisplayableEnergy() {
        return energyBarGraphSet.getMaxDisplayableEnergy();
    }
}
