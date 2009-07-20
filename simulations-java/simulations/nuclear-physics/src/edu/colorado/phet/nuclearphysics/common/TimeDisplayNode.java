/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a digital time display that can display a very wide
 * range of time, from milliseconds to billions of years.  Note that this does
 * note display time like a clock does (e.g. 12:43 PM) but as a length of time
 * (e.g. 427 ms).
 * 
 * TODO: This class was leveraged from another class that displays time at all
 * sorts of different resolutions.  However, there has been talk about making
 * this one ONLY display years.  I am reluctant to fix it to years just yet,
 * since someone may decide differently, so there is a lot of commented out
 * code at the moment.  Once the behavior is firmed up, this should be cleaned
 * up.  If it is decided that several time scales are needed, it may be
 * worthwhile to generalize this and use the same one here and for the graph
 * from which it was initially extracted.  jblanco, July 20, 2009.
 * Also, when cleaning up based in this, if there is no need for an exponential
 * (and maybe even if there is), it seems like it would make sense to
 * consolidate all the display terms into one HTML node.
 * 
 * @author John Blanco
 */
public class TimeDisplayNode extends PNode {
	
	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private final Font  TIME_FONT = new PhetFont( Font.BOLD, 26 );
    private final float BORDER_STROKE_SIZE = 3;
    private final double MILLISECONDS_PER_SECOND = 1000;
    private final double MILLISECONDS_PER_MINUTE = 60 * MILLISECONDS_PER_SECOND;
    private final double MILLISECONDS_PER_HOUR = 60 * MILLISECONDS_PER_MINUTE;
    private final double MILLISECONDS_PER_DAY = 24 * MILLISECONDS_PER_HOUR;
    private final double MILLISECONDS_PER_YEAR = 365 * MILLISECONDS_PER_DAY;
    private final double MILLISECONDS_PER_MILLENIUM = 1000 * MILLISECONDS_PER_YEAR;
    private final double MILLISECONDS_PER_MILLION_YEARS = 1E6 * MILLISECONDS_PER_YEAR;
    private final double MILLISECONDS_PER_BILLION_YEARS = 1E9 * MILLISECONDS_PER_YEAR;
    private final double MILLISECONDS_PER_TRILLION_YEARS = 1E12 * MILLISECONDS_PER_YEAR;
    private final double MILLISECONDS_PER_QUADRILLION_YEARS = 1E15 * MILLISECONDS_PER_YEAR;
    private final int EXPONENT_SCALE = 80; // percent
    private final int NUM_SIGNIFICANT_DIGITS = 3;
    
    private double _width;
    private double _height;
	private PPath _background;
	private RoundRectangle2D _backgroundShape;
	private double _currentTimeInMilliseconds = Double.NEGATIVE_INFINITY;
	private HTMLNode _timeText;
	private HTMLNode _unitsText;
	private PText _spaceText;
	private HTMLNode _dummyTextNormal;       // Used for scaling.
	private HTMLNode _dummyTextExponential;  // Used for scaling.
	private NumberFormat _timeFormatterWithCommas = NumberFormat.getNumberInstance(PhetResources.readLocale());
    private DecimalFormat _timeFormatterNoDecimals = new DefaultDecimalFormat( "##0" );
    private DecimalFormat _timeFormatterOneDecimal = new DecimalFormat( "##0.0" );
    private DecimalFormat _timeFormatterTwoDecimals = new DecimalFormat( "##0.00" );
    private ConstantPowerOfTenNumberFormat _thousandsFormatter = new ConstantPowerOfTenNumberFormat("0", 3, EXPONENT_SCALE);
    private ConstantPowerOfTenNumberFormat _millionsFormatter = new ConstantPowerOfTenNumberFormat("0", 6, EXPONENT_SCALE);
    private ConstantPowerOfTenNumberFormat _billionsFormatter = new ConstantPowerOfTenNumberFormat("0", 9, EXPONENT_SCALE);
    private ConstantPowerOfTenNumberFormat _trillionsFormatter = new ConstantPowerOfTenNumberFormat("0", 12, EXPONENT_SCALE);
	
