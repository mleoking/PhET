/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.quantum.view.PhotonGraphic;
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.mri.view.DipoleGraphic;
import edu.colorado.phet.mri.view.ElectromagnetGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * ModelElementGraphicManager
 * <p/>
 * Creates graphics when elements are added to the model, and adds the graphics in a well defined set
 * of layers according to their types.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModelElementGraphicManager extends MriModel.ChangeAdapter {

    private PhetPCanvas phetPCanvas;
    // The PNode on which all dipole graphics are placed
    private PNode canvas;
    // A map of model elements to their graphics
    private HashMap modelElementToGraphicMap = new HashMap();
    // List of graphic classes that are currently invisible
    private List invisibleGraphicClasses = new ArrayList();

    // Special composite nodes that hold graphics of specific types. This is the way we get things to
    // stay in layers on the canvas
    private PNode dipolesLayer = new PNode();
    private PNode rfLayer = new PNode();
    private PNode sampleChamberLayer = new PNode();
    private PNode magnetCoilLayer = new PNode();
    private PNode controlLayer = new PNode();
    private PNode headLayer = new PNode();

    /**
     * Constructor
     * @param phetPCanvas
     * @param canvas
     */
    public ModelElementGraphicManager( PhetPCanvas phetPCanvas, PNode canvas ) {
        this.phetPCanvas = phetPCanvas;
        this.canvas = canvas;

        // Add composite nodes in the layer order we want to maintain
        canvas.addChild( magnetCoilLayer );
        canvas.addChild( headLayer );
        canvas.addChild( sampleChamberLayer );
        canvas.addChild( dipolesLayer );
        canvas.addChild( rfLayer );
//        canvas.addChild( headLayer );
        canvas.addChild( controlLayer );

    }

    public void scanModel( MriModel model ) {
        List modelElements = model.getModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            ModelElement modelElement = (ModelElement)modelElements.get( i );
            modelElementAdded( modelElement );
        }
    }

    public void modelElementAdded( ModelElement modelElement ) {
        PNode graphic = null;
        PNode layer = canvas;

        if( modelElement instanceof Dipole ) {
            graphic = new DipoleGraphic( (Dipole)modelElement );
            layer = dipolesLayer;
        }
        else if( modelElement instanceof SampleChamber ) {
            graphic = new SampleChamberGraphic( (SampleChamber)modelElement );
            layer = sampleChamberLayer;
        }
        else if( modelElement instanceof GradientElectromagnet ) {
            graphic = new ElectromagnetGraphic( (GradientElectromagnet)modelElement );
            layer = magnetCoilLayer;
        }
        else if( modelElement instanceof RadiowaveSource
            && !( modelElement instanceof PlaneWaveCycle ) ) {
            graphic = new RadiowaveSourceGraphic( (RadiowaveSource)modelElement, phetPCanvas );
            layer = controlLayer;
        }
        else if( modelElement instanceof Photon ) {
            graphic = PhotonGraphic.getInstance( (Photon)modelElement );
            layer = rfLayer;
        }
        else if( modelElement instanceof PlaneWaveMedium ) {
            PlaneWaveMedium pwm = (PlaneWaveMedium)modelElement;
            double maxOpacity = 0.25;
            if( pwm.getSource() instanceof MriEmittedPhoton ) {
                   maxOpacity = 0.5;
            }
            graphic = new PlaneWaveGraphic( pwm,
                                            maxOpacity,
                                            Color.black );
            layer = rfLayer;
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
        Collection graphics = modelElementToGraphicMap.values();
        for( Iterator iterator = graphics.iterator(); iterator.hasNext(); ) {
            GraphicRecord graphicRecord = (GraphicRecord)iterator.next();
            if( graphicClass.isInstance( graphicRecord.getGraphic() ) ) {
                PNode graphic = (PNode)graphicRecord.getGraphic();
                graphic.setVisible( isVisible );
            }
        }

        if( !isVisible ) {
            invisibleGraphicClasses.add( graphicClass );
        }
        else {
            invisibleGraphicClasses.remove( graphicClass );
        }
    }

    public PNode getDipolesLayer() {
        return dipolesLayer;
    }

    public PNode getRfLayer() {
        return rfLayer;
    }

    public PNode getSampleChamberLayer() {
        return sampleChamberLayer;
    }

    public PNode getMagnetCoilLayer() {
        return magnetCoilLayer;
    }

    public PNode getControlLayer() {
        return controlLayer;
    }

    public PNode getHeadLayer() {
        return headLayer;
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
