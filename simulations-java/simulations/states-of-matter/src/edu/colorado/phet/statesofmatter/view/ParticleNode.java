/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom2;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.model.particle.UserDefinedAtom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

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

    protected StatesOfMatterAtom m_particle;
    private final StatesOfMatterAtom.Listener m_particleListener;
    private ModelViewTransform m_mvt;
    private Point2D.Double m_position;
    private SphericalNode m_sphere;
    private PImage m_sphereImage;

    //----------------------------------------------------------------------------
    // Constructors
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

            public void velocityChanged() {
            }

            public void accelerationChanged() {
            }

            public void particleRemoved( StatesOfMatterAtom particle ) {
                handleParticleRemoved( particle );
            }
            
            public void particleRadiusChanged(){
                handleParticleRadiusChanged();
            }
        };
        particle.addListener( m_particleListener );

        // Create the image that will represent this particle.
        m_sphere = new SphericalNode( particle.getRadius() * 2, choosePaint( particle ), false );
        initGraphics();
        
        // Set ourself to be non-pickable so that we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        updatePosition();
    }
    
    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    protected void updatePosition() {
        if ( m_particle != null ) {
            m_mvt.modelToView( m_particle.getPositionReference(), m_position );
            setOffset( m_position );
        }
    }
    
    protected void initGraphics(){
        addChild( m_sphere );
    }
    
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
     * If the radius of the particle changes, we need to redraw ourself to
     * correspond.
     */
    protected void handleParticleRadiusChanged(){
        m_sphere.setDiameter( m_particle.getRadius() * 2 );
    }

    /**
     * Select the color for this particle.
     * 
     * @return Color to use for this particle.
     */
    protected Paint choosePaint( StatesOfMatterAtom atom ) {

    	return chooseColor( atom );
    }
    
    protected Color chooseColor ( StatesOfMatterAtom atom ){
        Color baseColor;

        if ( atom instanceof ArgonAtom ) {
            baseColor = new Color( 0x009933 );
        }
        else if ( atom instanceof NeonAtom ) {
            baseColor = new Color( 0xdd4400 );
        }
        else if ( atom instanceof OxygenAtom ) {
            baseColor = new Color( 0x4488ff );
        }
        else if ( atom instanceof HydrogenAtom ) {
            baseColor = Color.RED;
        }
        else if ( atom instanceof HydrogenAtom2 ) {
            baseColor = new Color( 0xff4444 );
        }
        else if ( atom instanceof UserDefinedAtom ) {
            baseColor = new Color( 0xCC66CC );
        }
        else {
            baseColor = Color.WHITE;
        }

        return baseColor;
    }

	protected StatesOfMatterAtom getParticle() {
		return m_particle;
	}

	protected void setParticle(StatesOfMatterAtom m_particle) {
		this.m_particle = m_particle;
	}

	protected SphericalNode getSphere() {
		return m_sphere;
	}

	protected void setSphere(SphericalNode m_sphere) {
		this.m_sphere = m_sphere;
	}
}
