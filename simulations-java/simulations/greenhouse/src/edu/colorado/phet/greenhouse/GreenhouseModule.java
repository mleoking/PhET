/**
 * Class: GreenhouseModule
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.greenhouse.phetcommon.application.PhetApplication;
import edu.colorado.phet.greenhouse.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.greenhouse.phetcommon.view.graphics.Graphic;

public class GreenhouseModule extends BaseGreenhouseModule {


    HashMap cloudsToGraphicMap = new HashMap();
    boolean cloudsEnabled = false;


    public GreenhouseModule() {
        super( SimStrings.get( "ModuleTitle.GreenHouseModule" ) );

        // Add some clouds
        createCloud( getEarth().getLocation().getX() + 1,
                     getEarth().getLocation().getY() + Earth.radius + 7.5,
                     3, .3 );

        createCloud( getEarth().getLocation().getX() - 5,
                     getEarth().getLocation().getY() + Earth.radius + 5,
                     5, .5 );

        createCloud( getEarth().getLocation().getX() + 5.5,
                     getEarth().getLocation().getY() + Earth.radius + 5.8,
                     6, .4 );

        // Set up the controls
        JPanel pnl = new JPanel();
        GreenhouseControlPanel greenhouseControlPanel = new GreenhouseControlPanel( this );
        pnl.add( greenhouseControlPanel );
        setControlPanel( pnl );

        // Tell the Earth not to jimmy the temperature
        getEarth().setJimmyArray( null );
    }

    public void activate( PhetApplication phetApplication ) {
    }

    public void deactivate( PhetApplication phetApplication ) {
    }


    public void reset() {
        super.reset();
        setToday();
        numCloudsEnabled( 0 );
        cloudsEnabled( false );
    }

    //
    // Methods for handling clouds
    //
    public boolean isCloudsEnabled() {
        return cloudsEnabled;
    }

    private void createCloud( double x, double y, double width, double height ) {
        Ellipse2D.Double bounds = new Ellipse2D.Double();
        bounds.setFrameFromCenter( x, y, x + width / 2, y + height / 2 );
        Cloud cloud = new Cloud( bounds );
        CloudGraphic cloudGraphic = new CloudGraphic( cloud );
        cloudsToGraphicMap.put( cloud, cloudGraphic );
    }

    public void cloudsEnabled( boolean enabled ) {
        Collection clouds = cloudsToGraphicMap.keySet();
        for ( Iterator iterator = clouds.iterator(); iterator.hasNext(); ) {
            Cloud cloud = (Cloud) iterator.next();
            if ( !cloudsEnabled && enabled ) {
                getGreenhouseModel().addCloud( cloud );
                getApparatusPanel().addGraphic( (Graphic) cloudsToGraphicMap.get( cloud ), ApparatusPanel.LAYER_DEFAULT );
            }
            else if ( cloudsEnabled && !enabled ) {
                getGreenhouseModel().removeCloud( cloud );
                getApparatusPanel().removeGraphic( (Graphic) cloudsToGraphicMap.get( cloud ) );
            }
        }
        cloudsEnabled = enabled;
    }

    public void numCloudsEnabled( int numClouds ) {
        Collection clouds = cloudsToGraphicMap.keySet();
        int n = 0;
        for ( Iterator iterator = clouds.iterator(); iterator.hasNext(); ) {
            Cloud cloud = (Cloud) iterator.next();
            getGreenhouseModel().removeCloud( cloud );
            getApparatusPanel().removeGraphic( (Graphic) cloudsToGraphicMap.get( cloud ) );
        }
        for ( Iterator iterator = clouds.iterator(); iterator.hasNext(); ) {
            Cloud cloud = (Cloud) iterator.next();
            n++;
            if ( n <= numClouds ) {
                getGreenhouseModel().addCloud( cloud );
                getApparatusPanel().addGraphic( (Graphic) cloudsToGraphicMap.get( cloud ), ApparatusPanel.LAYER_DEFAULT );
            }
        }
    }
}
