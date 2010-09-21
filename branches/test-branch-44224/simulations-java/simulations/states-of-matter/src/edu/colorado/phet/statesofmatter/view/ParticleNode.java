/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.ConfigurableStatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom2;
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
	
	public static final double OVERLAP_ENLARGEMENT_FACTOR = 1.25;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    protected StatesOfMatterAtom m_particle;
    private final StatesOfMatterAtom.Listener m_particleListener;
    private ModelViewTransform m_mvt;
    private Point2D.Double m_position;
    private SphericalNode m_sphere;
    private boolean m_useGradient = false;
    private boolean m_overlapEnabled = false;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Main constructor.
     * 
     * @param particle - The particle in the model that this node will represent in the view.
     * @param mvt - The model view transform for transforming particle position.
     * @param useGradient - True to use a gradient when displaying the node, false if not.  The gradient is
     * computationally intensive to create, so use only when needed.
     * @param enableOverlap - True if the node should be larger than the actual particle, thus allowing particles
     * to overlap when they collide.
     */
    public ParticleNode( StatesOfMatterAtom particle, ModelViewTransform mvt, boolean useGradient, 
    		boolean enableOverlap ) {

    	if ((mvt == null) || (particle == null)){
    		throw new IllegalArgumentException();
    	}
    	
        m_particle = particle;
        m_mvt = mvt;
        m_useGradient = useGradient;
        m_overlapEnabled = enableOverlap;

        // Local initialization.
        m_position = new Point2D.Double();

        // Set ourself up to listen to this particle.
        m_particleListener = new StatesOfMatterAtom.Adapter() {

            public void positionChanged() {
                updatePosition();
            }

            public void particleRemoved( StatesOfMatterAtom particle ) {
                handleParticleRemoved( particle );
            }
            
            public void radiusChanged(){
                handleParticleRadiusChanged();
            }
        };
        particle.addListener( m_particleListener );

        // Decide of the diameter of the sphere/circle.
        double sphereDiameter = particle.getRadius() * 2;
        if (m_overlapEnabled){
        	// Overlap is enabled, so make the shape slightly larger than
        	// the radius of the sphere so that overlap will occur during
        	// inter-particle collisions.
        	sphereDiameter = sphereDiameter * OVERLAP_ENLARGEMENT_FACTOR;
        }
        
        // Create the node that will represent this particle.  If we are
        // using a gradient, specify that an image should be used, since it
        // will be less computationally intensive to move it around.
        m_sphere = new SphericalNode( sphereDiameter, choosePaint( particle ), useGradient );
        addChild( m_sphere );
        
        // Set ourself to be non-pickable so that we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        updatePosition();
    }
    
    public ParticleNode( StatesOfMatterAtom particle, ModelViewTransform mvt ) {
    	this( particle, mvt, false, false ); // If the user doesn't specify, a hard circle with no gradient is assumed.
    }
    
    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public boolean getGradientEnabled(){
    	return m_useGradient;
    }
    
    public void setGradientEnabled(boolean gradientEnabled){
    	if (m_useGradient != gradientEnabled){
    		
        	m_useGradient = gradientEnabled;
        	
        	if (m_useGradient){
        		m_sphere.setConvertToImage(true);
        		m_sphere.setPaint( choosePaint(m_particle) );
        	}
        	else{
        		m_sphere.setConvertToImage(false);
        		m_sphere.setPaint( chooseColor(m_particle) );
        	}
    	}
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private void updatePosition() {
        if ( m_particle != null ) {
            m_mvt.modelToView( m_particle.getPositionReference(), m_position );
            setOffset( m_position );
        }
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
    	
    	if (m_useGradient){
    		// If the size changes, the gradient must also change to match.
    		m_sphere.setPaint( choosePaint(m_particle) );
    	}
    	double sphereDiameter = m_particle.getRadius() * 2;
    	if (m_overlapEnabled){
    		// Make node larger than particle so that overlap appears to
    		// happen when the particles collide.
    		sphereDiameter = sphereDiameter * OVERLAP_ENLARGEMENT_FACTOR;
    	}
        m_sphere.setDiameter( sphereDiameter );
    }

    /**
     * Select the color for this particle.
     * 
     * @return Color to use for this particle.
     */
    protected Paint choosePaint( StatesOfMatterAtom atom ) {

    	Color baseColor = chooseColor( atom );
    	Color darkenedBaseColor = ColorUtils.darkerColor(baseColor, 0.9);
    	Color transparentDarkenedBasedColor = new Color(darkenedBaseColor.getRed(), darkenedBaseColor.getGreen(), 
    			darkenedBaseColor.getBlue(), 20);
    	
    	if (m_useGradient){
    		double radius = m_overlapEnabled ? atom.getRadius() * OVERLAP_ENLARGEMENT_FACTOR : atom.getRadius();
        
    		return ( new RoundGradientPaint( 0, 0, baseColor,
                    new Point2D.Double( -radius, radius ), transparentDarkenedBasedColor ) );
    	}
    	else{
    		return baseColor;
    	}
    }
    
    /**
     * Choose the base color for the node based on the type of atom.
     * 
     * @param atom
     * @return
     */
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
        else if ( atom instanceof ConfigurableStatesOfMatterAtom ) {
            baseColor = new Color( 0xCC66CC );
        }
        else {
            baseColor = Color.WHITE;
        }

        return baseColor;
    }
}
