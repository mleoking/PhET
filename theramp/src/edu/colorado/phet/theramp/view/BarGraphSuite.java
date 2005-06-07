/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.theramp.common.BarGraphic2D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.ValueAccessor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 10:42:31 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class BarGraphSuite extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private RampModel rampModel;

    private int dx = 10;
    private int dy = -10;

    public BarGraphSuite( RampPanel rampPanel, final RampModel rampModel ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.rampModel = rampModel;
//        int topY = (int)( rampPanel.getRampBaseY()*0.82 )+30;
        int topY = (int)( rampPanel.getRampBaseY() * 0.82 ) + 120;//complicated function of my scale, and rampworld scale.
        int y = 700;
//        int height = y - topY;
        int width = 23;
        int dw = 10;
        int sep = width + dw;

        ModelViewTransform1D transform1D = new ModelViewTransform1D( 0, 300, 0, 3 );

        ValueAccessor[] energyAccess = new ValueAccessor[]{
            new ValueAccessor.KineticEnergy( getLookAndFeel() ), new ValueAccessor.PotentialEnergy( getLookAndFeel() ),
            new ValueAccessor.ThermalEnergy( getLookAndFeel() ), new ValueAccessor.TotalEnergy( getLookAndFeel() )
        };
        ValueAccessor[] workAccess = new ValueAccessor[]{
            new ValueAccessor.TotalWork( getLookAndFeel() ),
            new ValueAccessor.GravityWork( getLookAndFeel() ),
            new ValueAccessor.FrictiveWork( getLookAndFeel() ),
            new ValueAccessor.AppliedWork( getLookAndFeel() )
        };

        int energyWidth = ( energyAccess.length ) * sep;
        int workWidth = ( workAccess.length ) * sep;

        for( int i = 0; i < workAccess.length; i++ ) {
            final ValueAccessor accessor = workAccess[i];
            final BarGraphic2D barGraphic = new BarGraphic2D( getComponent(), accessor.getName(), transform1D,
                                                              accessor.getValue( rampModel ), ( i ) * sep + dw, width, y, dx, dy, accessor.getColor() );
            addClockTickListener( new ClockTickListener() {
                public void clockTicked( ClockTickEvent event ) {
                    barGraphic.setValue( accessor.getValue( rampModel ) );
                }
            } );
            addGraphic( barGraphic );
        }

        int energyX = workWidth + dw + sep;
        int workX = 0;
        for( int i = 0; i < energyAccess.length; i++ ) {
            final ValueAccessor accessor = energyAccess[i];
            final BarGraphic2D barGraphic = new BarGraphic2D( getComponent(), accessor.getName(), transform1D,
//                                                              accessor.getValue( rampModel ), sep * ( i + energyAccess.length + 1 ) + dw, width, y, dx, dy, toEnergyPaint( accessor.getColor() ) );
                                                              accessor.getValue( rampModel ), sep * ( i + energyAccess.length + 1 ) + dw, width, y, dx, dy, accessor.getColor() );
            addClockTickListener( new ClockTickListener() {
                public void clockTicked( ClockTickEvent event ) {
                    barGraphic.setValue( accessor.getValue( rampModel ) );
                }
            } );
            addGraphic( barGraphic );
        }

//        CurlyLabel energyLabel = new CurlyLabel( getComponent(), "<html>Energy</html>" );
//        energyLabel.setLocation( energyX, y );
//        addGraphic( energyLabel, 1 );
//        energyLabel.rescaleToWidth( energyWidth );
//
//        CurlyLabel workLabel = new CurlyLabel( getComponent(), "<html>Work</html>" );
//        workLabel.setLocation( workX, y );
//        workLabel.rescaleToWidth( workWidth );
//        addGraphic( workLabel, 1 );

        PhetShapeGraphic energyBackground = new PhetShapeGraphic( getComponent(), new Rectangle( energyX - 5, topY, 5 * 2 + energyWidth, 1000 ), Color.white, new BasicStroke(), Color.black );
        addGraphic( energyBackground, -10 );

        PhetShapeGraphic workBackground = new PhetShapeGraphic( getComponent(), new Rectangle( workX - 5, topY, 5 * 2 + workWidth, 1000 ), new Color( 240, 250, 245 ), new BasicStroke(), Color.black );
        addGraphic( workBackground, -10 );
        setIgnoreMouse( true );
    }

    public void setLocation( Point p ) {
        super.setLocation( p );
    }

    public void setLocation( int x, int y ) {
        super.setLocation( x, y );
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

    private RampLookAndFeel getLookAndFeel() {
        return rampPanel.getLookAndFeel();
    }

    private void addClockTickListener( ClockTickListener clockTickListener ) {
        rampPanel.getRampModule().getClock().addClockTickListener( clockTickListener );
    }

    public void setEnergyBarsVisible( boolean selected ) {
        en
    }
}
