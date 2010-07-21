/* Copyright 2004-2010, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * PickupCoil is the model of a pickup coil.
 * Its behavior follows Faraday's Law for electromagnetic induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PickupCoil extends AbstractCoil implements ModelElement, SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_CALIBRATION = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    
    private double _averageBx; // in Gauss
    private double _flux; // in webers
    private double _deltaFlux; // in webers
    private double _emf; // in volts
    private double _biggestAbsEmf; // in volts
    private Point2D _samplePoints[]; // B-field sample points
    private SamplePointsStrategy _samplePointsStrategy;
    private double _transitionSmoothingScale;
    private double _calibrationEmf;
    private String _moduleName; // for debug output
    
    // Reusable objects
    private AffineTransform _someTransform;
    private Point2D _samplePoint;
    private Vector2D _sampleVector;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a PickupCoil that uses a fixed number of sample points
     * to measure the magnet's B-field.
     * 
     * @param magnetModel
     * @param calibrationEmf
     */
    public PickupCoil( AbstractMagnet magnetModel, double calibrationEmf, String moduleName ) {
        this( magnetModel, calibrationEmf, moduleName, new ConstantNumberOfSamplePointsStrategy( 9 /* numberOfSamplePoints */ ) );
    }
    
    /*
     * Constructs a PickupCoil that uses a specified strategy for creating sample points
     * to measure the magnet's B-field.
     * 
     * @param magnetModel
     * @param calibrationEmf
     * @param samplePointsStrategy
     */
    private PickupCoil( AbstractMagnet magnetModel, double calibrationEmf, String moduleName, SamplePointsStrategy samplePointsStrategy ) {
        super();
        
        assert( magnetModel != null );
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        assert( calibrationEmf >= 1 );
        _calibrationEmf = calibrationEmf;
        
        _moduleName = moduleName;
        
        _samplePointsStrategy = samplePointsStrategy;
        _samplePoints = null;
        
        _averageBx = 0.0;
        _flux = 0.0;
        _deltaFlux = 0.0;
        _emf = 0.0;
        _biggestAbsEmf = 0.0;
        _transitionSmoothingScale = 1.0; // no smoothing
        
        // Reusable objects
        _someTransform = new AffineTransform();
        _samplePoint = new Point2D.Double();
        _sampleVector = new Vector2D();
        
        // loosely packed loops
        setLoopSpacing( 1.5 * getWireWidth() );
        
        updateSamplePoints();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the magnetic flux.
     * 
     * @return the flux, in Webers
     */
    public double getFlux() {
        return _flux;
    }
    
    /**
     * Gets the change in magnetic flux.
     * 
     * @return change in magnetic flux, in Webers
     */
    public double getDeltaFlux() {
        return _deltaFlux;
    }
    
    /**
     * Gets the average Bx of the pickup coil's sample points.
     * 
     * @return
     */
    public double getAverageBx() {
        return _averageBx;
    }
    
    /**
     * Gets the emf.
     * 
     * @return the emf
     */
    public double getEmf() {
        return _emf;
    }
    
    /**
     * Gets the biggest emf that the pickup coil has seen.
     * 
     * @return the biggest emf
     */
    public double getBiggestEmf() {
        return _biggestAbsEmf;
    }
    
    /**
     * Gets the sample points used to measure the B-field and calculate emf.
     * 
     * @return
     */
    public Point2D[] getSamplePoints() {
        return _samplePoints;
    }
    
    /**
     * When the coil's radius changes, update the sample points.
     */
    public void setRadius( double radius ) {
        super.setRadius( radius );
        updateSamplePoints();
    }
    
    /**
     * Sets the strategy used to compute sample points.
     * 
     * @param samplePointsStrategy
     */
    public void setSamplePointsStrategy( SamplePointsStrategy samplePointsStrategy ) {
        _samplePointsStrategy = samplePointsStrategy;
        updateSamplePoints();
    }
    
    /**
     * Sets a scaling factor used to smooth out abrupt changes that occur when
     * the magnet transitions between being inside & outside the coil.
     * <p> 
     * This is used to scale the B-field for sample points inside the magnet,
     * eliminating abrupt transitions at the left and right edges of the magnet. For any sample
     * point inside the magnet, the B field sample is multiplied by this value.
     * <p>
     * To set this value, follow these steps:
     * <ol>
     * <li>enable the developer controls for "pickup transition scale" and "display flux"
     * <li>move the magnet horizontally through the coil until, by moving it one pixel, 
     *     you see an abrupt change in the displayed flux value.
     * <li>note the 2 flux values when the abrupt change occurs
     * <li>move the magnet so that the larger of the 2 flux values is displayed
     * <li>adjust the developer control until the larger value is reduced
     *     to approximately the same value as the smaller value.
     * </ol>
     * 
     * @param scale 0 < scale <= 1
     */
    public void setTransitionSmoothingScale( double scale ) {
        if ( scale <= 0 || scale > 1 ) {
            throw new IllegalArgumentException( "scale must be > 0 and <= 1: " + scale );
        }
        _transitionSmoothingScale = scale;
        // no need to update, wait for next clock tick
    }
    
    /**
     * See setTransitionSmoothingScale.
     * 
     * @return
     */
    public double getTransitionSmoothingScale() {
        return _transitionSmoothingScale;
    }
    
    /**
     * Dividing the coil's emf by this number will give us the coil's current amplitude,
     * a number between 0 and 1 that determines the responsiveness of view components.
     * This number should be set as close as possible to the maximum emf that can be induced
     * given the range of all model parameters.
     * <p>
     * See PickupCoil.calibrateEmf for guidance on how to set this.
     * 
     * @param calibrationEmf
     */
    public void setCalibrationEmf( double calibrationEmf ) {
        if ( !( calibrationEmf >= 1 ) ) {
            throw new IllegalArgumentException( "calibrationEmf must be >= 1: " + calibrationEmf );
        }
        _calibrationEmf = calibrationEmf;
        // no need to update, wait for next clock tick
    }
    
    public double getCalibrationEmf() {
        return _calibrationEmf;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        // Do nothing, handled by stepInTime
    }
    
    //----------------------------------------------------------------------------
    // Sample points
    //----------------------------------------------------------------------------
    
    /*
     * Updates the sample points for the coil.
     * The samples points are used to measure the B-field in the calculation of emf.
     */
    private void updateSamplePoints() {
        _samplePoints = _samplePointsStrategy.createSamplePoints( this );
    }
    
    /**
     * Interface implemented by all strategies for 
     * creating B-field sample points for a pickup coil.
     */
    public interface SamplePointsStrategy {
        public Point2D[] createSamplePoints( PickupCoil pickupCoilModel );
    }
    
    /**
     * A fixed number of points is distributed
     * along a vertical line that goes through the center of a pickup coil.
     * The number of sample points must be odd, so that one point is at the center.
     * The points at the outer edge are guaranteed to be on the coil.
     */
    public static class ConstantNumberOfSamplePointsStrategy implements SamplePointsStrategy {
        
        private final int _numberOfSamplePoints;
        
        public ConstantNumberOfSamplePointsStrategy( final int numberOfSamplePoints ) {
            if ( numberOfSamplePoints < 1 || numberOfSamplePoints % 2 != 1 ) {
                throw new IllegalArgumentException( "numberOfSamplePoints must be > 0 and odd" );
            }
            _numberOfSamplePoints = numberOfSamplePoints;
        }

        public Point2D[] createSamplePoints( PickupCoil pickupCoilModel ) {
            
            Point2D[] samplePoints = new Point2D[_numberOfSamplePoints];
            final double numberOfSamplePointsOnRadius = ( _numberOfSamplePoints - 1 ) / 2;
            final double samplePointsYSpacing = pickupCoilModel.getRadius() / numberOfSamplePointsOnRadius;

            // all sample points share the same x offset
            final double xOffset = 0;

            // Center
            int index = 0;
            samplePoints[index++] = new Point2D.Double( xOffset, 0 );

            // Offsets below & above the center
            double y = 0;
            for ( int i = 0; i < numberOfSamplePointsOnRadius; i++ ) {
                y += samplePointsYSpacing;
                samplePoints[index++] = new Point2D.Double( xOffset, y );
                samplePoints[index++] = new Point2D.Double( xOffset, -y );
            }
            
            return samplePoints;
        }
    }
    
    /**
     * A fixed spacing is used to distribute a variable number of points
     * along a vertical line that goes through the center of a pickup coil.
     * One point is at the center. Points will be on the edge of the coil
     * only if the coil's radius is an integer multiple of the spacing.
     */
    public static class VariableNumberOfSamplePointsStrategy implements SamplePointsStrategy {
        
        private final double _ySpacing;
        
        public VariableNumberOfSamplePointsStrategy( final double ySpacing ) {
            if ( ySpacing <= 0 ) {
                throw new IllegalArgumentException( "ySpacing must be >= 0" );
            }
            _ySpacing = ySpacing;
        }
        
        public Point2D[] createSamplePoints( PickupCoil pickupCoilModel ) {
            
            final int numberOfSamplePointsOnRadius = (int)( pickupCoilModel.getRadius() / _ySpacing );
            
            Point2D[] samplePoints = new Point2D[ 1 + ( 2 * numberOfSamplePointsOnRadius ) ];

            // all sample points share the same x offset
            final double xOffset = 0;

            // Center
            int index = 0;
            samplePoints[index++] = new Point2D.Double( xOffset, 0 );

            // Offsets below & above the center
            double y = 0;
            for ( int i = 0; i < numberOfSamplePointsOnRadius; i++ ) {
                y += _ySpacing;
                samplePoints[index++] = new Point2D.Double( xOffset, y );
                samplePoints[index++] = new Point2D.Double( xOffset, -y );
            }
            
            return samplePoints;
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Handles ticks of the simulation clock.
     * Calculates the induced emf using Faraday's Law.
     * 
     * @param dt time delta
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {
            updateEmf( dt );
        }
    }
    
    /**
     * Updates the induced emf (and other related instance data), using Faraday's Law.
     */
    private void updateEmf( double dt ) {
        
        // Sum the B-field sample points.
        double sumBx = getSumBx();
        
        // Average the B-field sample points.
        _averageBx = sumBx / _samplePoints.length;
        
        // Flux in one loop.
        double A = getEffectiveLoopArea();
        double loopFlux = A * _averageBx; 
        
        // Flux in the coil.
        double flux = getNumberOfLoops() * loopFlux;
        
        // Change in flux.
        _deltaFlux = flux - _flux;
        _flux = flux;
        
        // Induced emf.
        double emf = -( _deltaFlux / dt );
        
        // If the emf has changed, set the current in the coil and notify observers.
        if ( emf != _emf ) {
            _emf = emf;
            
            // Current amplitude is proportional to emf amplitude.
            double amplitude = MathUtil.clamp( -1,  emf / _calibrationEmf, +1 );
            setCurrentAmplitude( amplitude ); // calls notifyObservers
        }
        
        calibrateEmf();
    }
    
    /*
     * Provides assistance for calibrating this coil.
     * The easiest way to calibrate is to run the sim in developer mode, 
     * then follow these steps for each module that has a pickup coil.
     * 
     * 1. Set the "Pickup calibration EMF" developer control to its smallest value.
     * 2. Set the model parameters to their maximums, so that maximum emf will be generated.
     * 3. Do whatever is required to generate emf (move magnet through coil, run generator, etc.)
     * 4. Watch System.out for a message that tells you what value to use.
     * 5. Change the value of the module's CALIBRATION_EMF constant.
     */
    private void calibrateEmf() {
        
        double absEmf = Math.abs( _emf );
        
        /*
         * Keeps track of the biggest emf seen by the pickup coil.
         * This is useful for determining the desired value of calibrationEmf.
         * Set DEBUG_CALIBRATION=true, run the sim, set model controls to 
         * their max values, then observe this debug output.  The largest
         * value that you see is what you should use for calibrationEmf.
         */
        if ( absEmf > _biggestAbsEmf ) {
            _biggestAbsEmf = _emf;
            if ( DEBUG_CALIBRATION ) {
                System.out.println( "PickupCoil.updateEmf: biggestEmf=" + _biggestAbsEmf );
            }
            
            /*
             * If this prints, you have calibrationEmf set too low.
             * This will cause view components to exhibit responses that are less then their maximums.
             * For example, the voltmeter won't fully deflect, and the lightbulb won't fully light.
             */
            if ( _biggestAbsEmf > _calibrationEmf && PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                System.out.println( "PickupCoil.updateEmf: you should recalibrate module \"" + _moduleName + "\" with CALIBRATION_EMF=" + _biggestAbsEmf );
            }
        }
        
        /*
         * TODO The coil could theoretically be self-calibrating. If we notice that we've exceeded calibrationEmf,
         * then adjust calibrationEmf.  This would be OK as long as we started with a value that was in the ballpark,
         * because we don't want the user to perceive a noticeable change in the sim's behavior.
         */
    }
    
    /*
     * Gets the sum of Bx at the coil's sample points.
     */
    private double getSumBx() {
        
        final double magnetStrength = _magnetModel.getStrength();
        
        // Sum the B-field sample points.
        double sumBx = 0;
        for ( int i = 0; i < _samplePoints.length; i++ ) {
            
            _samplePoint.setLocation( getX() + _samplePoints[i].getX(), getY() + _samplePoints[i].getY() );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _someTransform.setToIdentity();
                _someTransform.rotate( getDirection(), getX(), getY() );
                _someTransform.transform( _samplePoint, _samplePoint /* output */);
            }
            
            // Find the B-field vector at that point.
            _magnetModel.getBField( _samplePoint, _sampleVector /* output */  );
            
            /*
             * If the B-field x component is equal to the magnet strength, then our B-field sample
             * was inside the magnet. Use the fudge factor to scale the sample so that the transitions
             * between inside and outside are not abrupt. See Unfuddle #248.
             */ 
            double Bx = _sampleVector.getX();
            if ( Math.abs( Bx ) == magnetStrength ) {
                Bx *= _transitionSmoothingScale;
            }
            
            // Accumulate a sum of the sample points.
            sumBx += Bx;
        }
        
        return sumBx;
    }
    
    /*
     * See Unfuddle #721.
     * When the magnet is in the center of the coil, increasing the loop size should 
     * decrease the EMF.  But since we are averaging sample points on a vertical line,
     * multiplying by the actual area would (incorrectly) result in an EMF increase.
     * The best solution would be to take sample points across the entire coil,
     * but that requires many changes, so Mike Dubson came up with this workaround.  
     * By fudging the area using a thin vertical rectangle, the results are qualitatively 
     * (but not quantitatively) correct.
     * 
     * NOTE: This fix required recalibration of all the scaling factors accessible via developer controls.
     */
    private double getEffectiveLoopArea() {
        double width = FaradayConstants.MIN_PICKUP_LOOP_RADIUS;
        double height = 2 * getRadius();
        return width * height;
    }
}