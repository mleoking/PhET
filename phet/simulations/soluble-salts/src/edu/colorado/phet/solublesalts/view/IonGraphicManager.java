/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.ion.*;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * IonGraphicManager
 * <p/>
 * Creates graphics for ions when they are added to a model and adds the graphics to a canvas,
 * and removes the graphics from the canvas when the ions leave the model
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonGraphicManager implements IonListener {

    //----------------------------------------------------------------
    // Class data and methods
    //----------------------------------------------------------------

    static private HashMap imageMap = new HashMap();

    /**
     * Returns the buffered image for a specified ion class
     * @param ionClass
     * @return a BufferedImage
     */
    static public BufferedImage getIonImage( Class ionClass ) {
        BufferedImage bi = (BufferedImage)imageMap.get( ionClass );
        if( bi == null ) {
            throw new RuntimeException( "Ion class not recognized" );
        }
        return (BufferedImage)imageMap.get( ionClass );
    }

    static {
        putImage( new ConfigurableCation(), new Color( 0, 0, 0 ) );
        putImage( new ConfigurableAnion(), new Color( 230, 230, 230 ) );
//        putImage( new ConfigurableAnion(), new Color( 200, 200, 200 ) );
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
//        putImage( new Sulfur(), new Color( 255, 90, 13 ) );
        putImage( new Arsenate(), new Color( 255, 90, 13 ));
    }

    static private void putImage( Ion ion, Color color ) {
        IonGraphic ig = new IonGraphic( ion, SolubleSaltsConfig.BLUE_ION_IMAGE_NAME );
        if( color != null ) {
            ig.setColor( color );
        }
        else {
            throw new RuntimeException( "Ion class not recognized" );
        }
        imageMap.put( ion.getClass(), ig.getImage() );
    }

    static private IonGraphic createPImage( Ion ion ) {
        BufferedImage bi = (BufferedImage)imageMap.get( ion.getClass() );
        IonGraphic ig = new IonGraphic( ion, bi );
        return ig;
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
        IonGraphic ig = (IonGraphic)ionToGraphicMap.get( event.getIon() );
        if( ig != null ) {
            graphicContainer.removeChild( ig );
            ionToGraphicMap.remove( event.getIon() );
        }
    }
}
