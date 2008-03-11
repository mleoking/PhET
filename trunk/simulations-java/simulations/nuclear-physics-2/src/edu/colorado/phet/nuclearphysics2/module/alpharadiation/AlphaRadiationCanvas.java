package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics2.view.AlphaRadiationChart;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;
import edu.umd.cs.piccolo.util.PDimension;


public class AlphaRadiationCanvas extends PhetPCanvas {
    
    private AtomicNucleusNode _atomicNucleusNode; 
    private AlphaParticleNode _alphaParticleNode;
    private AlphaRadiationChart _alphaRadiationChart;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AlphaRadiationCanvas(AlphaRadiationModel alphaRadiationModel) {
        
        // Call the constructor that will allow the canvas to be scaled.  The
        // units are assumed to be femtometers (fm).  As a point of reference,
        // the nucleus of an atom of uranium is about 15 fm in diameter.  We
        // are also assuming an initial aspect ratio of 4:3.
        super(new PDimension(150.0d, 115.0d));
        
        // Set the background color.
        setBackground( NuclearPhysics2Constants.CANVAS_BACKGROUND );
        
        // Add the nodes that depict the decay process to the canvas.
        _atomicNucleusNode = new AtomicNucleusNode(alphaRadiationModel.getAtom());
        addWorldChild( _atomicNucleusNode );
        _alphaParticleNode = new AlphaParticleNode(alphaRadiationModel.getAlphaParticle());
        addWorldChild( _alphaParticleNode );
        
        // Add the chart that depicts the tunneling energy threshold to the
        // canvas.  The initial size is arbitrary and will be scaled when the
        // canvas is painted.
        _alphaRadiationChart = new AlphaRadiationChart();
        addScreenChild( _alphaRadiationChart );

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                _alphaRadiationChart.componentResized( getWidth(), getHeight() );
            }
        } );
    }
}
