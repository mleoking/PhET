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

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.model.ion.*;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.umd.cs.piccolo.PNode;

import java.util.HashMap;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

    static public BufferedImage getIonImage( Class ionClass ) {
        return (BufferedImage)imageMap.get( ionClass );
    }

    static {
        putImage( new Sodium() );
        putImage( new Chlorine() );
        putImage( new Lead() );
        putImage( new Silver() );
        putImage( new Iodine() );
        putImage( new Copper() );
        putImage( new Hydroxide() );
        putImage( new Chromium() );
        putImage( new Strontium() );
        putImage( new Phosphate() );
        putImage( new Bromine() );
        putImage( new Mercury() );
    }

    static private void putImage( Ion ion ) {
        IonGraphic ig = createPImage( ion );
        imageMap.put( ion.getClass(), ig.getImage() );
    }

    static private IonGraphic createPImage( Ion ion ) {
        IonGraphic ig = new IonGraphic( ion, SolubleSaltsConfig.BLUE_ION_IMAGE_NAME );
        boolean ionClassRecognized = false;

        if( ion instanceof Chlorine ) {
            ig.setColor( new Color( 0, 100, 0 ) );
            ionClassRecognized = true;
        }
        if( ion instanceof Sodium ) {
            ig.setColor( Color.orange );
            ig.setPolarityMarkerColor( Color.black );
            ionClassRecognized = true;
        }
        if( ion instanceof Lead ) {
            ig.setColor( Color.red );
            ig.setPolarityMarkerColor( Color.black );
            ionClassRecognized = true;
        }
        if( ion instanceof Silver ) {
            ig.setColor( Color.black );
            ig.setPolarityMarkerColor( Color.black );
            ionClassRecognized = true;
        }
        if( ion instanceof Iodine ) {
            ig.setColor( new Color( 140, 10, 10 ) );
            ionClassRecognized = true;
        }
        if( ion instanceof Copper ) {
            ig.setColor( new Color( 4, 110, 40 ) );
            ionClassRecognized = true;
        }
        if( ion instanceof Hydroxide ) {
            ig.setColor( new Color( 255, 90, 13 ) );
            ionClassRecognized = true;
        }
        if( ion instanceof Chromium ) {
            ig.setColor( Color.black );
            ionClassRecognized = true;
        }
        if( ion instanceof Strontium ) {
            ig.setColor( new Color( 160, 0, 200 ) );
            ionClassRecognized = true;
        }
        if( ion instanceof Phosphate ) {
            ig.setColor( new Color( 20, 140, 30 ) );
            ionClassRecognized = true;
        }
        if( ion instanceof Mercury ) {
            ig.setColor( new Color( 100, 100, 180 ) );
            ionClassRecognized = true;
        }
        if( ion instanceof Bromine ) {
            ig.setColor( new Color( 120, 100, 60 ) );
            ionClassRecognized = true;
        }

        if( !ionClassRecognized ) {
            throw new RuntimeException( "Ion class not recognized" );
        }
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
