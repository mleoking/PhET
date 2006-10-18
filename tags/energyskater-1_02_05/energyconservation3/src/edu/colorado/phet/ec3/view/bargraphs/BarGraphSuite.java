/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view.bargraphs;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.ec3.EC3Canvas;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
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
    private EC3Canvas eC3Canvas;
    private EnergyConservationModel energyConservationModel;

    private BarGraphSet workBarGraphSet;
    private BarGraphSet energyBarGraphSet;
    private ModelViewTransform1D transform1D;

    public BarGraphSuite( EC3Canvas eC3Canvas, final EnergyConservationModel energyConservationModel ) {
        this.eC3Canvas = eC3Canvas;
        this.energyConservationModel = energyConservationModel;

        transform1D = new ModelViewTransform1D( 0, 600, 0, 3 );
        workBarGraphSet = new WorkBarGraphSet( eC3Canvas, energyConservationModel, transform1D );
        energyBarGraphSet = new EnergyBarGraphSet( eC3Canvas, energyConservationModel, transform1D );
        addChild( workBarGraphSet );
        addChild( energyBarGraphSet );

        energyBarGraphSet.translate( workBarGraphSet.getFullBounds().getWidth() + 0, 0 );
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

    public void setEnergyBarsMaximized( boolean selected ) {
        energyBarGraphSet.setMinimized( !selected );
    }

    public void setWorkBarsMaximized( boolean selected ) {
        workBarGraphSet.setMinimized( !selected );
    }

    public boolean getEnergyBarsMaximized() {
        return !energyBarGraphSet.isMinimized();
    }

    public boolean getWorkBarsMaximized() {
        return !workBarGraphSet.isMinimized();
    }

    public double getMaxDisplayableEnergy() {
        return energyBarGraphSet.getMaxDisplayableEnergy();
    }

    public boolean areBothMinimized() {
        return ( !getWorkBarsMaximized() ) && ( !getEnergyBarsMaximized() );
    }

    public void setBarChartHeight( double barChartHeight ) {
        workBarGraphSet.setBarChartHeight( barChartHeight );
        energyBarGraphSet.setBarChartHeight( barChartHeight );
    }
}
