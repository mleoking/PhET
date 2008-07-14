/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.umd.cs.piccolo.PNode;

/**
 * This class is a Piccolo PNode extension that represents a particle in the view.
 *
 * @author John Blanco
 */
public class ParticleNode extends PNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final ParticleNode TEST = new ParticleNode( StatesOfMatterAtom.TEST, new ModelViewTransform() );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private StatesOfMatterAtom m_particle;
    private final StatesOfMatterAtom.Listener m_particleListener;
    private ModelViewTransform m_mvt;
    Point2D.Double m_position;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public ParticleNode( StatesOfMatterAtom particle, ModelViewTransform mvt ) {

        m_particle = particle;
        m_mvt = mvt;

        // Local initialization.
        m_position = new Point2D.Double();

        // Set ourself up to listen to this particle.
        m_particleListener = new StatesOfMatterAtom.Listener() {

            public void positionChanged() {
                updatePosition();
            }

            public void particleRemoved( StatesOfMatterAtom particle ) {
                handleParticleRemoved( particle );
            }
        };
        particle.addListener( m_particleListener );

        // Create the image that will represent this particle.
        SphericalNode sphere = new SphericalNode( particle.getRadius() * 2, choosePaint( particle ), false );
        addChild( sphere );

        // Set ourself to be non-pickable so that we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        updatePosition();
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

    public void updatePosition() {
        if ( m_particle != null ) {
            m_mvt.modelToView( m_particle.getPositionReference(), m_position );
            setOffset( m_position );
        }
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    /**
     * Handle the removal of the particle within the model that is being
     * represented in the view by this particle node.  This is done by
     * removing ourself from the canvas and by cleaning up any memory
     * references so that we can be garbage collected.
     * 
     * @param particle
     */
    private void handleParticleRemoved( StatesOfMatterAtom particle ) {

        // Remove ourself from the canvas.
        PNode parent = getParent();
        if ( parent != null ) {
            parent.removeChild( this );
        }

        // Remove all children, since they have a reference to this object.
        removeAllChildren();

        // Explicitly clear our reference to the particle in the model.
        m_particle = null;
    }

    /**
     * Select the color for this particle.
     * 
     * @return Color to use for this particle.
     */
    Paint choosePaint( StatesOfMatterAtom atom ) {

        Color baseColor;

        if ( atom instanceof ArgonAtom ) {
            baseColor = new Color( 0x0099aa );
        }
        else if ( atom instanceof NeonAtom ) {
            baseColor = new Color( 0xdd4400 );
        }
        else if ( atom instanceof OxygenAtom ) {
            baseColor = new Color( 0x55aa00 );
        }
        else if ( atom instanceof HydrogenAtom ) {
//            baseColor = new Color( 0xdd00dd );
            baseColor = Color.RED;
        }
        else {
            baseColor = Color.WHITE;
        }

//        double atomRadius = atom.getRadius();
//        Paint paint = new RoundGradientPaint( atomRadius / 3, atomRadius / 3, Color.WHITE, 
//                new Point2D.Double( atomRadius, atomRadius ), baseColor );

        return baseColor;
    }
}
