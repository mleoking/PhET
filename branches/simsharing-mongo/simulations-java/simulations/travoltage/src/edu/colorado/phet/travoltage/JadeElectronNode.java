// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 1:07:31 AM
 */

public class JadeElectronNode extends PNode {
    private TravoltageBodyNode travoltageBodyNode;
    private JadeElectron electron;
    private LocationMap locationMap = new DefaultLocationMap( getRadius() );

    public static double getViewRadius() {
        try {
            return ImageLoader.loadBufferedImage( "travoltage/images/Electron3.GIF" ).getWidth() / 2;
        }
        catch( IOException e ) {
            e.printStackTrace();
            return Double.NaN;
        }
    }

    public JadeElectronNode( TravoltageBodyNode travoltageBodyNode, JadeElectron electron ) {
        this.travoltageBodyNode = travoltageBodyNode;
        this.electron = electron;
        addChild( PImageFactory.create( "travoltage/images/Electron3.GIF" ) );
        setPickable( false );
        setChildrenPickable( false );
        update();
        electron.addListener( new JadeElectron.Listener() {
            public void electronMoved() {
                update();
            }
        } );
    }

    public void update() {
        Point2D loc = locationMap.getLocation( electron.getPosition() );
        setOffset( loc.getX() + travoltageBodyNode.getOffset().getX(), loc.getY() + travoltageBodyNode.getOffset().getY() );
    }

    public double getRadius() {
        return getFullBounds().getWidth() / 2.0;
    }

    public JadeElectron getJadeElectron() {
        return electron;
    }

    public void setLocationMap( LocationMap locationMap ) {
        this.locationMap = locationMap;
        update();
    }
}
