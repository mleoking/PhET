/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics2.view.AlphaRadiationChart;
import edu.colorado.phet.nuclearphysics2.view.AtomicNucleusNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed.
 *
 * @author John Blanco
 */
public class AlphaRadiationCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Constant that sets the scale of this sim, which is in femtometers.
    private final double SCALE = 0.8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private AlphaRadiationChart _alphaRadiationChart;
    private PPath _breakoutCircle;
    private HashMap _mapAlphaParticlesToNodes = new HashMap();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public AlphaRadiationCanvas(AlphaRadiationModel alphaRadiationModel) {
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setTransformStrategy( new RenderingSizeStrategy(this, new PDimension(150.0d * SCALE, 115.0d * SCALE) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/2, getHeight()/4 );
            }
        });
        
        // Register with the model for notifications of alpha particles coming
        // and going.
        alphaRadiationModel.addListener( new AlphaRadiationModel.Listener(  ){
            
            /**
             * A new particle has been added in the model, so we need to
             * display it on the canvas.
             */
            public void particleAdded(AlphaParticle alphaParticle){
                
                AlphaParticleNode alphaParticleNode = new AlphaParticleNode(alphaParticle);
                
                // TODO: JPB TBD - Need to determine the best index/layer at
                // which to add this.
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
        
        // Add the breakout radius to the canvas.
        double radius = AlphaRadiationModel.BREAKOUT_RADIUS;
        _breakoutCircle = new PPath(new Ellipse2D.Double(-radius, -radius, 2*radius, 2*radius));
        _breakoutCircle.setStroke( new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                new float[] {2, 2 }, 0) );
        _breakoutCircle.setStrokePaint( new Color(0x990099) );
        addWorldChild(_breakoutCircle);
        

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
