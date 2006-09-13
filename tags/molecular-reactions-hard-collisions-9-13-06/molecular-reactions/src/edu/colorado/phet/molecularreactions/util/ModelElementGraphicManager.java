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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.molecularreactions.model.ProvisionalBond;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

import java.util.*;

/**
 * ModelElementGraphicManager
 * <p/>
 * Creates graphics when elements are added to the model, and adds the graphics in a well defined set
 * of layers according to their types. When model elements leave the model, its corresponding graphic
 * is removed from the view.
 * <p>
 * The graphics are created by instances of ModelElementGraphicManager.GraphicFactory that are created
 * by the client and plugged into the ModelElementGraphicManager with the addGraphicFactory() method.
 * ModelElementGraphicManager.GraphicFactory is an abstract class whose concrete subclasses must
 * implement the public abstract PNode createGraphic( ModelElement modelElement ) method.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModelElementGraphicManager extends PublishingModel.ModelListenerAdapter {

    private Map modelElementClassToGraphicFactory = new HashMap();

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

    /**
     * Constructor
     * <p>
     * If you use this constructor and then add GraphicFactory instances to it,
     * call scanModel() to create graphics for the model elements that are
     * already in the model
     *
     * @param model
     * @param canvas
     */
    public ModelElementGraphicManager( PublishingModel model, PNode canvas ) {
        this.canvas = canvas;
        this.model = model;
        model.addListener( this );

        // Add layer nodes in the layer order we want to maintain
        canvas.addChild( moleculeLayer );
        canvas.addChild( boxLayer );
    }

    /**
     * Constructor
     * <p>
     * This constructor calls scanModel(), so that instances of ModelElements
     * that are already in the model get graphics created for them. DO NOT
     * call scanModel() after using this constructor, or you'll get duplicate
     * graphic instances.
     *
     * @param model
     * @param canvas
     * @param graphicFactories  A list of GraphicFactory instances
     */
    public ModelElementGraphicManager( PublishingModel model,
                                       PNode canvas,
                                       List graphicFactories ) {
        this( model, canvas );
        for( int i = 0; i < graphicFactories.size(); i++ ) {
            GraphicFactory graphicFactory = (GraphicFactory)graphicFactories.get( i );
            addGraphicFactory( graphicFactory );
        }
        scanModel();
    }

    /**
     *
     * @param graphicFactory
     */
    public void addGraphicFactory( GraphicFactory graphicFactory ) {
        modelElementClassToGraphicFactory.put( graphicFactory.getModelElementClass(),
                                               graphicFactory );
    }

    /**
     * Scans the model and creates graphics for all the model elements it
     * currently has that don't have graphics. Use this after you have
     * added a new factory to the ModelElementGraphicManager instance.
     * <p>
     * This method is called by the constructor of this class that gets a list
     * of GraphicFactories as an argument, so it will pick up all the elements
     * that are already in the model.
     */
    public void scanModel() {
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            ModelElement modelElement = (ModelElement)modelElements.get( i );
            if( modelElementToGraphicMap.get( modelElement ) == null ) {
                modelElementAdded( modelElement );
            }
        }
    }

    /**
     * When a ModelElement is added to the Model, we create a graphic, if a factor has be installed for that
     * class of ModelElement
     *
     * @param modelElement
     */
    public void modelElementAdded( ModelElement modelElement ) {
        PNode graphic = null;
        PNode layer = canvas;

        // Look up the inheritance chain for a graphic factory that will work for the
        // model element
        Class modelElementClass = modelElement.getClass();
        GraphicFactory graphicFactory = null;
        while( modelElementClass != Object.class && graphicFactory == null ) {
            graphicFactory = (GraphicFactory)modelElementClassToGraphicFactory.get( modelElementClass );
            modelElementClass = modelElementClass.getSuperclass();
        }

        // If we found a graphic factory for the model element, have it make a graphic
        // and add it to the view
        if( graphicFactory != null ) {
            graphic = graphicFactory.createGraphic( modelElement );
            if( graphicFactory.getLayer() != null ) {
                layer = graphicFactory.getLayer();
            }
            if( invisibleGraphicClasses.contains( graphic.getClass() ) ) {
                graphic.setVisible( false );
            }
            modelElementToGraphicMap.put( modelElement, new GraphicRecord( graphic, layer ) );
            addGraphic( graphic, layer );
        }
    }

    /**
     * When a model element is removed from the model, we remove its associated graphic
     * from the layer it's on, and remove the graphic from our records
     *
     * @param modelElement
     */
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
     * Adds a graphic directly to the canvas. This is provided with a public interface so that
     * clients can add graphicsfor object that are not ModelElements.
     *
     * @param graphic
     */
    public void addGraphic( PNode graphic ) {
        addGraphic( graphic, canvas );
    }


    /**
     * Adds a graphic directly to a specified layer on the canvas. This is provided with a
     * public interface so that clients can add graphicsfor object that are not ModelElements.
     *
     * @param graphic
     */
    public void addGraphic( PNode graphic, PNode layer ) {
        layer.addChild( graphic );
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

    /**
     * Get all the graphics for model elemenst of a specified class
     *
     * @param modelElementClass
     * @return all the graphics for the model elements of a specified class
     */
    public List getGraphicsForModelElementClass( Class modelElementClass ) {
        List graphics = new ArrayList();
        Iterator keyIt = modelElementToGraphicMap.keySet().iterator();
        while( keyIt.hasNext() ) {
            ModelElement modelElement = (ModelElement)keyIt.next();
            if( modelElementClass.isInstance( modelElement )) {
                graphics.add( modelElement );
            }
        }
        return graphics;
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

    /**
     * A factory class that creates instances of graphics for a specific class of ModelElement
     */
    public static abstract class GraphicFactory {
        private Class modelElementClass;
        private PNode layer;

        /**
         *
         * @param modelElementClass The class of ModelElement that this factory creates graphics for
         * @param layer The layer on which graphics created by this factory are to be placed
         */
        protected GraphicFactory( Class modelElementClass, PNode layer ) {
            this.modelElementClass = modelElementClass;
            this.layer = layer;
        }

        public Class getModelElementClass() {
            return modelElementClass;
        }

        public PNode getLayer() {
            return layer;
        }

        public abstract PNode createGraphic( ModelElement modelElement );
    }
}
