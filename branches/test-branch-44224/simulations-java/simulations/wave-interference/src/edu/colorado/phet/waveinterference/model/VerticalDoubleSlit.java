/*  */
package edu.colorado.phet.waveinterference.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:49:48 AM
 */

public class VerticalDoubleSlit extends VerticalBarrier {
    private Rectangle topSlit;
    private Rectangle bottomSlit;

    private Rectangle topBar;
    private Rectangle midBar;
    private Rectangle bottomBar;

    public VerticalDoubleSlit( int gridWidth, int gridHeight, int x, int thickness, int slitSize, int slitSeparation, double potential ) {
        super( gridWidth, gridHeight, x, thickness, slitSize, slitSeparation, potential );
    }

    protected void update() {
        int gridH = getGridHeight();
        int sep = getSlitSeparation();
        int slitSize = getSlitSize();

        int midBarHeight = sep - slitSize;
        if ( midBarHeight <= 0 ) {     //make sure there is always a barrier between the slits, see #884
            midBarHeight = 1;
        }

        int topBarHeight = gridH/2-midBarHeight/2-slitSize;
        int bottomBarHeight = gridH - topBarHeight - slitSize - midBarHeight - slitSize;//remaining

        int thickness = getThickness();
        int x = getX();
        double potential = getPotential();

        topBar = new Rectangle( x, 0, thickness, topBarHeight );
        midBar = new Rectangle( x, topBarHeight + slitSize, thickness, midBarHeight );
        bottomBar = new Rectangle( x, midBar.y + midBar.height + slitSize, thickness, bottomBarHeight );

        this.topSlit = new Rectangle( x, topBar.y + topBar.height, thickness, slitSize );//todo: fix slitSize
        this.bottomSlit = new Rectangle( x, midBar.y + midBar.height, thickness, slitSize );

        CompositePotential compositePotential = new CompositePotential();
        if ( super.getInverse() ) {
            compositePotential.addPotential( new BarrierPotential( topSlit, potential ) );
            compositePotential.addPotential( new BarrierPotential( bottomSlit, potential ) );
        }
        else {
            compositePotential.addPotential( new BarrierPotential( topBar, potential ) );
            compositePotential.addPotential( new BarrierPotential( midBar, potential ) );
            compositePotential.addPotential( new BarrierPotential( bottomBar, potential ) );
        }
        setPotentialDelegate( new PrecomputedPotential( compositePotential, getGridWidth(), getGridHeight() ) );
        notifyListeners();

    }

    public Rectangle[] getSlitAreas() {
        return new Rectangle[]{new Rectangle( topSlit ), new Rectangle( bottomSlit )};
    }

    public Rectangle[] getRectangleBarriers() {
        ArrayList r = new ArrayList();
        if ( getInverse() ) {
            r.add( topSlit );
            r.add( bottomSlit );
        }
        else {
            r.add( topBar );
            r.add( midBar );
            r.add( bottomBar );
        }
        return (Rectangle[]) r.toArray( new Rectangle[0] );
    }
}
