/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 9:02:45 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */
                                                                       
public class AbstractArrowSet extends CompositePhetGraphic {
    private ArrayList graphics = new ArrayList();

    public AbstractArrowSet( Component component ) {
        super( component );
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
