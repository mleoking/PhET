/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.TemperatureControl;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.factories.CompositeMoleculeGraphicFactory;
import edu.colorado.phet.molecularreactions.view.factories.ProvisionalBondGraphicFactory;
import edu.colorado.phet.molecularreactions.view.factories.SimpleMoleculeGraphicFactory;
import edu.colorado.phet.molecularreactions.view.factories.BondGraphicFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * SpatialView
 * <p/>
 * A view of an MRModel that shows all the molecules in the model, and the box containing them, in a 2D layout.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpatialView extends PNode {
    private Color background = MRConfig.SPATIAL_VIEW_BACKGROUND;
    private PNode moleculeLayer = new PNode();
    private PNode bondLayer = new PNode();
    private PNode boxLayer = new PNode();
    private PNode topLayer = new PNode();
    private ModelElementGraphicManager megm;

    /**
     * @param module
     * @param size
     */
    public SpatialView( MRModule module, Dimension size ) {
        MRModel model = module.getMRModel();
        PSwingCanvas pSwingCanvas = (PSwingCanvas)module.getSimulationPanel();
        PPath canvas = new PPath( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ), new BasicStroke( 1 ) );
        canvas.setPaint( background );
        addChild( canvas );

        // Add the layers to the canvas in their z order
        addChild( moleculeLayer );
        addChild( bondLayer );
        addChild( boxLayer );
        addChild( topLayer );

        // Create the graphic manager and add required factories to it
        megm = new ModelElementGraphicManager( model, canvas );
        megm.addGraphicFactory( new SimpleMoleculeGraphicFactory( module.getMRModel(), moleculeLayer ) );
        megm.addGraphicFactory( new BoxGraphicFactory() );
        megm.addGraphicFactory( new CompositeMoleculeGraphicFactory( bondLayer ) );
        megm.addGraphicFactory( new ProvisionalBondGraphicFactory( bondLayer ) );
        megm.addGraphicFactory( new BondGraphicFactory( bondLayer ) );
        megm.addGraphicFactory( new TemperatureControlGraphicFactory( pSwingCanvas, bondLayer ) );
        megm.scanModel();

        // Graphic that shows the components of the reaction
        ReactionGraphic reactionGraphic = new ReactionGraphic( model.getReaction(), Color.black, model );
        reactionGraphic.setOffset( model.getBox().getMinX() + model.getBox().getWidth() / 2,
                                   canvas.getHeight() - reactionGraphic.getHeight() - 20 );
        canvas.addChild( reactionGraphic );

        // Add the thermometer
        ThermometerGraphic thermometerGraphic = new ThermometerGraphic( model,
                                                                        module.getClock(),
                                                                        10,
                                                                        120 );
        thermometerGraphic.setOffset( model.getBox().getMaxX() - 20,
                                      model.getBox().getMinY() - 20 );
        megm.addGraphic( thermometerGraphic, topLayer );
    }

    public void setGraphicTypeVisible( boolean visible ) {
        megm.setAllOfTypeVisible( BondGraphic.class, visible );
        megm.setAllOfTypeVisible( ProvisionalBondGraphic.class, visible );
    }

    public void addGraphicFactory( ModelElementGraphicManager.GraphicFactory graphicFactory ) {
        megm.addGraphicFactory( graphicFactory );
    }

    public PNode getTopLayer() {
        return topLayer;
    }

    //--------------------------------------------------------------------------------------------------
    // Graphic factory classes
    //--------------------------------------------------------------------------------------------------

    private class BoxGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected BoxGraphicFactory() {
            super( Box2D.class, boxLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            BoxGraphic boxGraphic = new BoxGraphic( (Box2D)modelElement );
            boxGraphic.setOffset( ((Box2D)modelElement).getPosition() );
            return boxGraphic;
        }
    }

    private class TemperatureControlGraphicFactory extends ModelElementGraphicManager.GraphicFactory {
        private PSwingCanvas pSwingCanvas;

        protected TemperatureControlGraphicFactory( PSwingCanvas pSwingCanvas, PNode layer ) {
            super( TemperatureControl.class, layer );
            this.pSwingCanvas = pSwingCanvas;
        }

        public PNode createGraphic( ModelElement modelElement ) {
            TemperatureControlGraphic graphic = new TemperatureControlGraphic( pSwingCanvas,
                                                                               (TemperatureControl)modelElement );
            return graphic;
        }
    }

}
