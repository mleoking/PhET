/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.fissiononenucleus;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics2.view.FissionEnergyChart;
import edu.colorado.phet.nuclearphysics2.view.NeutronSourceNode;
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

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public FissionOneNucleusCanvas(FissionOneNucleusModel fissionOneNucleusModel) {
        
        // Set up the transform strategy so that the scale is in femtometers
        // and so that the center of the screen above the chart is at
        // coordinate location (0,0).
        setTransformStrategy( new RenderingSizeStrategy(this, new PDimension(150.0d * SCALE, 115.0d * SCALE) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/2, getHeight()/4 );
            }
        });
        
        // TODO: JPB TBD - Register with the model for events.
        
        // Set the background color.
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Add the nucleus node to the canvas.
        _atomicNucleusNode = new AtomicNucleusNode(fissionOneNucleusModel.getAtom());
        addWorldChild( _atomicNucleusNode );
        
        // Add the neutron source to the canvas.
        _neutronSourceNode = new NeutronSourceNode(fissionOneNucleusModel.getNeutronSource());
        addWorldChild( _neutronSourceNode );
        
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
