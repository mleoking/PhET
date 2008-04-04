/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.Proton;
import edu.colorado.phet.nuclearphysics2.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics2.view.AlphaRadiationEnergyChart;
import edu.colorado.phet.nuclearphysics2.view.AlphaRadiationTimeChart;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics2.view.NeutronNode;
import edu.colorado.phet.nuclearphysics2.view.ProtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Alpha Radiation tab of this simulation.
 *
 * @author John Blanco
 */
public class AlphaRadiationCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 100;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 4.0;
    
    // Contants that control where the charts are placed.
    private final double CHART_AREA_FRACTION = 0.5; // Fraction of canvas for charts.
    private final double ENERGY_CHART_FRACTION = 0.6; // Fraction of chart area for this chart.
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private AlphaRadiationModel _alphaRadiationModel;
    private AtomicNucleusNode _nucleusNode;
    private AlphaRadiationEnergyChart _alphaRadiationEnergyChart;
    private AlphaRadiationTimeChart _alphaRadiationTimeChart;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public AlphaRadiationCanvas(AlphaRadiationModel alphaRadiationModel) {

        _alphaRadiationModel = alphaRadiationModel;
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/WIDTH_TRANSLATION_FACTOR, 
                        getHeight()/HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Get the nucleus from the model and then get the constituents
        // and create a visible node for each.
        AtomicNucleus atomicNucleus = alphaRadiationModel.getAtomNucleus();
        ArrayList nucleusConstituents = atomicNucleus.getConstituents();
        
        // Create a parent node where we will display the nucleus.  This is
        // being done so that a label can be placed over the top of it.
        PNode nucleusLayer = new PNode();
        addWorldChild(nucleusLayer);
        
        // Add a node for each particle that comprises the nucleus.
        for (int i = 0; i < nucleusConstituents.size(); i++){
            
            Object constituent = nucleusConstituents.get( i );
            
            if (constituent instanceof AlphaParticle){
                // Add a visible representation of the alpha particle to the canvas.
                AlphaParticleNode alphaNode = new AlphaParticleNode((AlphaParticle)constituent);
                nucleusLayer.addChild( alphaNode );
            }
            else if (constituent instanceof Neutron){
                // Add a visible representation of the neutron to the canvas.
                NeutronNode neutronNode = new NeutronNode((Neutron)constituent);
                nucleusLayer.addChild( neutronNode );
            }
            else if (constituent instanceof Proton){
                // Add a visible representation of the proton to the canvas.
                ProtonNode protonNode = new ProtonNode((Proton)constituent);
                nucleusLayer.addChild( protonNode );
            }
            else {
                // There is some unexpected object in the list of constituents
                // of the nucleus.  This should never happen and should be
                // debugged if it does.
                assert false;
            }
        }

        // Add the nucleus node itself to the canvas, which is actually only
        // the label, since the individual nodes show the individual particles.
        // This must be added last so that it can appear on top of the nucleus.
        PNode labelLayer = new PNode();
        addWorldChild(labelLayer);
        _nucleusNode = new AtomicNucleusNode(atomicNucleus);
        labelLayer.addChild( _nucleusNode );

        
        // Register with the model for notifications of alpha particles coming
        // and going.
        // TODO: This may be obsolete, since the canvas now (as of March 19
        // 2008) gets the nucleus and registers with the sub-particles thereof
        // instead of watching the particles come and go directly from the
        // model.  Remove this if it is not needed within, say, a week.
        alphaRadiationModel.addListener( new AlphaRadiationModel.Listener(  ){
            
            /**
             * A new particle has been added in the model, so we need to
             * display it on the canvas.
             */
            public void particleAdded(AlphaParticle alphaParticle){
                
                AlphaParticleNode alphaParticleNode = new AlphaParticleNode(alphaParticle);
                
                // Add the particle to the world.
                addWorldChild( 0, alphaParticleNode );
                
                // Map the particle to the node so that we can remove it later.
                _mapAlphaParticlesToNodes.put( alphaParticle, alphaParticleNode );
            }
            
            /**
             * A particle has been removed from the model, so we need to
             * remove its representation from the canvas (i.e. view).
             */
            public void particleRemoved(AlphaParticle alphaParticle){
                AlphaParticleNode alphaParticleNode = 
                    (AlphaParticleNode)_mapAlphaParticlesToNodes.get( alphaParticle );
                assert alphaParticleNode != null;
                removeWorldChild( alphaParticleNode );
                _mapAlphaParticlesToNodes.remove( alphaParticleNode );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Add the chart that depicts the tunneling energy threshold.
        _alphaRadiationEnergyChart = new AlphaRadiationEnergyChart(50);
        addScreenChild( _alphaRadiationEnergyChart );
        
        // Add the breakout radius to the canvas.
        double radius = _alphaRadiationModel.getAtomNucleus().getTunnelingRegionRadius();
        PPath breakoutCircle = new PPath(new Ellipse2D.Double(-radius, -radius, 2*radius, 2*radius));
        breakoutCircle.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {0.75f, 0.75f }, 0) );
        breakoutCircle.setStrokePaint( new Color(0x990099) );
        addWorldChild(breakoutCircle);
        
        // Add the lines that make it clear that the tunneling radius is at
        // the point where the particle energy exceeds the potential energy.
        PPath leftBreakoutLine = new PPath(new Line2D.Double(-radius, 
                CANVAS_HEIGHT * HEIGHT_TRANSLATION_FACTOR, -radius, 
                CANVAS_HEIGHT * (1 - HEIGHT_TRANSLATION_FACTOR)));
        leftBreakoutLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        leftBreakoutLine.setStrokePaint( new Color(0x990099) );
        addWorldChild(leftBreakoutLine);

        PPath rightBreakoutLine = new PPath(new Line2D.Double(radius, 
                CANVAS_HEIGHT * HEIGHT_TRANSLATION_FACTOR, radius, 
                CANVAS_HEIGHT * (1 - HEIGHT_TRANSLATION_FACTOR)));
        rightBreakoutLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        rightBreakoutLine.setStrokePaint( new Color(0x990099) );
        addWorldChild(rightBreakoutLine);

        // Add the chart that shows the decay time.
        _alphaRadiationTimeChart = new AlphaRadiationTimeChart(_alphaRadiationModel.getClock(), 
                _alphaRadiationModel.getAtomNucleus());
        addScreenChild( _alphaRadiationTimeChart );

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            
            /**
             * This method is called when the canvas is resized.  In response,
             * we generally pass this event on to child nodes that need to be
             * aware of it.
             */
            public void componentResized( ComponentEvent e ) {
                
                // Get the diameter of the atomic nucleus so that it can be
                // used to set the width of the energy well in the chart.
                double nucleusDiameter = _alphaRadiationModel.getAtomNucleus().getDiameter();
                Dimension2D nucleasDiameterDim = new PDimension(nucleusDiameter, nucleusDiameter);
                
                // Convert the diameter to screen coordinates so that we have
                // the right units for setting the width of the energy well in
                // the chart.
                Dimension2D converted1 = _nucleusNode.localToGlobal( nucleasDiameterDim );
                Dimension2D converted2 = _alphaRadiationEnergyChart.globalToLocal( converted1 );
                
                // Set the new desired width of the energy well.
                _alphaRadiationEnergyChart.setEnergyWellWidth(converted2.getHeight());

                // Position the energy chart.
                Rectangle2D energyChartRect = new Rectangle2D.Double(0, getHeight() * CHART_AREA_FRACTION, getWidth(),
                        getHeight() * CHART_AREA_FRACTION * ENERGY_CHART_FRACTION);
                _alphaRadiationEnergyChart.componentResized( energyChartRect );

                // Position the time chart.
                _alphaRadiationTimeChart.componentResized( 
                        new Rectangle2D.Double( 0, energyChartRect.getMaxY(), getWidth(),
                        getHeight() * CHART_AREA_FRACTION * (1 - ENERGY_CHART_FRACTION)));
            }
        } );
    }
    
    /**
     * Sets the view back to the original state when sim was first started.
     */
    public void reset(){
        _alphaRadiationTimeChart.reset();
    }
}
