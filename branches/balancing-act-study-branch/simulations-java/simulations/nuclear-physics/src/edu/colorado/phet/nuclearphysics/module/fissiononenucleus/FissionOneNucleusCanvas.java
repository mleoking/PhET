// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.fissiononenucleus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;
import edu.colorado.phet.nuclearphysics.common.view.AbstractAtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.common.view.LabeledExplodingAtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.CompositeAtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.NeutronSource;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleModelNode;
import edu.colorado.phet.nuclearphysics.view.FissionEnergyChart;
import edu.colorado.phet.nuclearphysics.view.NeutronSourceNode;
import edu.colorado.phet.nuclearphysics.view.NucleonNode;
import edu.umd.cs.piccolo.PNode;
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

    // Timer for delaying the appearance of the reset button.
    private static final int BUTTON_DELAY_TIME = 1750; // In milliseconds.
    private static final Timer BUTTON_DELAY_TIMER = new Timer( BUTTON_DELAY_TIME, null );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private final PNode _nucleusParticlesLayerNode;
    private final PNode _nucleusLabelsLayerNode;
    private final FissionOneNucleusModel _fissionOneNucleusModel;
    private final AbstractAtomicNucleusNode _atomicNucleusNode;
    private final NeutronSourceNode _neutronSourceNode;
    private final FissionEnergyChart _fissionEnergyChart;
    private final Hashtable _particleToNodeMap;
    private final HTMLImageButtonNode _resetButtonNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public FissionOneNucleusCanvas( FissionOneNucleusModel fissionOneNucleusModel ) {

        // Set up the transform strategy so that the scale is in femtometers
        // and so that the center of the screen above the chart is at
        // coordinate location (0,0).
        setWorldTransformStrategy( new RenderingSizeStrategy( this, new PDimension( 150.0d * SCALE, 115.0d * SCALE ) ) {
            @Override
            protected AffineTransform getPreprocessedTransform() {
                return AffineTransform.getTranslateInstance( getWidth() / 2, getHeight() / 4 );
            }
        } );

        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Register as a listener to the model.
        _fissionOneNucleusModel = fissionOneNucleusModel;
        _fissionOneNucleusModel.addListener( new FissionOneNucleusModel.Listener() {
            public void nucleonRemoved( Nucleon nucleon ) {
                // Remove the nucleon from the canvas and from our records.
                PNode nucleonNode = (PNode) _particleToNodeMap.get( nucleon );
                if ( nucleonNode != null ) {
                    if ( _nucleusParticlesLayerNode.removeChild( nucleonNode ) == null ) {
                        System.err.println( "Error: Unable to locate node for given nucleon." );
                    }
                    _particleToNodeMap.remove( nucleon );
                }
                else {
                    System.err.println( "Error: Unable to locate particle in particle-to-node map." );
                }
            }
        } );

        // Register as a listener to the one nucleus that exists within the
        // model so that we know when decay has occurred.
        _fissionOneNucleusModel.getAtomicNucleus().addListener( new AtomicNucleus.Adapter() {

            @Override
            public void nucleusChangeEvent( AtomicNucleus atomicNucleus, int numProtons, int numNeutrons,
                                            ArrayList byProducts ) {
                if ( byProducts != null ) {
                    // This was a decay event, so start the timer that will
                    // cause the button to be shown after some delay.
                    BUTTON_DELAY_TIMER.restart();
                }
                else {
                    // This must have been a reset event, so hide the button
                    // that allows the user to reset the nucleus.
                    _resetButtonNode.setVisible( false );
                }
            }
        } );

        // Create a parent node where we will display the nucleus particles.
        // This is being done so that a label can be placed over the top of
        // and so that new particles can be added to the world and not end up
        // appearing over other important nodes.
        _nucleusParticlesLayerNode = new PNode();
        addWorldChild( _nucleusParticlesLayerNode );

        // Create a parent node where the nodes that create the labels will
        // appear.  Again, this is done to retain the desired "layering"
        // effect.
        _nucleusLabelsLayerNode = new PNode();
        addWorldChild( _nucleusLabelsLayerNode );

        // Get the nucleus from the model and then get the constituents
        // and create a visible node for each.
        CompositeAtomicNucleus atomicNucleus = _fissionOneNucleusModel.getAtomicNucleus();
        ArrayList nucleusConstituents = atomicNucleus.getConstituents();

        // Add a node for each particle that comprises the nucleus.
        _particleToNodeMap = new Hashtable( nucleusConstituents.size() );
        for ( int i = 0; i < nucleusConstituents.size(); i++ ) {

            Object constituent = nucleusConstituents.get( i );

            if ( constituent instanceof AlphaParticle ) {
                // Add a visible representation of the alpha particle to the canvas.
                AlphaParticleModelNode alphaNode = new AlphaParticleModelNode( (AlphaParticle) constituent );
                _nucleusParticlesLayerNode.addChild( alphaNode );
                _particleToNodeMap.put( constituent, alphaNode );
            }
            else if ( constituent instanceof Nucleon ) {
                // Add a visible representation of the nucleon to the canvas.
                NucleonNode nucleonNode = new NucleonNode( (Nucleon) constituent );
                _nucleusParticlesLayerNode.addChild( nucleonNode );
                _particleToNodeMap.put( constituent, nucleonNode );
            }
            else {
                // There is some unexpected object in the list of constituents
                // of the nucleus.  This should never happen and should be
                // debugged if it does.
                assert false;
            }
        }

        // Add the nucleus node to the canvas.  Since the constituents are
        // handled individually, this just shows the label.
        _atomicNucleusNode = new LabeledExplodingAtomicNucleusNode( fissionOneNucleusModel.getAtomicNucleus() );
        _nucleusLabelsLayerNode.addChild( _atomicNucleusNode );

        // Add the neutron source to the canvas.
        _neutronSourceNode = new NeutronSourceNode( fissionOneNucleusModel.getNeutronSource(), 26 );
        _neutronSourceNode.setRotationEnabled( false );
        addWorldChild( _neutronSourceNode );

        // Register as a listener with the neutron source so that we will know
        // when new neutrons have been produced.
        fissionOneNucleusModel.getNeutronSource().addListener( new NeutronSource.Adapter() {
            @Override
            public void neutronGenerated( Nucleon neutron ) {
                // Add this new neutron to the canvas.
                NucleonNode neutronNode = new NucleonNode( neutron );
                _nucleusParticlesLayerNode.addChild( neutronNode );
                _particleToNodeMap.put( neutron, neutronNode );
            }
        } );

        // Add the button for resetting the nucleus to the canvas.
        _resetButtonNode = new HTMLImageButtonNode( NuclearPhysicsStrings.RESET_NUCLEUS, new PhetFont( Font.BOLD, 16 ),
                                           NuclearPhysicsConstants.CANVAS_RESET_BUTTON_COLOR );
        double desiredResetButtonWidth = _neutronSourceNode.getFullBoundsReference().width;
        _resetButtonNode.setScale( desiredResetButtonWidth / _resetButtonNode.getFullBoundsReference().width );
        _resetButtonNode.setOffset( _neutronSourceNode.getFullBoundsReference().x,
                                    _neutronSourceNode.getFullBoundsReference().y - _resetButtonNode.getFullBoundsReference().height * 2 );
        addWorldChild( _resetButtonNode );
        _resetButtonNode.setVisible( false );  // Initially invisible, becomes visible when nucleus decays.

        // Register to receive button pushes.
        _resetButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                _fissionOneNucleusModel.getClock().resetSimulationTime();
            }
        } );

        // Set up the button delay timer that will make the reset button
        // appear some time after the decay has occurred.
        BUTTON_DELAY_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // Show the button.
                _resetButtonNode.setVisible( true );
                BUTTON_DELAY_TIMER.stop();
            }
        } );

        // Add to the canvas the chart that will depict the energy of the nucleus.
        _fissionEnergyChart = new FissionEnergyChart( _fissionOneNucleusModel, this );
        addScreenChild( _fissionEnergyChart );

        // Add a listener for when the canvas is resized.
        addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized( ComponentEvent e ) {
                _fissionEnergyChart.componentResized( getWidth(), getHeight() );
            }
        } );
    }
}
