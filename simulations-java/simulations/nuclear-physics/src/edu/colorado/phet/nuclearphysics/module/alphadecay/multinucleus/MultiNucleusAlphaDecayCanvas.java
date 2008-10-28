/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.model.Proton;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModel;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayTimeChart;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.view.BucketOfNucleiNode;
import edu.colorado.phet.nuclearphysics.view.NeutronModelNode;
import edu.colorado.phet.nuclearphysics.view.ProtonModelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayCanvas extends PhetPCanvas {
    
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
    
    // Proportionate size of the bucket.
    private final double BUCKET_WIDTH = CANVAS_WIDTH * 0.2;
    private final double BUCKET_HEIGHT = BUCKET_WIDTH * 0.7;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private AlphaDecayTimeChart _alphaDecayTimeChart;
    private GradientButtonNode _resetButtonNode;
    private Point2D _bucketNodeLocation;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public MultiNucleusAlphaDecayCanvas(MultiNucleusAlphaDecayModel multiNucleusAlphaDecayModel) {
        
        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR, 
                        getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Add the button for resetting the nucleus to the canvas.
        _resetButtonNode = new GradientButtonNode(NuclearPhysicsStrings.RESET_ALL_NUCLEI, 22, new Color(0xff9900));
        addScreenChild(_resetButtonNode);
        
        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
            }
        });

        // Add the chart that shows the decay time.
        // TODO: JPB TBD - Temp workaround in place to keep chart visible while new model is developed.
        // Clean up when model is ready.
        SingleNucleusAlphaDecayModel singleNucleusAlphaDecayModel = new SingleNucleusAlphaDecayModel(new NuclearPhysicsClock(24, 10));
        _alphaDecayTimeChart = new AlphaDecayTimeChart(singleNucleusAlphaDecayModel.getClock(), 
        		singleNucleusAlphaDecayModel.getAtomNucleus());
        addScreenChild( _alphaDecayTimeChart );
        
        // Add the bucket containing the atoms.
        BucketOfNucleiNode bucketNode = new BucketOfNucleiNode(BUCKET_WIDTH, BUCKET_HEIGHT);
        addWorldChild(bucketNode);
        _bucketNodeLocation = new Point2D.Double(CANVAS_WIDTH * 0.20, CANVAS_HEIGHT * 0.30);
        bucketNode.setOffset( _bucketNodeLocation );
        
        // Register with the bucket for notifications of nuclei being pulled
        // out and dropped on the canvas.
        bucketNode.addListener(new BucketOfNucleiNode.Listener(){
        	public void nucleusExtracted(PNode nucleusNode){
        		addNucleusNodeFromBucket(nucleusNode);
        	}
        });

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            
            /**
             * This method is called when the canvas is resized.  In response,
             * we generally pass this event on to child nodes that need to be
             * aware of it.
             */
            public void componentResized( ComponentEvent e ) {
                
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
    
    /**
     * Add a node to the canvas that was extracted from the bucket.  The
     * tricky part about this is making sure that the location is correct.
     * @param nucleusNode
     */
    private void addNucleusNodeFromBucket(PNode nucleusNode){
    	/*
    	Point2D originalPosition = nucleusNode.getOffset();
    	Point2D globalPosition = nucleusNode.localToGlobal(originalPosition);
    	Point2D convertedPosition = getRoot().globalToLocal(globalPosition);
    	nucleusNode.setOffset(convertedPosition);
    	*/
    	Point2D nucleusPosition = nucleusNode.getOffset();
    	nucleusNode.setOffset(nucleusPosition.getX() + _bucketNodeLocation.getX(), 
    			nucleusPosition.getY() + _bucketNodeLocation.getY());
    	addWorldChild(nucleusNode);
    }
}
