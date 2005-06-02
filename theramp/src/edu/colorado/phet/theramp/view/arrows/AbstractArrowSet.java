/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.theramp.view.BlockGraphic;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 9:02:45 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */

public class AbstractArrowSet extends CompositePhetGraphic {
    public static final String APPLIED = "Applied";
    public static final String TOTAL = "Total";
    public static final String FRICTION = "Friction";
    public static final String WEIGHT = "Weight";
    public static final String NORMAL = "Normal";
    public static final String WALL = "Wall";

    private ArrayList graphics = new ArrayList();

    private int totalForceDY = 45;
    private BlockGraphic blockGraphic;

    public int getDefaultOffsetDY() {
        return totalForceDY;
    }

    public AbstractArrowSet( Component component, BlockGraphic blockGraphic ) {
        super( component );
        this.blockGraphic = blockGraphic;
    }

    public BlockGraphic getBlockGraphic() {
        return blockGraphic;
    }

    protected void addForceArrowGraphic( ForceArrowGraphic forceArrowGraphic ) {
        addGraphic( forceArrowGraphic );
        graphics.add( forceArrowGraphic );
    }

    public void updateGraphics() {
        if( isVisible() ) {
            for( int i = 0; i < graphics.size(); i++ ) {
                ForceArrowGraphic forceArrowGraphic = (ForceArrowGraphic)graphics.get( i );
                forceArrowGraphic.update();
            }
        }
    }

    public void setForceVisible( String force, boolean selected ) {
        for( int i = 0; i < graphics.size(); i++ ) {
            ForceArrowGraphic forceArrowGraphic = (ForceArrowGraphic)graphics.get( i );
            String name = forceArrowGraphic.getName();
            if( name.toLowerCase().indexOf( force.toLowerCase() ) >= 0 ) {
//                forceArrowGraphic.setVisible( selected );
                forceArrowGraphic.setUserVisible( selected );
            }
        }
    }

    public static interface ForceComponent {
        Vector2D getForce();
    }

}
