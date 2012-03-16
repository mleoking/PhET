// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.*;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.rutherfordscattering.event.ParticleEvent;
import edu.colorado.phet.rutherfordscattering.event.ParticleListener;
import edu.colorado.phet.rutherfordscattering.model.AlphaParticle;
import edu.colorado.phet.rutherfordscattering.model.RSModel;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * TracesNode draws traces of AlphParticles.
 * <p>
 * This node watches the Model, and adds (removes) a trace path whenever an
 * alpha particle ModelElement is added to (removed from) the model.
 * Trace paths are implemented using GeneralPath.
 * This node observes alpha particles, watching for a change in position.
 * When an alpha particle's position changes, its corresponding GeneralPath
 * is updated.
 * <p>
 * Traces can be turned on/off at any time.
 * <p>
 * Running with traces enabled has a performance penalty, since one GeneralPath
 * is updates and drawn for every alpha particle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TracesNode extends PhetPNode implements ParticleListener, Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color TRACE_COLOR = Color.LIGHT_GRAY;
    private static final Stroke TRACE_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private RSModel _model;
    private HashMap _pathMap; // maps AlphaParticle to GeneralPath (view coordinates)
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param model the model that we're listening to
     */
    public TracesNode( RSModel model ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        _model = model;
        _pathMap = new HashMap();
        _enabled = false;
    }
    
    public void cleanup() {
        _model.removeParticleListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables or disables this node.
     * When disabled, no traces are drawn and we stop listening to the model.
     * 
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
        if ( _enabled ) {
            _model.addParticleListener( this );
        }
        else {
            _model.removeParticleListener( this );
            clear();
        }
        setVisible( enabled );
    }
    
    /**
     * Is this node enabled?
     * @return true or false
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    /**
     * Clears all of the traces,
     * stops observing all alpha particles.
     */
    public void clear() {
        Set keys = _pathMap.keySet();
        if ( keys != null ) {
            Iterator i = keys.iterator();
            while ( i.hasNext() ) {
                AlphaParticle alphaParticle = (AlphaParticle) i.next();
                alphaParticle.deleteObserver( this );
            }
            _pathMap.clear();
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------
    
    /*
     * Draws all paths that are in the path map,
     * after drawing itself and any children.
     * 
     * @param paintContext
     */
    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        Collection paths = _pathMap.values();
        if ( paths != null && paths.size() != 0 ) {
            Graphics2D g2 = paintContext.getGraphics();
            Color saveColor = g2.getColor();
            Stroke saveStroke = g2.getStroke();
            g2.setColor( TRACE_COLOR );
            g2.setStroke( TRACE_STROKE );
            Iterator i = paths.iterator();
            while ( i.hasNext() ) {
                GeneralPath path = (GeneralPath) i.next();
                g2.draw( path );
            }
            g2.setColor( saveColor );
            g2.setStroke( saveStroke );
        }
    }
    
    //----------------------------------------------------------------------------
    // ParticleListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When an AlphaParticle is added to the model, this method
     * creates a new GeneralPath for the particle, and starts observing
     * the particle's motion.
     * 
     * @param event
     */
    public void particleAdded( ParticleEvent event ) {
        AlphaParticle alphaParticle = event.getParticle();
        alphaParticle.addObserver( this );
        Point2D p = RSModelViewTransform.transform( alphaParticle.getPositionRef() );
        GeneralPath path = new GeneralPath();
        path.moveTo( (float) p.getX(), (float) p.getY() );
        _pathMap.put( alphaParticle, path );
        repaint();
    }

    /**
     * When an AlphaParticle is removed from the model, this method
     * stops observing the particle's motion and removes the particle's GeneralPath.
     */
    public void particleRemoved( ParticleEvent event ) {
        AlphaParticle alphaParticle = event.getParticle();
        alphaParticle.deleteObserver( this );
        _pathMap.remove( alphaParticle );
        repaint();
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Observes the motion of an AlphaParticle,
     * and adds a line segment to it's GeneralPath.
     */
    public void update( Observable o, Object arg ) {
        if ( o instanceof AlphaParticle && arg == AlphaParticle.PROPERTY_POSITION ) {
            AlphaParticle alphaParticle = (AlphaParticle) o;
            GeneralPath path = (GeneralPath) _pathMap.get( alphaParticle );
            if ( path != null ) {
                Point2D p = RSModelViewTransform.transform( alphaParticle.getPositionRef() );
                path.lineTo( (float) p.getX(), (float) p.getY() );
            }
        }
    }

}
