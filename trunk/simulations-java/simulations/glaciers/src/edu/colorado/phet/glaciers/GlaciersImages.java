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
    public static final BufferedImage BOREHOLE_DRILL_ON_BUTTON = GlaciersResources.getImage( "boreholeDrillOnButton.png" );
    public static final BufferedImage BOREHOLE_DRILL_OFF_BUTTON = GlaciersResources.getImage( "boreholeDrillOffButton.png" );
    public static final BufferedImage GLACIAL_BUDGET_METER = GlaciersResources.getImage( "glacialBudgetMeter.png" );
    public static final BufferedImage GLACIER_PICTURE = GlaciersResources.getImage( "Tonsina_Glacier.jpg" );
    public static final BufferedImage GLACIER_THUMBNAIL = GlaciersResources.getImage( "Tonsina_Glacier_thumbnail.png" );
    public static final BufferedImage GPS_RECEIVER = GlaciersResources.getImage( "gpsReceiver.png" );
    public static final BufferedImage SNOWFLAKE = GlaciersResources.getImage( "snowflake.png" );
    public static final BufferedImage TRACER_FLAG = GlaciersResources.getImage( "tracerFlag.png" );
    public static final BufferedImage TRASH_CAN = GlaciersResources.getImage( "trashCan.png" );
    public static final BufferedImage PAN_CHARACTER = GlaciersResources.getImage( "bear.png" );
    public static final BufferedImage MOUNTAINS = GlaciersResources.getImage( "mountains.png" );
}
