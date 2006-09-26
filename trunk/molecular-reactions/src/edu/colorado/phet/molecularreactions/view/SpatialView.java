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
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
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
    Color background = MRConfig.SPATIAL_VIEW_BACKGROUND;
    PNode moleculeLayer = new PNode();
    PNode bondLayer = new PNode();
    PNode boxLayer = new PNode();

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

        // Create the graphic manager and add required factories to it
        ModelElementGraphicManager megm = new ModelElementGraphicManager( model, canvas );
        megm.addGraphicFactory( new SimpleMoleculeGraphicFactory( moleculeLayer ) );
        megm.addGraphicFactory( new BoxGraphicFactory() );
        megm.addGraphicFactory( new CompositeMoleculeGraphicFactory() );
        megm.addGraphicFactory( new ProvisionalBondGraphicFactory() );
        megm.addGraphicFactory( new BondGraphicFactory( bondLayer ) );
        megm.scanModel();

        // Temperature control
        createTemperatureControl( model, pSwingCanvas );

        ReactionGraphic reactionGraphic = new ReactionGraphic( model.getReaction(), Color.black );
//        reactionGraphic.setOffset( 20, 100 );
        reactionGraphic.setOffset( model.getBox().getMinX() + model.getBox().getWidth() / 2,
                                   canvas.getHeight() - reactionGraphic.getHeight() - 20 );
        canvas.addChild( reactionGraphic );

    }


    private void createTemperatureControl( MRModel model, PSwingCanvas pSwingCanvas ) {
        TemperatureControl tempCtrl = new TemperatureControl( model );
        model.addModelElement( tempCtrl );
        TemperatureControlGraphic tempCtrlGraphic = new TemperatureControlGraphic( pSwingCanvas, tempCtrl );
        tempCtrlGraphic.setOffset( 200, 200 );
        addChild( tempCtrlGraphic );
        tempCtrlGraphic.setOffset( ( model.getBox().getMaxX() + model.getBox().getMinX() ) / 2,
                                   model.getBox().getMaxY() + 30 );
    }

    private void createMoleculeCounters( PPath canvas, PSwingCanvas pSwingCanvas, MRModel model ) {
        int xSpacing = (int)canvas.getWidth() / 5;
        MoleculeCounterPNode aCounter = new MoleculeCounterPNode( pSwingCanvas, model, MoleculeA.class );
        aCounter.setOffset( 1 * xSpacing - aCounter.getFullBounds().getWidth() / 2,
                            canvas.getHeight() );
        addChild( aCounter );
        MoleculeCounterPNode abCounter = new MoleculeCounterPNode( pSwingCanvas, model, MoleculeAB.class );
        abCounter.setOffset( 2 * xSpacing - aCounter.getFullBounds().getWidth() / 2,
                             canvas.getHeight() );
        addChild( abCounter );
        MoleculeCounterPNode bcCounter = new MoleculeCounterPNode( pSwingCanvas, model, MoleculeBC.class );
        bcCounter.setOffset( 3 * xSpacing - aCounter.getFullBounds().getWidth() / 2,
                             canvas.getHeight() );
        addChild( bcCounter );
        MoleculeCounterPNode cCounter = new MoleculeCounterPNode( pSwingCanvas, model, MoleculeC.class );
        cCounter.setOffset( 4 * xSpacing - aCounter.getFullBounds().getWidth() / 2,
                            canvas.getHeight() );
        addChild( cCounter );
    }

    //--------------------------------------------------------------------------------------------------
    // Graphic factory classes
    //--------------------------------------------------------------------------------------------------

    private class CompositeMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected CompositeMoleculeGraphicFactory() {
            super( CompositeMolecule.class, bondLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            if( modelElement instanceof CompositeMolecule ) {
                return new CompositeMoleculeGraphic( (CompositeMolecule)modelElement );
            }
            else {
                return null;
            }
        }
    }

    private class SimpleMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected SimpleMoleculeGraphicFactory( PNode moleculeLayer ) {
            super( SimpleMolecule.class, moleculeLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new SpatialSimpleMoleculeGraphic( (SimpleMolecule)modelElement );
        }
    }

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

    private class ProvisionalBondGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected ProvisionalBondGraphicFactory() {
            super( ProvisionalBond.class, bondLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new ProvisionalBondGraphic( (ProvisionalBond)modelElement );
        }
    }


}
