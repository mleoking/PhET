/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.Proton;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayEnergyChart;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayTimeChart;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.view.NeutronModelNode;
import edu.colorado.phet.nuclearphysics.view.ProtonModelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class SingleNucleusAlphaDecayCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 100;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.45; // 0 = all the way up, 1 = all the way down.
    
    // Constants that control where the charts are placed.
    private final double TIME_CHART_FRACTION = 0.2;   // Fraction of canvas for time chart.
    private final double ENERGY_CHART_FRACTION = 0.3; // Fraction of canvas for energy chart.
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private SingleNucleusAlphaDecayModel _singleNucleusAlphaDecayModel;
    private AtomicNucleusNode _nucleusNode;
    private AlphaDecayEnergyChart _alphaDecayEnergyChart;
    private AlphaDecayTimeChart _alphaDecayTimeChart;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();
    private GradientButtonNode _resetButtonNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public SingleNucleusAlphaDecayCanvas(SingleNucleusAlphaDecayModel singleNucleusAlphaDecayModel) {
        
        _singleNucleusAlphaDecayModel = singleNucleusAlphaDecayModel;
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR, 
                        getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Get the nucleus from the model and then get the constituents
        // and create a visible node for each.
        CompositeAtomicNucleus atomicNucleus = singleNucleusAlphaDecayModel.getAtomNucleus();
        ArrayList nucleusConstituents = atomicNucleus.getConstituents();
        
        // Create a parent node where we will display the nucleus.  This is
        // being done so that a label can be placed over the top of it.
        PNode nucleusLayer = new PNode();
        nucleusLayer.setPickable( false );
        nucleusLayer.setChildrenPickable( false );
        nucleusLayer.setVisible( true );
        addWorldChild(nucleusLayer);
        
        // Add a node for each particle that comprises the nucleus.
        for (int i = 0; i < nucleusConstituents.size(); i++){
            
            Object constituent = nucleusConstituents.get( i );
            
            if (constituent instanceof AlphaParticle){
                // Add a visible representation of the alpha particle to the canvas.
                AlphaParticleModelNode alphaNode = new AlphaParticleModelNode((AlphaParticle)constituent);
                alphaNode.setVisible( true );
                nucleusLayer.addChild( alphaNode );
            }
            else if (constituent instanceof Neutron){
                // Add a visible representation of the neutron to the canvas.
                NeutronModelNode neutronNode = new NeutronModelNode((Neutron)constituent);
                neutronNode.setVisible( true );
                nucleusLayer.addChild( neutronNode );
            }
            else if (constituent instanceof Proton){
                // Add a visible representation of the proton to the canvas.
                ProtonModelNode protonNode = new ProtonModelNode((Proton)constituent);
                protonNode.setVisible( true );
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
        // TODO: jblanco - This may be obsolete, since the canvas now (as of March 19
        // 2008) gets the nucleus and registers with the sub-particles thereof
        // instead of watching the particles come and go directly from the
        // model.  Remove this if it is not needed within, say, a week.
        singleNucleusAlphaDecayModel.addListener( new SingleNucleusAlphaDecayModel.Listener(  ){
            
            /**
             * A new particle has been added in the model, so we need to
             * display it on the canvas.
             */
            public void particleAdded(AlphaParticle alphaParticle){
                
                AlphaParticleModelNode alphaParticleNode = new AlphaParticleModelNode(alphaParticle);
                
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
                AlphaParticleModelNode alphaParticleNode = 
                    (AlphaParticleModelNode)_mapAlphaParticlesToNodes.get( alphaParticle );
                assert alphaParticleNode != null;
                removeWorldChild( alphaParticleNode );
                _mapAlphaParticlesToNodes.remove( alphaParticleNode );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Add the chart that depicts the tunneling energy threshold.
        _alphaDecayEnergyChart = new AlphaDecayEnergyChart(singleNucleusAlphaDecayModel, this);
        addScreenChild( _alphaDecayEnergyChart );
        
        // Add the breakout radius to the canvas.
        double radius = _singleNucleusAlphaDecayModel.getAtomNucleus().getTunnelingRegionRadius();
        PPath breakoutCircle = new PPath(new Ellipse2D.Double(-radius, -radius, 2*radius, 2*radius));
        breakoutCircle.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {0.75f, 0.75f }, 0) );
        breakoutCircle.setStrokePaint( new Color(0x990099) );
        addWorldChild(breakoutCircle);
        
        // Add the lines that make it clear that the tunneling radius is at
        // the point where the particle energy exceeds the potential energy.
        PPath leftBreakoutLine = new PPath(new Line2D.Double(-radius, -CANVAS_HEIGHT, -radius, CANVAS_HEIGHT));
        leftBreakoutLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        leftBreakoutLine.setStrokePaint( new Color(0x990099) );
        addWorldChild(leftBreakoutLine);

        PPath rightBreakoutLine = new PPath(new Line2D.Double(radius, -CANVAS_HEIGHT, radius, CANVAS_HEIGHT));
        rightBreakoutLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        rightBreakoutLine.setStrokePaint( new Color(0x990099) );
        addWorldChild(rightBreakoutLine);
        
        // Add the button for resetting the nucleus to the canvas.
        _resetButtonNode = new GradientButtonNode(NuclearPhysicsStrings.RESET_NUCLEUS, 22, new Color(0xff9900));
        addScreenChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                _singleNucleusAlphaDecayModel.getClock().resetSimulationTime();
            }
        });

        // Add the chart that shows the decay time.
        _alphaDecayTimeChart = new AlphaDecayTimeChart(_singleNucleusAlphaDecayModel.getClock(), 
                _singleNucleusAlphaDecayModel.getAtomNucleus());
        addScreenChild( _alphaDecayTimeChart );

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            
            /**
             * This method is called when the canvas is resized.  In response,
             * we generally pass this event on to child nodes that need to be
             * aware of it.
             */
            public void componentResized( ComponentEvent e ) {
                
                // Redraw the energy chart.
                Rectangle2D energyChartRect = new Rectangle2D.Double(0, 0, getWidth(), 
                        getHeight() * ENERGY_CHART_FRACTION);
                _alphaDecayEnergyChart.componentResized( energyChartRect );

                // Position the energy chart.
                _alphaDecayEnergyChart.setOffset( 0, 
                        getHeight() - _alphaDecayEnergyChart.getFullBoundsReference().height );
                
                // Redraw the time chart.
                _alphaDecayTimeChart.componentResized( new Rectangle2D.Double( 0, 0, getWidth(),
                        getHeight() * TIME_CHART_FRACTION));
                
                // Position the time chart.
                _alphaDecayTimeChart.setOffset( 0, 0 );
                
                // Position the reset button.
                _resetButtonNode.setOffset( (0.82 * getWidth()) - (_resetButtonNode.getFullBoundsReference().width / 2),
                        0.30 * getHeight() );
            }
        } );
    }
    
    /**
     * Sets the view back to the original state when sim was first started.
     */
    public void reset(){
        _alphaDecayTimeChart.reset();
    }
}
