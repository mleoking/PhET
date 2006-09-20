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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.BoxGraphic;
import edu.colorado.phet.molecularreactions.view.CompositeMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.SpatialSimpleMoleculeGraphic;
import edu.colorado.phet.collision.Box2D;
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
    Color background = new Color( 255, 255, 200 );
    PNode moleculeLayer = new PNode();
    PNode bondLayer = new PNode();
    PNode boxLayer = new PNode();

    public SpatialView( MRModel model, PSwingCanvas pSwingCanvas, Dimension size ) {
        PPath canvas = new PPath( new Rectangle2D.Double( 0,0, size.getWidth(), size.getHeight()), new BasicStroke( 1 ));
        canvas.setPaint( background );
        addChild( canvas );

        // Add the layers to the canvas in their z order
        addChild( bondLayer );
        addChild( moleculeLayer );
        addChild( boxLayer );

        // Create the graphic manager and add required factories to it
        ModelElementGraphicManager megm = new ModelElementGraphicManager( model, canvas );
        megm.addGraphicFactory( new SimpleMoleculeGraphicFactory( moleculeLayer ) );
        megm.addGraphicFactory( new BoxGraphicFactory() );
        megm.addGraphicFactory( new CompositeMoleculeGraphicFactory() );
        megm.addGraphicFactory( new ProvisionalBondGraphicFactory() );
        megm.scanModel();

        // Molecule counters
        MoleculeCounterPNode aCounter = new MoleculeCounterPNode( pSwingCanvas, model, MoleculeA.class );
        aCounter.setOffset( 20, canvas.getHeight() );
        addChild( aCounter );
        MoleculeCounterPNode abCounter = new MoleculeCounterPNode( pSwingCanvas, model, MoleculeAB.class );
        abCounter.setOffset( 80, canvas.getHeight() );
        addChild( abCounter );
        MoleculeCounterPNode bcCounter = new MoleculeCounterPNode( pSwingCanvas, model, MoleculeBC.class );
        bcCounter.setOffset( 140, canvas.getHeight() );
        addChild( bcCounter );
        MoleculeCounterPNode cCounter = new MoleculeCounterPNode( pSwingCanvas, model, MoleculeC.class );
        cCounter.setOffset( 200, canvas.getHeight() );
        addChild( cCounter );

        // Temperature control
        TemperatureControl tempCtrl = new TemperatureControl( model );
        model.addModelElement( tempCtrl );
        TemperatureControlGraphic tempCtrlGraphic = new TemperatureControlGraphic( pSwingCanvas, tempCtrl );
        tempCtrlGraphic.setOffset( 200, 200 );
        addChild( tempCtrlGraphic );
        tempCtrlGraphic.setOffset( (model.getBox().getMaxX() + model.getBox().getMinX()) / 2,
                                   model.getBox().getMaxY() + 30 );
    }


    private class CompositeMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected CompositeMoleculeGraphicFactory() {
            super( CompositeMolecule.class, bondLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new CompositeMoleculeGraphic( (CompositeMolecule)modelElement );
        }
    }

    private class SimpleMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected SimpleMoleculeGraphicFactory( PNode moleculeLayer ) {
            super( SimpleMolecule.class, moleculeLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new SpatialSimpleMoleculeGraphic( (SimpleMolecule)modelElement ) ;
        }
    }

    private class BoxGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected BoxGraphicFactory() {
            super( Box2D.class, boxLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new BoxGraphic( (Box2D)modelElement ) ;
        }
    }

    private class ProvisionalBondGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected ProvisionalBondGraphicFactory() {
            super( ProvisionalBond.class, bondLayer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new ProvisionalBondGraphic( (ProvisionalBond)modelElement ) ;
        }
    }

    
}
