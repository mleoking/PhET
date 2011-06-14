// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.acidbasesolutions.view.molecules.*;
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

    // icons used in control panels
    public static final Icon BEAKER_ICON = getIcon( "icons/beaker_icon.png" );
    public static final Icon LIGHT_BULB_ICON = getIcon( "icons/lightBulb_icon.png" );
    public static final Icon MAGNIFYING_GLASS_ICON = getIcon( "icons/magnifyingGlass_icon.png" );
    public static final Icon PH_METER_ICON = getIcon( "icons/pHMeter_icon.png" );
    public static final Icon PH_PAPER_ICON = getIcon( "icons/pHPaper_icon.png" );

    // generate molecule images using Piccolo
    public static final Image A_MINUS_MOLECULE = new AMinusNode().toImage();
    public static final Image B_MOLECULE = new BNode().toImage();
    public static final Image BH_PLUS_MOLECULE = new BHPlusNode().toImage();
    public static final Image H2O_MOLECULE = new H2ONode().toImage();
    public static final Image H3O_PLUS_MOLECULE = new H3OPlusNode().toImage();
    public static final Image HA_MOLECULE = new HANode().toImage();
    public static final Image M_PLUS_MOLECULE = new MPlusNode().toImage();
    public static final Image MOH_MOLECULE = new MOHNode().toImage();
    public static final Image OH_MINUS_MOLECULE = new OHMinusNode().toImage();;

    private static final BufferedImage getBufferedImage( String resourceName ) {
        return ABSResources.getBufferedImage( resourceName );
    }

    private static final Icon getIcon( String resourceName ) {
        return createIcon( ABSResources.getBufferedImage( resourceName ), 1 );
    }

    public static final Icon createIcon( Image image, double scale ) {
        PImage imageNode = new PImage( image );
        imageNode.scale( scale ); // use Piccolo to scale images
        return new ImageIcon( imageNode.toImage() );
    }
}