    /**
     * Constructor.
     * 
     * @param width
     * @param height
     */
	public TimeDisplayNode(double width, double height){
		_width = width;
		_height = height;
		_backgroundShape = new RoundRectangle2D.Double(0, 0, _width, _height, _width / 10, _width / 10);
		_background = new PPath(_backgroundShape);
		_background.setPaint(BACKGROUND_COLOR);
		_background.setStroke(new BasicStroke(BORDER_STROKE_SIZE));
		addChild(_background);
		_timeText = new HTMLNode();
		_timeText.setFont(TIME_FONT);
		addChild(_timeText);
		_spaceText = new PText(" ");
		_spaceText.setFont(TIME_FONT);
		addChild(_spaceText);
		_unitsText = new HTMLNode();
		_unitsText.setFont(TIME_FONT);
		addChild(_unitsText);
		_dummyTextNormal = new HTMLNode(_timeFormatterNoDecimals.format(9999));
		_dummyTextNormal.setFont(TIME_FONT);
		_dummyTextExponential = new HTMLNode(_trillionsFormatter.format(999E12));
		_dummyTextExponential.setFont(TIME_FONT);
	}
	
	public void setSize(double width, double height){
		_width = width;
		_height = height;
		_backgroundShape.setFrame( 0, 0, _width, _height );
		_background.setPathTo(_backgroundShape);
		
		updateTextScaling();
		updateTimeDisplay();
	}
	
	private void updateTextScaling(){
		
		double maxTextHeight = _height * 0.9;
		double maxTextWidth = _width * 0.9;
		double scalingFactor = 1;
		double unscaledWidth;
		double unscaledHeight;

//		if (_currentTimeInMilliseconds > MILLISECONDS_PER_MILLENIUM){
//    		unscaledWidth = _dummyTextExponential.getFullBoundsReference().width + _unitsText.getFullBoundsReference().width;
//    		unscaledHeight = _dummyTextExponential.getFullBoundsReference().height;
//		}
//		else{
//    		unscaledWidth = _dummyTextNormal.getFullBoundsReference().width + _unitsText.getFullBoundsReference().width;
//    		unscaledHeight = _dummyTextNormal.getFullBoundsReference().height;
//		}
		
		_dummyTextNormal.setHTML( _timeText.getHTML() + _spaceText.getText() + _unitsText.getHTML() );
		
		unscaledWidth = _dummyTextNormal.getFullBoundsReference().width;
		unscaledHeight = _dummyTextNormal.getFullBoundsReference().height;
		
		if (unscaledWidth > maxTextWidth){
			// Scaling is required for this to fit.
			scalingFactor = maxTextWidth / unscaledWidth;
		}
		if (unscaledHeight > maxTextHeight){
			// Scaling is required for this to fit.
			if (maxTextHeight / unscaledHeight < scalingFactor){
				scalingFactor = maxTextHeight / unscaledHeight;
			}
			scalingFactor = maxTextWidth / unscaledWidth;
		}
		
		// Compare the calculated scaling factor with the desired scaling
		// factor, within a degree of accuracy, and set the new scale if it
		// is different.
		if ((int)(scalingFactor * 100) != (int)(_timeText.getScale() * 100)){
			_timeText.setScale(1);
			_spaceText.setScale(1);
			_unitsText.setScale(1);
//			_dummyTextExponential.setScale(1);
//			_dummyTextNormal.setScale(1);
			
    		_timeText.setScale(scalingFactor);
    		_spaceText.setScale(scalingFactor);
    		_unitsText.setScale(scalingFactor);
//    		_dummyTextNormal.setScale(scalingFactor);
//    		_dummyTextExponential.setScale(scalingFactor);
		}
	}

