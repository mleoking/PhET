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
import java.util.Collection;
import java.util.Iterator;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.Neutron;
import edu.colorado.phet.nuclearphysics.common.model.Proton;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayAdapter;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayCompositeNucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayEnergyChart;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.view.AutoPressGradientButtonNode;
import edu.colorado.phet.nuclearphysics.view.NeutronModelNode;
import edu.colorado.phet.nuclearphysics.view.NucleonModelNode;
import edu.colorado.phet.nuclearphysics.view.ProtonModelNode;
import edu.colorado.phet.nuclearphysics.view.SingleNucleusAlphaDecayTimeChart;
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
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in femto meters.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 100;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.45; // 0 = all the way up, 1 = all the way down.
    
    // Constants that control where the charts are placed.
    private final double TIME_CHART_FRACTION = 0.2;   // Fraction of canvas for time chart.
    private final double ENERGY_CHART_FRACTION = 0.35; // Fraction of canvas for energy chart.
    
    // Other constants that affect the appearance of the chart.
    private final Color TUNNELING_MARKERS_COLOR = new Color(150, 0, 150);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private SingleNucleusAlphaDecayModel _singleNucleusAlphaDecayModel;
    private AtomicNucleusNode _nucleusNode;
    private AlphaDecayEnergyChart _alphaDecayEnergyChart;
    private SingleNucleusAlphaDecayTimeChart _alphaDecayTimeChart;
    private AutoPressGradientButtonNode _resetButtonNode;
	private PNode _nucleusLayer;
	private PNode _labelLayer;
	private PPath _tunnelingRegion;
	private PPath _leftTunnelingLine;
	private PPath _rightTunnelingLine;

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
        
        // Register for alpha decay events from the model.
        _singleNucleusAlphaDecayModel.addListener(new AlphaDecayAdapter(){
            public void modelElementAdded(Object modelElement){
            	createNucleusNodes();
            	positionTunnelingMarkers();
            	if (modelElement instanceof AtomicNucleus){
                    ((AtomicNucleus)modelElement).addListener(new AtomicNucleus.Adapter(){
                        public void tunnelingRadiusChanged(){
                        	positionTunnelingMarkers();
                        };
                    });
            	}
            }
            public void modelElementRemoved(Object modelElement){
            	if (modelElement instanceof AlphaDecayCompositeNucleus){
                	removeNucleusNodes();
            	}
            	// TODO: JPB TBD - Need to handle removal of alpha particle.
            }
        });
        
        // Create the layer where nodes that comprise the nucleus will be placed.
        _nucleusLayer = new PNode();
        _nucleusLayer.setPickable( false );
        _nucleusLayer.setChildrenPickable( false );
        _nucleusLayer.setVisible( true );
        addWorldChild(_nucleusLayer);

        // Create the layer where the nucleus label will be placed.
        _labelLayer = new PNode();
        addWorldChild(_labelLayer);
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Add the tunneling radius to the canvas.
        _tunnelingRegion = new PPath();
        _tunnelingRegion.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {0.75f, 0.75f }, 0) );
        _tunnelingRegion.setStrokePaint( TUNNELING_MARKERS_COLOR );
        addWorldChild(_tunnelingRegion); // Put on layer that will go behind the charts.
        
        // Add the chart that depicts the tunneling energy threshold.
        _alphaDecayEnergyChart = new AlphaDecayEnergyChart(singleNucleusAlphaDecayModel, this);
        addScreenChild( _alphaDecayEnergyChart );

        // Add the lines that visually connect that energy chart to the
        // tunneling radius.
        _leftTunnelingLine = new PPath(new Line2D.Double(0, 0, 0, CANVAS_HEIGHT * 0.35));
        _leftTunnelingLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        _leftTunnelingLine.setStrokePaint( TUNNELING_MARKERS_COLOR );
        addWorldChild(_leftTunnelingLine);

        _rightTunnelingLine = new PPath(new Line2D.Double(0, 0, 0, CANVAS_HEIGHT * 0.35));
        _rightTunnelingLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        _rightTunnelingLine.setStrokePaint( TUNNELING_MARKERS_COLOR );
        addWorldChild(_rightTunnelingLine);
        
        // Add to the canvas the button for resetting the nucleus.
        _resetButtonNode = new AutoPressGradientButtonNode(NuclearPhysicsStrings.RESET_NUCLEUS, 22, 
        		NuclearPhysicsConstants.CANVAS_RESET_BUTTON_COLOR);
        addScreenChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                _singleNucleusAlphaDecayModel.resetNucleus();
            }
        });

        // Add the chart that shows the decay time.
        _alphaDecayTimeChart = new SingleNucleusAlphaDecayTimeChart(_singleNucleusAlphaDecayModel);
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

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

	/**
	 * Auto-press the reset button, i.e. make it look like someone or some
	 * THING pressed the button.
	 */
	public void autoPressResetButton(){
		_resetButtonNode.autoPress();
	}

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

	/**
	 * Position the set of markers that depict the tunneling radius for the
	 * nucleus.
	 */
	private void positionTunnelingMarkers(){
		AtomicNucleus nucleus = _singleNucleusAlphaDecayModel.getAtomNucleus();
		if (nucleus != null){
			double tunnelingRadius = nucleus.getTunnelingRegionRadius();
			if (tunnelingRadius < CANVAS_WIDTH){
				_leftTunnelingLine.setOffset(-tunnelingRadius, 0);
				_leftTunnelingLine.setVisible(true);
				_rightTunnelingLine.setOffset(tunnelingRadius, 0);
				_rightTunnelingLine.setVisible(true);
				_tunnelingRegion.setPathTo(new Ellipse2D.Double(0, 0, tunnelingRadius * 2, tunnelingRadius * 2));
				_tunnelingRegion.setOffset(-tunnelingRadius, -tunnelingRadius);
				_tunnelingRegion.setVisible(true);
			}
			else{
				_leftTunnelingLine.setVisible(false);
				_rightTunnelingLine.setVisible(false);
				_tunnelingRegion.setVisible(false);
			}
		}
	}
	
    /**
     * Create the nodes needed to represent the nucleus that is currently in
     * the model.
     */
	private void createNucleusNodes() {
		// Get the nucleus from the model and then get the constituents
        // and create a visible node for each.
        CompositeAtomicNucleus atomicNucleus = _singleNucleusAlphaDecayModel.getAtomNucleus();
        ArrayList nucleusConstituents = atomicNucleus.getConstituents();
        
        // Add a node for each particle that comprises the nucleus.
        for (int i = 0; i < nucleusConstituents.size(); i++){
            
            Object constituent = nucleusConstituents.get( i );
            
            if (constituent instanceof AlphaParticle){
                // Add a visible representation of the alpha particle to the canvas.
                AlphaParticleModelNode alphaNode = new AlphaParticleModelNode((AlphaParticle)constituent);
                alphaNode.setVisible( true );
                _nucleusLayer.addChild( alphaNode );
            }
            else if (constituent instanceof Neutron){
                // Add a visible representation of the neutron to the canvas.
                NeutronModelNode neutronNode = new NeutronModelNode((Neutron)constituent);
                neutronNode.setVisible( true );
                _nucleusLayer.addChild( neutronNode );
            }
            else if (constituent instanceof Proton){
                // Add a visible representation of the proton to the canvas.
                ProtonModelNode protonNode = new ProtonModelNode((Proton)constituent);
                protonNode.setVisible( true );
                _nucleusLayer.addChild( protonNode );
            }
            else {
                // There is some unexpected object in the list of constituents
                // of the nucleus.  This should never happen and should be
                // debugged if it does.
                assert false;
            }
        }

        _nucleusNode = new AtomicNucleusNode(atomicNucleus);
        _labelLayer.addChild( _nucleusNode );
	}
	
    /**
     * Remove and dispose of the nodes that are currently representing the nucleus.
     */
	private void removeNucleusNodes(){

		// Clean up the nodes that comprise the nucleus.
		Collection nucleusLayerNodes = _nucleusLayer.getAllNodes();
		Iterator itr = nucleusLayerNodes.iterator();
		while( itr.hasNext() ){
			Object node = itr.next();
			if (node instanceof AlphaParticleModelNode){
				((AlphaParticleModelNode)node).cleanup();
			}
			else if (node instanceof NucleonModelNode){
				((NucleonModelNode)node).cleanup();
			}
		}
		
		// Clean up the nucleus node itself, which is just the label in this case.
		Collection labelLayerNodes = _labelLayer.getAllNodes();
		itr = labelLayerNodes.iterator();
		while( itr.hasNext() ){
			Object node = itr.next();
			if (node instanceof AtomicNucleusNode){
				((AtomicNucleusNode)node).cleanup();
			}
		}
		
		// TODO: JPB TBD - Not sure if this is sufficient or if it will cause memory leaks.
		_nucleusLayer.removeAllChildren();
		_labelLayer.removeAllChildren();
	}
}
