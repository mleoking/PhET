/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.view.BlockGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 9:02:45 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */

public class AbstractArrowSet extends PNode {
    public static final String APPLIED = TheRampStrings.getString( "applied1" );
    public static final String TOTAL = TheRampStrings.getString( "total" );
    public static final String FRICTION = TheRampStrings.getString( "friction1" );
    public static final String WEIGHT = TheRampStrings.getString( "weight" );
    public static final String NORMAL = TheRampStrings.getString( "normal" );
    public static final String WALL = TheRampStrings.getString( "wall" );

    private ArrayList graphics = new ArrayList();

    private int totalForceDY = 45;
    private BlockGraphic blockGraphic;

    public int getDefaultOffsetDY() {
        return totalForceDY;
    }

    public AbstractArrowSet( Component component, BlockGraphic blockGraphic ) {
        super();
        this.blockGraphic = blockGraphic;
    }

    public BlockGraphic getBlockGraphic() {
        return blockGraphic;
    }

    protected void addForceArrowGraphic( ForceArrowGraphic forceArrowGraphic ) {
        addChild( forceArrowGraphic );
        graphics.add( forceArrowGraphic );
    }

    public void updateGraphics() {
        if( getVisible() ) {
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