	// TODO: A request was made to always show the time in years.  If this
	// sticks, the code below will no longer be needed and can be
	// permanently removed.  If this is done, the name of the class should
	// also be changed to something like "YearDisplayNode".  jblanco, July 2009.
//	public void setTime(double milliseconds){
//
//		_currentTimeInMilliseconds = milliseconds;
//		
//		if (milliseconds < MILLISECONDS_PER_SECOND){
//			// Milliseconds range.
//            _timeText.setHTML( _timeFormatterNoDecimals.format(milliseconds) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_MILLISECONDS );
//		}
//		else if (milliseconds < MILLISECONDS_PER_MINUTE){
//			// Seconds range.
//            _timeText.setHTML( _timeFormatterOneDecimal.format(milliseconds / MILLISECONDS_PER_SECOND) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_SECONDS );
//		}
//		else if (milliseconds < MILLISECONDS_PER_HOUR){
//			// Minutes range.
//            _timeText.setHTML( _timeFormatterOneDecimal.format(milliseconds / MILLISECONDS_PER_MINUTE) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_MINUTES );
//		}
//		else if (milliseconds < MILLISECONDS_PER_DAY){
//			// Hours range.
//            _timeText.setHTML( _timeFormatterOneDecimal.format(milliseconds / MILLISECONDS_PER_HOUR) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_HOURS );
//		}
//		else if (milliseconds < MILLISECONDS_PER_YEAR){
//			// Days range.
//            _timeText.setHTML( _timeFormatterNoDecimals.format(milliseconds / MILLISECONDS_PER_DAY) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_DAYS );
//		}
//		else if (milliseconds < MILLISECONDS_PER_MILLENIUM){
//			// Years range.
//            _timeText.setHTML( _timeFormatterNoDecimals.format(milliseconds / MILLISECONDS_PER_YEAR) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YEARS );
//		}
//		else if (milliseconds < MILLISECONDS_PER_MILLION_YEARS){
//			// Thousand years range (millenia).
//            _timeText.setHTML( _thousandsFormatter.format(milliseconds / MILLISECONDS_PER_YEAR) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YRS );
//		}
//		else if (milliseconds < MILLISECONDS_PER_BILLION_YEARS){
//			// Million years range.
//            _timeText.setHTML( _millionsFormatter.format(milliseconds / MILLISECONDS_PER_YEAR) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YRS );
//		}
//		else if (milliseconds < MILLISECONDS_PER_TRILLION_YEARS){
//			// Billion years range.
//            _timeText.setHTML( _billionsFormatter.format(milliseconds / MILLISECONDS_PER_YEAR) );
//            _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YRS );
//		}
//		else if (milliseconds < MILLISECONDS_PER_QUADRILLION_YEARS){
//			// Trillion years range.
//            _timeText.setHTML( _trillionsFormatter.format(milliseconds / MILLISECONDS_PER_YEAR) );
//            _unitsText.setHTML(NuclearPhysicsStrings.READOUT_UNITS_YRS);
//		}
//		else {
//			_timeText.setHTML( "\u221e"); // Infinity.
//			_unitsText.setHTML("");
//		}
//		
//		updateTextScaling();
//        updateTimeDisplay();
//	}
	
	public void setTime(double milliseconds){
		
		if (milliseconds == _currentTimeInMilliseconds){
			// Don't bother updating the display if the value hasn't changed.
			return;
		}

		_currentTimeInMilliseconds = milliseconds;

		// Convert to years.
		double timeInYears = milliseconds / MILLISECONDS_PER_YEAR;
		double valueToDisplay = roundToSignificantDigits(timeInYears, NUM_SIGNIFICANT_DIGITS);
		if (valueToDisplay < 1 && valueToDisplay != 0){
			_timeText.setHTML(_timeFormatterTwoDecimals.format( valueToDisplay ));
		}
		else{
			_timeText.setHTML( _timeFormatterWithCommas.format(valueToDisplay) );
		}
        _unitsText.setHTML( NuclearPhysicsStrings.READOUT_UNITS_YRS );
		
		updateTextScaling();
        updateTimeDisplay();
	}

	
	/**
	 * Note: This only works for numbers to the left of the decimal place.
	 * 
	 * @param val
	 * @param numSigDigs
	 * @return
	 */
	public static double roundToSignificantDigits(double val, int numSigDigs){
		
		if (val == 0){
			// Can't do much with.
			return 0;
		}
		
		int places = (int)Math.floor(Math.log10(val)) + 1;
		
		// Calculate a value where the decimal point has the number of
		// desired significant digits to the left of the decimal point.
		double retVal = val / Math.pow(10, places - numSigDigs);
		
		// Round the value.
		retVal = (double)Math.round(retVal);
		
		// Multiply back to the original number of places.
		retVal = retVal * Math.pow(10, places - numSigDigs);
		
		// We're done.
		return retVal;
	}
	
