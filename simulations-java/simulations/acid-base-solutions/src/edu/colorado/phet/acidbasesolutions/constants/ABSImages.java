/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Images and Icons used throughout the simulation.
 * Loaded statically to make it easy to debug missing image files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSImages {

    /* not intended for instantiation */
    private ABSImages() {}
    
    public static final BufferedImage ARROW_DOUBLE = getBufferedImage( "arrow_double.png" );
    public static final BufferedImage ARROW_SINGLE = getBufferedImage( "arrow_single.png" );
    public static final BufferedImage BATTERY= getBufferedImage( "battery.png" );
    public static final BufferedImage LIGHT_BULB_BASE = getBufferedImage( "lightBulbBase.png" );
    public static final BufferedImage LIGHT_BULB_GLASS = getBufferedImage( "lightBulbGlass.png" );
    public static final BufferedImage LIGHT_BULB_GLASS_MASK = getBufferedImage( "lightBulbGlassMask.png" );
    
    // icons used in control panels
    public static final Icon BEAKER_ICON = getIcon( "icons/beaker_icon.png" );
    public static final Icon CONCENTRATION_GRAPH_ICON = getIcon( "icons/concentrationGraph_icon.png" );
    public static final Icon LIGHT_BULB_ICON = getIcon( "icons/lightBulb_icon.png" );
    public static final Icon MAGNIFYING_GLASS_ICON = getIcon( "icons/magnifyingGlass_icon.png" );
    public static final Icon PH_METER_ICON = getIcon( "icons/pHMeter_icon.png" );
    public static final Icon PH_PAPER_ICON = getIcon( "icons/pHPaper_icon.png" );
    
    // molecules
    public static final BufferedImage A_MINUS_MOLECULE = getBufferedImage( "molecules/A_minus.png" );
    public static final BufferedImage B_MOLECULE = getBufferedImage( "molecules/B.png" );
    public static final BufferedImage BH_PLUS_MOLECULE = getBufferedImage( "molecules/BH_plus.png" );
    public static final BufferedImage H2O_MOLECULE = getBufferedImage( "molecules/H2O.png" );
    public static final BufferedImage H3O_PLUS_MOLECULE = getBufferedImage( "molecules/H3O_plus.png" );
    public static final BufferedImage HA_MOLECULE = getBufferedImage( "molecules/HA.png" );
    public static final BufferedImage M_PLUS_MOLECULE = getBufferedImage( "molecules/M_plus.png" );
    public static final BufferedImage MOH_MOLECULE = getBufferedImage( "molecules/MOH.png" );
    public static final BufferedImage OH_MINUS_MOLECULE = getBufferedImage( "molecules/OH_minus.png" );
    
    // molecule icons
    private static final double MOLECULE_ICON_SCALE = 0.75;
    public static final Icon A_MINUS_ICON = getIcon( "molecules/A_minus.png", MOLECULE_ICON_SCALE );
    public static final Icon B_ICON = getIcon( "molecules/B.png", MOLECULE_ICON_SCALE );
    public static final Icon BH_PLUS_ICON = getIcon( "molecules/BH_plus.png", MOLECULE_ICON_SCALE );
    public static final Icon H2O_ICON = getIcon( "molecules/H2O.png", MOLECULE_ICON_SCALE );
    public static final Icon H3O_PLUS_ICON = getIcon( "molecules/H3O_plus.png", MOLECULE_ICON_SCALE );
    public static final Icon HA_ICON = getIcon( "molecules/HA.png", MOLECULE_ICON_SCALE );
    public static final Icon M_PLUS_ICON = getIcon( "molecules/M_plus.png", MOLECULE_ICON_SCALE );
    public static final Icon MOH_ICON = getIcon( "molecules/MOH.png", MOLECULE_ICON_SCALE );
    public static final Icon OH_MINUS_ICON = getIcon( "molecules/OH_minus.png", MOLECULE_ICON_SCALE );
    
    private static final BufferedImage getBufferedImage( String resourceName ) {
        return ABSResources.getBufferedImage( resourceName );
    }
    
    private static final Icon getIcon( String resourceName ) {
        return getIcon( resourceName, 1 );
    }
    
    private static final Icon getIcon( String resourceName, double scale ) {
        BufferedImage image = ABSResources.getBufferedImage( resourceName );
        PImage imageNode = new PImage( image );
        imageNode.scale( scale ); // use Piccolo to scale images
        return new ImageIcon( imageNode.toImage() );
    }
}
