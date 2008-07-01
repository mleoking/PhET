/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ParticlesNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double ACID_PH_THRESHOLD = 6;
    private static final double BASE_PH_THRESHOLD = 8;
    
    private static final int DEFAULT_NEUTRAL_PARTICLES = 100;
    private static final int DEFAULT_MAX_PARTICLES = 5000;
    private static final double DEFAULT_DIAMETER = 4;
    private static final int DEFAULT_MAJORITY_ALPHA = 128; // 0-255, transparent-opaque
    private static final int DEFAULT_MINORITY_ALPHA = 255; // 0-255, transparent-opaque
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final PBounds _containerBounds;
    private final PNode _particlesParent;
    private final Random _randomX, _randomY;
   
    private Double _pH; // used to watch for pH change in the liquid
    private int _maxParticles;
    private int _neutralParticles;
    private double _diameter;
    private int _majorityAlpha, _minorityAlpha;
    private Color _h3oColor, _ohColor;
    private int _numberOfH3O, _numberOfOH;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
   
    public ParticlesNode( Liquid liquid, PBounds containerBounds ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _containerBounds = new PBounds( containerBounds );
        _randomX = new Random();
        _randomY = new Random();
        _pH = _liquid.getPH();
        
        _particlesParent = new PNode();
        addChild( _particlesParent );
        
        _maxParticles = DEFAULT_MAX_PARTICLES;
        _neutralParticles = DEFAULT_NEUTRAL_PARTICLES;
        _diameter = DEFAULT_DIAMETER;
        _majorityAlpha = DEFAULT_MAJORITY_ALPHA;
        _minorityAlpha = DEFAULT_MINORITY_ALPHA;
        _h3oColor = PHScaleConstants.H3O_COLOR;
        _ohColor = PHScaleConstants.OH_COLOR;
        _numberOfH3O = 0;
        _numberOfOH = 0;
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setMaxParticles( int maxParticles ) {
        if ( maxParticles != _maxParticles ) {
            _maxParticles = maxParticles;
            createParticles();
        }
    }
    
    public int getMaxParticles() {
        return _maxParticles;
    }
    
    public void setNeutralParticles( int neutralParticles ) {
        if ( neutralParticles != _neutralParticles ) {
            _neutralParticles = neutralParticles;
            createParticles();
        }
    }
    
    public int getNeutralParticles() {
        return _neutralParticles;
    }
    
    public void setParticleDiameter( double diameter ) {
        if ( diameter != _diameter ) {
            _diameter = diameter;
            int count = _particlesParent.getChildrenCount();
            for ( int i = 0; i < count; i++ ) {
                ( (ParticleNode) _particlesParent.getChild( i ) ).setDiameter( diameter );
            }
        }
    }
    
    public double getParticleDiameter() {
        return _diameter;
    }
    
    public void setMajorityAlpha( int alpha ) {
        if ( alpha != _majorityAlpha ) {
            _majorityAlpha = alpha;
            setH3OColor( _h3oColor );
            setOHColor( _ohColor );
        }
    }
    
    public int getMajorityAlpha() { 
        return _majorityAlpha;
    }
    
    public void setMinorityAlpha( int alpha ) {
        if ( alpha != _minorityAlpha ) {
            _minorityAlpha = alpha;
            setH3OColor( _h3oColor );
            setOHColor( _ohColor );
        }
    }
    
    public int getMinorityAlpha() {
        return _minorityAlpha;
    }
    
    public void setH3OColor( Color color ) {
        _h3oColor = color;
        int alpha = ( _numberOfH3O >= _numberOfOH ? _majorityAlpha : _minorityAlpha );
        Color particleColor = ColorUtils.createColor( _h3oColor, alpha );
        int count = _particlesParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = _particlesParent.getChild( i );
            if ( node instanceof H3ONode ) {
                node.setPaint( particleColor );
            }
        }
    }
    
    public Color getH3OColor() {
        return _h3oColor;
    }
    
    public void setOHColor( Color color ) {
        int alpha = ( _numberOfOH >= _numberOfH3O ? _majorityAlpha : _minorityAlpha );
        _ohColor = color;
        Color particleColor = ColorUtils.createColor( _ohColor, alpha );
        int count = _particlesParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = _particlesParent.getChild( i );
            if ( node instanceof OHNode ) {
                node.setPaint( particleColor );
            }
        }
    }
    
    public Color getOHColor() {
        return _ohColor;
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    public void setVisible( boolean visible ) {
        if ( visible != getVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
            else {
                _pH = null;
                _particlesParent.removeAllChildren();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        if ( getVisible() ) {
            Double previousPH = _pH;
            _pH = _liquid.getPH();
            if ( _pH == null ) {
                deleteAllParticles();
            }
            else if ( !_pH.equals( previousPH ) ) {
                createParticles();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Particle creation
    //----------------------------------------------------------------------------
    
    /*
     * Deletes all particles.
     */
    private void deleteAllParticles() {
        _particlesParent.removeAllChildren();
    }
    
    /*
     * Creates particle nodes based on the current pH value.
     */
    private void createParticles() {
        assert( _pH != null );
        
        deleteAllParticles();
        
        final double pH = _pH.doubleValue();

        if ( pH >= ACID_PH_THRESHOLD && pH <= BASE_PH_THRESHOLD ) {
            // # particles is varies with log in this range
            _numberOfH3O = (int) ( _liquid.getConcentrationH3O() * ( _neutralParticles / 2 ) / 1E-7 );
            _numberOfOH = (int) ( _liquid.getConcentrationOH() * ( _neutralParticles / 2 ) / 1E-7 );
        }
        else {
            // # particles becomes a linear relationship in this range
            final double N = ( _maxParticles - ( 10 * _neutralParticles / 2 ) ) / 7;
            if ( pH < ACID_PH_THRESHOLD ) {
                // strong acid
                _numberOfH3O = (int) Math.min( _maxParticles, ( Liquid.getConcentrationH3O( ACID_PH_THRESHOLD ) * ( _neutralParticles / 2 ) / 1E-7 ) + ( ( ACID_PH_THRESHOLD - pH ) * N ) );
                _numberOfOH = (int) Math.max( 1, ( Liquid.getConcentrationOH( ACID_PH_THRESHOLD ) * ( _neutralParticles / 2 ) / 1E-7 ) - ( ACID_PH_THRESHOLD - pH ) );
            }
            else {
                // strong base
                _numberOfH3O = (int) Math.max( 1, ( Liquid.getConcentrationH3O( BASE_PH_THRESHOLD ) * ( _neutralParticles / 2 ) / 1E-7 ) - ( pH - BASE_PH_THRESHOLD ) );
                _numberOfOH = (int) Math.min( _maxParticles, (Liquid.getConcentrationOH( BASE_PH_THRESHOLD ) * ( _neutralParticles / 2 ) / 1E-7 ) + ( ( pH - BASE_PH_THRESHOLD ) * N ) );
            }
        }
        System.out.println( "#H3O=" + _numberOfH3O + " #OH=" + _numberOfOH );
        
        // create particles, minority species in foreground
        if ( _numberOfH3O > _numberOfOH ) {
            createH3ONodes( (int) _numberOfH3O, _majorityAlpha );
            createOHNodes( (int) _numberOfOH, _minorityAlpha );
        }
        else {
            createOHNodes( (int) _numberOfOH, _majorityAlpha );
            createH3ONodes( (int) _numberOfH3O, _minorityAlpha );
        }
    }
    
    /*
     * Creates H3O nodes at random locations.
     */
    private void createH3ONodes( int count, int alpha ) {
        Point2D pOffset = new Point2D.Double();
        Color color = ColorUtils.createColor( _h3oColor, alpha );
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( _containerBounds, pOffset );
            ParticleNode p = new H3ONode( _diameter, color );
            p.setOffset( pOffset );
            _particlesParent.addChild( p );
        }
    }

    /*
     * Creates OH nodes at random locations.
     */
    private void createOHNodes( int count, int alpha ) {
        Point2D pOffset = new Point2D.Double();
        Color color = ColorUtils.createColor( _ohColor, alpha );
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( _containerBounds, pOffset );
            ParticleNode p = new OHNode( _diameter, color );
            p.setOffset( pOffset );
            _particlesParent.addChild( p );
        }
    }

    /*
     * Gets a random point inside some bounds.
     */
    private void getRandomPoint( PBounds bounds, Point2D pOutput ) {
        double x = bounds.getX() + ( _randomX.nextDouble() * bounds.getWidth() );
        double y = bounds.getY() + ( _randomY.nextDouble() * bounds.getHeight() );
        pOutput.setLocation( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /* Base class for all particle nodes */
    private static abstract class ParticleNode extends PPath {

        private Ellipse2D _ellipse;

        public ParticleNode( double diameter, Color color ) {
            super();
            _ellipse = new Ellipse2D.Double();
            setPaint( color );
            setStroke( null );
            setDiameter( diameter );
        }

        public void setDiameter( double diameter ) {
            _ellipse.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
            setPathTo( _ellipse );
        }
    }

    /* H30 particle node, marker class */
    private static class H3ONode extends ParticleNode {
        public H3ONode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

    /* OH particle node, marker class */
    private static class OHNode extends ParticleNode {
        public OHNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

}
