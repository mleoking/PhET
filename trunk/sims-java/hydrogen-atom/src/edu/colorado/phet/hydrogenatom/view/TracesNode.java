/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.*;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.hydrogenatom.model.AlphaParticle;
import edu.colorado.phet.hydrogenatom.model.Model;
import edu.colorado.phet.hydrogenatom.model.Model.ModelEvent;
import edu.colorado.phet.hydrogenatom.model.Model.ModelListener;
import edu.colorado.phet.piccolo.PhetPNode;
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
 * @version $Revision$
 */
public class TracesNode extends PhetPNode implements ModelListener, Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /*
     * Determines whether traces persist when their corresponding alpha particles
     * leave the model. WARNING! Setting this to true is prohibitively expensive.
     */
    private static final boolean TRACES_PERSIST = false;
    
    private static final Color TRACE_COLOR = Color.LIGHT_GRAY;
    private static final Stroke TRACE_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Model _model;
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
    public TracesNode( Model model ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        _model = model;
        _pathMap = new HashMap();
        _enabled = false;
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
            _model.addModelListener( this );
        }
        else {
            _model.removeModelListener( this );
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
    // ModelListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When an AlphaParticle is added to the model, this method
     * creates a new GeneralPath for the particle, and starts observing
     * the particle's motion.
     * 
     * @param event
     */
    public void modelElementAdded( ModelEvent event ) {
        ModelElement modelElement = event.getModelElement();
        if ( modelElement instanceof AlphaParticle ) {
            AlphaParticle alphaParticle = (AlphaParticle) modelElement;
            alphaParticle.addObserver( this );
            Point2D p = ModelViewTransform.transform( alphaParticle.getPositionRef() );
            GeneralPath path = new GeneralPath();
            path.moveTo( (float)p.getX(), (float)p.getY() );
            _pathMap.put( alphaParticle, path );
            repaint();
        }
    }

    /**
     * When an AlphaParticle is removed from the model, this method
     * stops observing the particle's motion and (depending on the value
     * of TRACES_PERSIST) removes the particle's GeneralPath.
     */
    public void modelElementRemoved( ModelEvent event ) {
        ModelElement modelElement = event.getModelElement();
        if ( modelElement instanceof AlphaParticle ) {
            AlphaParticle alphaParticle = (AlphaParticle) modelElement;
            alphaParticle.deleteObserver( this );
            if ( !TRACES_PERSIST ) {
                _pathMap.remove( alphaParticle );
                repaint();
            }
        }
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
                Point2D p = ModelViewTransform.transform( alphaParticle.getPositionRef() );
                path.lineTo( (float) p.getX(), (float) p.getY() );
            }
        }
    }

}
