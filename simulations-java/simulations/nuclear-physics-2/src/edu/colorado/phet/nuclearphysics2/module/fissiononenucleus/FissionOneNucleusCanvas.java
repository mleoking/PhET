/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.fissiononenucleus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.colorado.phet.nuclearphysics2.model.Proton;
import edu.colorado.phet.nuclearphysics2.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics2.view.FissionEnergyChart;
import edu.colorado.phet.nuclearphysics2.view.NeutronNode;
import edu.colorado.phet.nuclearphysics2.view.NeutronSourceNode;
import edu.colorado.phet.nuclearphysics2.view.ProtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * This class is the canvas upon which the simulation of the fission of a
 * single atomic nucleus is presented to the user. 
 *
 * @author John Blanco
 */
public class FissionOneNucleusCanvas extends PhetPCanvas {
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Constant that sets the scale of this sim, which is in femtometers.
    private final double SCALE = 0.8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private AtomicNucleusNode _atomicNucleusNode; 
    private NeutronSourceNode _neutronSourceNode; 
    private FissionEnergyChart _fissionEnergyChart;
    private ArrayList _freeParticles = new ArrayList();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public FissionOneNucleusCanvas(FissionOneNucleusModel fissionOneNucleusModel) {
        
        // Set up the transform strategy so that the scale is in femtometers
        // and so that the center of the screen above the chart is at
        // coordinate location (0,0).
        setWorldTransformStrategy( new RenderingSizeStrategy(this, new PDimension(150.0d * SCALE, 115.0d * SCALE) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/2, getHeight()/4 );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Create a parent node where we will display the nucleus.  This is
        // being done so that a label can be placed over the top of it.
        PNode nucleusLayer = new PNode();
        addWorldChild(nucleusLayer);
        
        // Get the nucleus from the model and then get the constituents
        // and create a visible node for each.
        AtomicNucleus atomicNucleus = fissionOneNucleusModel.getAtomicNucleus();
        ArrayList nucleusConstituents = atomicNucleus.getConstituents();
        
        // Add a node for each particle that comprises the nucleus.
        for (int i = 0; i < nucleusConstituents.size(); i++){
            
            Object constituent = nucleusConstituents.get( i );
            
            if (constituent instanceof AlphaParticle){
                // Add a visible representation of the alpha particle to the canvas.
                AlphaParticleNode alphaNode = new AlphaParticleNode((AlphaParticle)constituent);
                nucleusLayer.addChild( alphaNode );
            }
            else if (constituent instanceof Proton){
                // Add a visible representation of the proton to the canvas.
                ProtonNode protonNode = new ProtonNode((Proton)constituent);
                nucleusLayer.addChild( protonNode );
            }
            else if (constituent instanceof Neutron){
                // Add a visible representation of the neutron to the canvas.
                NeutronNode neutronNode = new NeutronNode((Neutron)constituent);
                nucleusLayer.addChild( neutronNode );
            }
            else {
                // There is some unexpected object in the list of constituents
                // of the nucleus.  This should never happen and should be
                // debugged if it does.
                assert false;
            }
        }
        
        // Add the nucleus node to the canvas.  Since the constiuents are
        // handled individually, this just shows the label.
        _atomicNucleusNode = new AtomicNucleusNode(fissionOneNucleusModel.getAtomicNucleus());
        addWorldChild( _atomicNucleusNode );
        
        // Add the neutron source to the canvas.
        _neutronSourceNode = new NeutronSourceNode(fissionOneNucleusModel.getNeutronSource());
        addWorldChild( _neutronSourceNode );
        
        // Register as a listener with the neutron source so that we will know
        // when new neutrons have been produced.
        fissionOneNucleusModel.getNeutronSource().addListener( new NeutronSource.Listener (){
            public void neutronGenerated(Neutron neutron){
                // Add this new neutron to the canvas.
                NeutronNode neutronNode = new NeutronNode(neutron);
                addWorldChild( neutronNode );
            }
            public void positionChanged(){
                // Ignore this, since we don't really care about it.
            }
        });
        
        // JPB TPB - add a line to show where the 0 y axis is.
        PPath yAxisLine = new PPath(new Line2D.Double(-100, 0, 100, 0)); 
        yAxisLine.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        yAxisLine.setStrokePaint( new Color(0x990099) );
        addWorldChild(yAxisLine);

        // Add to the canvas the chart that will depict the energy of the nucleus.
        _fissionEnergyChart = new FissionEnergyChart();
        addScreenChild( _fissionEnergyChart );

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                _fissionEnergyChart.componentResized( getWidth(), getHeight() );
            }
        } );
    }
}
