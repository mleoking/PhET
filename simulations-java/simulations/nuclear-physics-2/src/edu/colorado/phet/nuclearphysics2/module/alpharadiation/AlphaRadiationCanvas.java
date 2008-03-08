package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics2.view.AlphaRadiationChart;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;


public class AlphaRadiationCanvas extends PhetPCanvas {
    
    private AtomicNucleusNode _atomicNucleusNode; 
    private AlphaParticleNode _alphaParticleNode;
    private AlphaRadiationChart _alphaRadiationChart;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AlphaRadiationCanvas(AlphaRadiationModel alphaRadiationModel) {
        
        // Set the background color.
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Add the nodes that depict the decay process to the canvas.
        _atomicNucleusNode = new AtomicNucleusNode(alphaRadiationModel.getAtom());
        addWorldChild( _atomicNucleusNode );
        _alphaParticleNode = new AlphaParticleNode(alphaRadiationModel.getAlphaParticle());
        addWorldChild( _alphaParticleNode );
        
        // Add the chart that depicts the tunneling energy threshold to the canvas.
        _alphaRadiationChart = new AlphaRadiationChart(100, 500);
        addScreenChild( _alphaRadiationChart );

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                _alphaRadiationChart.componentResized( getWidth(), getHeight() );
            }
        } );
    }
}
