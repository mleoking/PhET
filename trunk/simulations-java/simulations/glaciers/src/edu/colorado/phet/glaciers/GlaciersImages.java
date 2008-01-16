/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers;

import java.awt.image.BufferedImage;

/**
 * GlaciersImages is a collection of images used by this simulation.
 * All images are loaded statically so that we can easily test for missing images on start up.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersImages {

    /* not intended for instantiation */
    private GlaciersImages() {}
    
    public static final BufferedImage BOREHOLE_DRILL = GlaciersResources.getImage( "boreholeDrill.png" );
    public static final BufferedImage GLACIAL_BUDGET_METER = GlaciersResources.getImage( "glacialBudgetMeter.png" );
    public static final BufferedImage ICE_THICKNESS_TOOL = GlaciersResources.getImage( "iceThicknessTool.png" );
    public static final BufferedImage THERMOMETER = GlaciersResources.getImage( "thermometer.png" );
    public static final BufferedImage TOOLBOX_BOREHOLE_DRILL = GlaciersResources.getImage( "toolbox-boreholeDrill.png" );
    public static final BufferedImage TOOLBOX_GLACIAL_BUDGET_METER = GlaciersResources.getImage( "toolbox-glacialBudgetMeter.png" );
    public static final BufferedImage TOOLBOX_GPS_RECEIVER = GlaciersResources.getImage( "toolbox-gpsReceiver.png" );
    public static final BufferedImage TOOLBOX_ICE_THICKNESS_TOOL = GlaciersResources.getImage( "toolbox-iceThicknessTool.png" );
    public static final BufferedImage TOOLBOX_THERMOMETER = GlaciersResources.getImage( "toolbox-thermometer.png" );
    public static final BufferedImage TOOLBOX_TRACER_FLAG = GlaciersResources.getImage( "toolbox-tracerFlag.png" );
    public static final BufferedImage TOOLBOX_TRASH_CAN = GlaciersResources.getImage( "toolbox-trashCan.png" );
    public static final BufferedImage TRACER_FLAG = GlaciersResources.getImage( "tracerFlag.png" );
    public static final BufferedImage PENGUIN = GlaciersResources.getImage( "penguin.png" );
    public static final BufferedImage VALLEY = GlaciersResources.getImage( "valley.png" );
}
