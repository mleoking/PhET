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

    static private HashMap imageMap = new HashMap();

    static public BufferedImage getIonImage( Class ionClass ) {
        return (BufferedImage)imageMap.get( ionClass );
    }

    static {
        IonGraphic igNa = createPImage( new Sodium() );
            imageMap.put( Sodium.class, igNa.getImage() );
        IonGraphic igCl = createPImage( new Chloride( ));
            imageMap.put( Chloride.class, igCl.getImage() );
    }


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

    static private IonGraphic createPImage( Ion ion ) {
        IonGraphic ig = new IonGraphic( ion, SolubleSaltsConfig.BLUE_ION_IMAGE_NAME );

        if( ion instanceof Chloride ) {
            ig.setColor( new Color( 0, 100, 0 ) );
        }
        if( ion instanceof Sodium ) {
            ig.setColor( Color.orange );
            ig.setPolarityMarkerColor( Color.black );
        }
        return ig;
    }

    public void ionRemoved( IonEvent event ) {
        IonGraphic ig = (IonGraphic)ionToGraphicMap.get( event.getIon() );
        graphicContainer.removeChild( ig );
        ionToGraphicMap.remove( event.getIon() );
    }

    static private BufferedImage setColor( Color color, BufferedImage bImg ) {
        MakeDuotoneImageOp op = new MakeDuotoneImageOp( color );
        return op.filter( bImg, null );
    }
}
