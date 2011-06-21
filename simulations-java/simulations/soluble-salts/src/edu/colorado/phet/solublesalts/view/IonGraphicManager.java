// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.ion.*;
import edu.umd.cs.piccolo.PNode;

/**
 * IonGraphicManager
 * <p/>
 * Creates graphics for ions when they are added to a model and adds the graphics to a canvas,
 * and removes the graphics from the canvas when the ions leave the model
 *
 * @author Ron LeMaster
 */
public class IonGraphicManager implements IonListener {

    //----------------------------------------------------------------
    // Class data and methods
    //----------------------------------------------------------------

    static private HashMap<Class, BufferedImage> imageMap = new HashMap<Class, BufferedImage>();

    /**
     * Returns the buffered image for a specified ion class
     *
     * @param ionClass
     * @return a BufferedImage
     */
    static public BufferedImage getIonImage( Class ionClass ) {
        BufferedImage bi = imageMap.get( ionClass );
        if ( bi == null ) {
            throw new RuntimeException( "Ion class not recognized" );
        }
        return imageMap.get( ionClass );
    }

    static {
        putImage( new ConfigurableCation(), new Color( 0, 0, 0 ) );
        putImage( new ConfigurableAnion(), new Color( 230, 230, 230 ) );
        putImage( new Sodium(), new Color( 200, 0, 60 ) );
        putImage( new Chlorine(), new Color( 0, 100, 0 ) );
        putImage( new Lead(), Color.red );
        putImage( new Silver(), Color.black );
        putImage( new Iodide(), new Color( 140, 10, 10 ) );
        putImage( new Copper(), new Color( 4, 110, 40 ) );
        putImage( new Hydroxide(), new Color( 255, 90, 13 ) );
        putImage( new Chromium(), Color.black );
        putImage( new Strontium(), new Color( 220, 0, 160 ) );
        putImage( new Phosphate(), new Color( 20, 140, 30 ) );
        putImage( new Bromine(), new Color( 120, 100, 60 ) );
        putImage( new Mercury(), new Color( 160, 80, 210 ) );
        putImage( new Thallium(), new Color( 180, 0, 220 ) );
        putImage( new Sulfur(), new Color( 255, 255, 0 ) );
        putImage( new Arsenate(), new Color( 255, 90, 13 ) );
    }

    public static void putImage( Ion ion, Color color ) {
        IonGraphic ig = new IonGraphic( ion, SolubleSaltsConfig.BLUE_ION_IMAGE_NAME );
        if ( color != null ) {
            ig.setColor( color );
        }
        else {
            throw new RuntimeException( "Ion class not recognized" );
        }
        putImage( ion.getClass(), (BufferedImage) ig.getImage() );
    }

    public static void putImage( Class clazz, BufferedImage image ) {
        imageMap.put( clazz, image );
    }

    protected IonGraphic createPImage( Ion ion ) {
        final BufferedImage image = imageMap.get( ion.getClass() );
        return createImage( ion, image );
    }

    protected IonGraphic createImage( Ion ion, BufferedImage image ) {
        return new IonGraphic( ion, image );
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private PNode graphicContainer;
    private HashMap ionToGraphicMap = new HashMap();

    public IonGraphicManager( PNode graphicContainer ) {
        this.graphicContainer = graphicContainer;
    }

    public void ionAdded( IonEvent event ) {
        IonGraphic ig = createPImage( event.getIon() );
        graphicContainer.addChild( ig );
        ionToGraphicMap.put( event.getIon(), ig );
    }

    public void ionRemoved( IonEvent event ) {
        IonGraphic ig = (IonGraphic) ionToGraphicMap.get( event.getIon() );
        if ( ig != null ) {
            graphicContainer.removeChild( ig );
            ionToGraphicMap.remove( event.getIon() );
        }
    }
}