	public static double round(double val, int places) {
		long factor = (long)Math.pow(10,places);

		// Shift the decimal the correct number of places
		// to the right.
		val = val * factor;

		// Round to the nearest integer.
		long tmp = Math.round(val);

		// Shift the decimal the correct number of places
		// back to the left.
		return (double)tmp / factor;
	}
	
	public static void main(String[] args) {
		System.out.println("Test of sig digs.");
		double testVal;
		testVal = 12345;
		System.out.println("Input: " + testVal + ", Output: " + roundToSignificantDigits(testVal, 2));
		testVal = 99.9;
		System.out.println("Input: " + testVal + ", Output: " + roundToSignificantDigits(testVal, 2));
		testVal = 0.002;
		System.out.println("Input: " + testVal + ", Output: " + roundToSignificantDigits(testVal, 3));
	}

	
	// TODO: This relates to the other todo above.  If this display ends up
	// only being used for years, this method can be permanently removed.
//	private void updateTimeDisplay(){
//		double xPos;
//		double yPos;
//		
//		if (_currentTimeInMilliseconds < MILLISECONDS_PER_MILLENIUM){
//    		xPos = _width / 2 - 
//    		        (_dummyTextNormal.getFullBoundsReference().width + _spaceText.getFullBoundsReference().width + 
//    	        	 _unitsText.getFullBoundsReference().width) / 2;
//    		xPos += _dummyTextNormal.getFullBoundsReference().width - _timeText.getFullBoundsReference().width;
//    		yPos = _height / 2 - _dummyTextNormal.getFullBoundsReference().height / 2;
//		}
//		else if (_unitsText.getHTML() != ""){
//    		xPos = _width / 2 - 
//       		        (_dummyTextExponential.getFullBoundsReference().width + _spaceText.getFullBoundsReference().width + 
//	        		 _unitsText.getFullBoundsReference().width) / 2;
//    		xPos += _dummyTextExponential.getFullBoundsReference().width - _timeText.getFullBoundsReference().width;
//    		yPos = _height / 2 - _dummyTextExponential.getFullBoundsReference().height / 2;
//		}
//		else{
//			// This is probably the case where the infinity symbol is being displayed.
//    		xPos = _width / 2 - _timeText.getFullBoundsReference().width / 2;
//    		yPos = _height / 2 - _timeText.getFullBoundsReference().height / 2;
//		}
//		
//		_timeText.setOffset(xPos, yPos);
//		_spaceText.setOffset(xPos + _timeText.getFullBoundsReference().width, yPos);
//		_unitsText.setOffset(xPos + _timeText.getFullBoundsReference().width + _spaceText.getFullBoundsReference().width, 
//				_timeText.getFullBoundsReference().getMaxY() - _unitsText.getFullBoundsReference().height);
//	}
	private void updateTimeDisplay(){
		double xPos;
		double yPos;
		double totalTextWidth;
		double textHeight;
		
		totalTextWidth = _timeText.getFullBoundsReference().width + _spaceText.getFullBoundsReference().width +
			_unitsText.getFullBoundsReference().width;
		textHeight = Math.max(_timeText.getFullBoundsReference().height, _unitsText.getFullBoundsReference().height);
		
		xPos = _width / 2 - totalTextWidth / 2;
		yPos = _height / 2 - textHeight / 2;
		
		_timeText.setOffset(xPos, yPos);
		_spaceText.setOffset(xPos + _timeText.getFullBoundsReference().width, yPos);
		_unitsText.setOffset(xPos + _timeText.getFullBoundsReference().width + _spaceText.getFullBoundsReference().width, 
				_timeText.getFullBoundsReference().getMaxY() - _unitsText.getFullBoundsReference().height);
	}
}
