/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.util;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.molecularreactions.model.RoundMolecule;
import edu.colorado.phet.molecularreactions.model.CompoundMolecule;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.collision.Box2D;
import edu.umd.cs.piccolo.PNode;

import java.util.*;

/**
 * ModelElementGraphicManager
 * <p/>
 * Creates graphics when elements are added to the model, and adds the graphics in a well defined set
 * of layers according to their types.
 * <p>
 * When model elements leave the model, its corresponding graphic is removed from the view
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModelElementGraphicManager extends PublishingModel.ModelListenerAdapter {

//    private AbstractMriModule module;
    private PhetPCanvas phetPCanvas;
    // The PNode on which all dipole graphics are placed
    private PNode canvas;
    // A map of model elements to their graphics
    private HashMap modelElementToGraphicMap = new HashMap();
    // Set of graphic classes that are currently invisible (need to use a set rather than
    // a list so that if a class gets added twice, it can still be cleared with one message)
    private Set invisibleGraphicClasses = new HashSet();

    // Special composite nodes that hold graphics of specific types. This is the way we get things to
    // stay in layers on the canvas
    private PNode moleculeLayer = new PNode();
    private PNode boxLayer = new PNode();
    private PublishingModel model;
    private Module module;

    /**
     * Constructor
     *
     * @param phetPCanvas
     * @param canvas
     */
    public ModelElementGraphicManager( Module module, PhetPCanvas phetPCanvas, PNode canvas ) {
        this.module = module;
        this.phetPCanvas = phetPCanvas;
        this.canvas = canvas;
        this.model = (PublishingModel)module.getModel();

        // Add layer nodes in the layer order we want to maintain
        canvas.addChild( moleculeLayer );
        canvas.addChild( boxLayer );
    }

    private void scanModel( PublishingModel model ) {
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            ModelElement modelElement = (ModelElement)modelElements.get( i );
            modelElementAdded( modelElement );
        }
    }

    public void modelElementAdded( ModelElement modelElement ) {
        PNode graphic = null;
        PNode layer = canvas;

        // Based on the type of the model element, create a graphic for it and set the layer appropriately
        if( modelElement instanceof Box2D ) {

        }

        if( modelElement instanceof RoundMolecule ){

        }

        if( modelElement instanceof CompoundMolecule  ) {

        }

        // If we created a graphic, add it to the canvas
        if( graphic != null ) {
            if( invisibleGraphicClasses.contains( graphic.getClass() ) ) {
                graphic.setVisible( false );
            }
            modelElementToGraphicMap.put( modelElement, new GraphicRecord( graphic, layer ) );
            addGraphic( graphic, layer );
        }
    }

    public void addGraphic( PNode graphic ) {
        addGraphic( graphic, canvas );
    }

    public void addGraphic( PNode graphic, PNode layer ) {
        layer.addChild( graphic );
    }

    public void modelElementRemoved( ModelElement modelElement ) {
        GraphicRecord graphicRecord = (GraphicRecord)modelElementToGraphicMap.get( modelElement );
        if( graphicRecord != null ) {
            PNode layer = graphicRecord.getLayer();
            PNode graphic = graphicRecord.getGraphic();
            layer.removeChild( graphic );
            modelElementToGraphicMap.remove( modelElement );
        }
    }

    /**
     * Sets the visibility of all graphics of a specified type
     *
     * @param graphicClass
     * @param isVisible
     */
    public void setAllOfTypeVisible( Class graphicClass, boolean isVisible ) {
        Iterator nodeIt = canvas.getAllNodes().iterator();
        while( nodeIt.hasNext() ) {
            PNode node = (PNode)nodeIt.next();
            if( graphicClass.isInstance( node ) ) {
                node.setVisible( isVisible );
            }
        }

        if( !isVisible ) {
            invisibleGraphicClasses.add( graphicClass );
        }
        else {
            invisibleGraphicClasses.remove( graphicClass );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * A data structure binding a graphic to the layer it is in
     */
    private static class GraphicRecord {
        private PNode graphic;
        private PNode layer;

        public GraphicRecord( PNode graphic, PNode layer ) {
            this.graphic = graphic;
            this.layer = layer;
        }

        public PNode getGraphic() {
            return graphic;
        }

        public PNode getLayer() {
            return layer;
        }
    }
}
