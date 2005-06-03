/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.theramp.common.BarGraphic2D;
import edu.colorado.phet.theramp.common.CurlyLabel;
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

public class BarGraphSet extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private RampModel rampModel;

    private int dx = 10;
    private int dy = -10;

    public BarGraphSet( RampPanel rampPanel, final RampModel rampModel ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.rampModel = rampModel;
        int y = 600;
        int width = 23;
        int dw = 10;
        int sep = width + dw;

        ModelViewTransform1D transform1D = new ModelViewTransform1D( 0, 150, 0, 5 );

        ValueAccessor[] energyAccess = new ValueAccessor[]{
            new ValueAccessor.KineticEnergy( getLookAndFeel() ), new ValueAccessor.PotentialEnergy( getLookAndFeel() ),
            new ValueAccessor.ThermalEnergy( getLookAndFeel() ), new ValueAccessor.TotalEnergy( getLookAndFeel() )

        };
        ValueAccessor[] workAccess = new ValueAccessor[]{
            new ValueAccessor.AppliedWork( getLookAndFeel() ), new ValueAccessor.FrictiveWork( getLookAndFeel() ),
            new ValueAccessor.GravityWork( getLookAndFeel() ), new ValueAccessor.TotalWork( getLookAndFeel() )
        };

        for( int i = 0; i < energyAccess.length; i++ ) {
            final ValueAccessor accessor = energyAccess[i];
            final BarGraphic2D barGraphic = new BarGraphic2D( getComponent(), accessor.getName(), transform1D,
                                                              accessor.getValue( rampModel ), sep * i + dw, width, y, dx, dy, toEnergyPaint( accessor.getColor() ) );
            addClockTickListener( new ClockTickListener() {
                public void clockTicked( ClockTickEvent event ) {
                    barGraphic.setValue( accessor.getValue( rampModel ) );
                }
            } );
            addGraphic( barGraphic );
        }

        CurlyLabel curlyLabel = new CurlyLabel( getComponent(), "<html>Energy<font SIZE=-1><br>of the block</html>" );
        curlyLabel.setLocation( 0, y );
        addGraphic( curlyLabel, 1 );

        int energyWidth = ( energyAccess.length ) * sep;
        curlyLabel.rescaleToWidth( energyWidth );

        int workX = energyWidth + dw + sep;
        for( int i = 0; i < workAccess.length; i++ ) {
            final ValueAccessor accessor = workAccess[i];
            final BarGraphic2D barGraphic = new BarGraphic2D( getComponent(), accessor.getName(), transform1D,
                                                              accessor.getValue( rampModel ), ( energyAccess.length + 1 + i ) * sep + dw, width, y, dx, dy, accessor.getColor() );
            addClockTickListener( new ClockTickListener() {
                public void clockTicked( ClockTickEvent event ) {
                    barGraphic.setValue( accessor.getValue( rampModel ) );
                }
            } );
            addGraphic( barGraphic );
        }

        int workWidth = ( workAccess.length ) * sep;

        CurlyLabel workLabel = new CurlyLabel( getComponent(), "<html>Work<font SIZE=-1><br>done on the block</html>" );
        workLabel.setLocation( workX, y );
        workLabel.rescaleToWidth( workWidth );
        addGraphic( workLabel, 1 );

        PhetShapeGraphic energyBackground = new PhetShapeGraphic( getComponent(), new Rectangle( -5, 0, 5 * 2 + energyWidth, 10000 ), Color.white, new BasicStroke(), Color.black );
        addGraphic( energyBackground, -10 );

        PhetShapeGraphic workBackground = new PhetShapeGraphic( getComponent(), new Rectangle( workX - 5, 0, 5 * 2 + workWidth, 10000 ), new Color( 240, 250, 245 ), new BasicStroke(), Color.black );
        addGraphic( workBackground, -10 );
        setIgnoreMouse( true );
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
}
