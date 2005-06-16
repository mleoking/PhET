/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.theramp.common.BarGraphic2D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:16:45 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class BarGraphSet extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private RampModel rampModel;
    public ModelViewTransform1D transform1D;
    public int y;
    public int width;
    public int dw;
    public int sep;
    private int dx = 10;
    private int dy = -10;
    public int topY;

    public BarGraphSet( RampPanel rampPanel, RampModel rampModel, String title ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.rampModel = rampModel;
        transform1D = new ModelViewTransform1D( 0, 300, 0, 3 );
        topY = (int)( rampPanel.getRampBaseY() * 0.82 ) + 120;
        y = 750;
        width = 23;
        dw = 10;
        sep = width + dw;
        PhetShadowTextGraphic phetShadowTextGraphic = new PhetShadowTextGraphic( rampPanel, new Font( "Lucida Sans", Font.BOLD, 22 ), title, Color.black, 1, 1, Color.gray );
        phetShadowTextGraphic.translate( 5, topY + 10 );
        addGraphic( phetShadowTextGraphic, 100 );
    }

    protected RampLookAndFeel getLookAndFeel() {
        return rampPanel.getLookAndFeel();
    }

    public double getMaxDisplayableEnergy() {
        return Math.abs( transform1D.viewToModelDifferential( y - topY ) );
    }

    protected void addClockTickListener( ClockTickListener clockTickListener ) {
        rampPanel.getRampModule().getClock().addClockTickListener( clockTickListener );
    }

    public void setAccessors( ValueAccessor[] workAccess ) {
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
        PhetShapeGraphic energyBackground = new PhetShapeGraphic( getComponent(), new Rectangle( 0, topY, 5 * 2 + getWidth(), 1000 ), Color.white, new BasicStroke(), Color.black );
        addGraphic( energyBackground, -10 );
    }
}
