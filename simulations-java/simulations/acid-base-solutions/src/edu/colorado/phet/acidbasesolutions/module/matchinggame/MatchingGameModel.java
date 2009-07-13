/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Acid.CustomAcid;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.module.ABSModel;
import edu.colorado.phet.acidbasesolutions.util.PrecisionDecimal;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * MatchingGameModel is the model for MatchingGameModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameModel extends ABSModel {
    
    private static final boolean DEBUG_MATCH = false;
    
    // parameters that define what constitutes a match
    private static final double LOG10_CONCENTRATION_CLOSENESS = 0.12; // difference between log10 of concentration values
    private static final double LOG10_STRENGTH_CLOSENESS = 0.35; // difference between log10 of strength values
    
    // points related to "Acid or Base?" question
    private static final int POINTS_ACIDBASE_CORRECT_FIRST = +1;
    private static final int POINTS_ACIDBASE_CORRECT_RETRY = +1;
    private static final int POINTS_ACIDBASE_WRONG_FIRST = -1;
    private static final int POINTS_ACIDBASE_WRONG_RETRY = -1;
    private static final int POINTS_ACID_BASE_GIVEUP = 0;

    // points related to "Match the solution" challenge
    private static final int POINTS_MATCH_CORRECT_FIRST = +5;
    private static final int POINTS_MATCH_CORRECT_RETRY = +5;
    private static final int POINTS_MATCH_WRONG_FIRST = -1;
    private static final int POINTS_MATCH_WRONG_RETRY = -1;
    private static final int POINTS_MATCH_GIVEUP = 0;

    private int numberOfSolutions;
    private int points;
    private final AqueousSolution solutionLeft, solutionRight;
    private final ArrayList<MatchingGameModelListener> listeners;
    private boolean acidBaseGuessed, matchGuessed;
    private int deltaPoints; // most recent points delta
    private final PrecisionDecimal concentrationValue;

    public MatchingGameModel( ABSClock clock ) {
        super( clock );
        numberOfSolutions = 0;
        points = 0;
        deltaPoints = 0;
        solutionLeft = new AqueousSolution();
        solutionRight = new AqueousSolution();  
        listeners = new ArrayList<MatchingGameModelListener>();
        concentrationValue = new PrecisionDecimal( ABSConstants.CONCENTRATION_RANGE.getMin(), ABSConstants.CONCENTRATION_DECIMAL_PLACES );
        reset();
    }
    
    public void reset() {
        acidBaseGuessed = matchGuessed = true; // so this isn't interpretted as "give up"
        setPoints( 0 );
        setNumberOfSolutions( 0 );
        newSolution();
    }

    public AqueousSolution getSolutionLeft() {
        return solutionLeft;
    }

    public AqueousSolution getSolutionRight() {
        return solutionRight;
    }
    
    private void setNumberOfSolutions( int numberOfSolutions ) {
        if ( numberOfSolutions != this.numberOfSolutions ) {
            this.numberOfSolutions = numberOfSolutions;
            notifyNumberOfSolutionsChanged( numberOfSolutions );
        }
    }
    
    public int getNumberOfSolutions() {
        return numberOfSolutions;
    }
    
    private void setPoints( int points ) {
        this.deltaPoints = points - this.points;
        if ( points != this.points ) {
            this.points = points;
            notifyPointsChanged( points );
        }
    }
    
    private void changePoints( int delta ) {
        setPoints( getPoints() + delta );
    }
    
    public int getPoints() {
        return points;
    }
    
    public int getDeltaPoints() {
        return deltaPoints;
    }
    
    /**
     * Generates the next solution to be matched.
     * The left and right solutions will be the same type (either Custom Acid or Custom Base),
     * but their concentrations and strengths initially will be different.
     */
    public void newSolution() {
        
        // deduct points for giving up
        int delta = 0;
        if ( !acidBaseGuessed ) {
            delta = POINTS_ACID_BASE_GIVEUP;
        }
        else if ( !matchGuessed ) {
            delta = POINTS_MATCH_GIVEUP;
        }
        acidBaseGuessed = matchGuessed = false;
        changePoints( delta );
        
        // generate new solutions
        Solute soluteLeft = null;
        Solute soluteRight = null;
        if ( getRandomAcid() ) {
            soluteLeft = new CustomAcid( getRandomStrength() );
            soluteRight = new CustomAcid( getRandomStrength() );
        }
        else {
            soluteLeft = new CustomBase( getRandomStrength() );
            soluteRight = new CustomBase( getRandomStrength() );
        }
        soluteLeft.setConcentration( getRandomConcentration() );
        soluteRight.setConcentration( getRandomConcentration() );
        solutionLeft.setSolute( soluteLeft );
        solutionRight.setSolute( soluteRight );
        setNumberOfSolutions( numberOfSolutions + 1 );
        notifyNewSolution();
    }
    
    /**
     * Checks if the solution on the left is an acid, assesses points.
     * @return
     */
    public boolean checkAcid() {
        return checkAcidBase( solutionLeft.isAcidic() );
    }
    
    /**
     * Checks if the solution on the left is a base, assesses points.
     * @return
     */
    public boolean checkBase() {
        return checkAcidBase( solutionLeft.isBasic() );
    }
    
    /**
     * Handles points for the acid/base question.
     * <p>
     * If this is our first guess and we're correct, we get points.
     * If it's not our first guess and we're correct, we get no points.
     * If the guess is wrong, we lose points.
     * @return
     */
    private boolean checkAcidBase( final boolean success ) {
        int delta = 0;
        if ( success ) {
            delta = ( acidBaseGuessed ? POINTS_ACIDBASE_CORRECT_RETRY : POINTS_ACIDBASE_CORRECT_FIRST );
        }
        else {
            delta = ( acidBaseGuessed ? POINTS_ACIDBASE_WRONG_RETRY: POINTS_ACIDBASE_WRONG_FIRST );
        }
        acidBaseGuessed = true;
        changePoints( delta );
        return success;
    }
    
    /**
     * Check whether the left and right solutions match.
     * Solutions match is their types are the same, and the right solution (the guess)
     * has concentration and strength that is "sufficiently close" to the left solution
     * (the randomly generated solution.)
     * <p>
     * If this is our first guess and we're correct, we get points.
     * If it's not our first guess and we're correct, we get no points.
     * If the guess is wrong, we lose points.
     * 
     * @return true or false
     */
    public boolean checkMatch() {
        boolean match = ( solutesMatch() && concentrationsMatch() && strengthsMatch() );
        int delta = 0;
        if ( match ) { 
            delta = ( matchGuessed ? POINTS_MATCH_CORRECT_RETRY : POINTS_MATCH_CORRECT_FIRST );
        }
        else {
            delta = ( matchGuessed ? POINTS_MATCH_WRONG_RETRY : POINTS_MATCH_WRONG_FIRST );
        }
        matchGuessed = true;
        changePoints( delta );
        return match;
    }
    
    /*
     * Solutes match is their types are the same.
     */
    private boolean solutesMatch() {
        return solutionLeft.getSolute().getClass().equals( solutionRight.getSolute().getClass() );
    }
    
    /*
     * Concentrations match if the right solution (the guess) is within 
     * CONCENTRATION_CLOSENESS of the left (random) solution.
     * Match is based on log10, since the concentration control is log.
     * If we did this based on linear closeness, matching would get more 
     * difficult as values got smaller.
     */
    private boolean concentrationsMatch() {
        final double left = MathUtil.log10( solutionLeft.getSolute().getConcentration() );
        final double right = MathUtil.log10( solutionRight.getSolute().getConcentration() );
        final double closeness = Math.abs( left - right );
        final boolean success = ( closeness <= LOG10_CONCENTRATION_CLOSENESS );
        if ( DEBUG_MATCH ) {
            System.out.println( "MatchingGameModel.concentrationMatch left=" + left + " right=" + right + " closeness=" + closeness + " threshold=" + LOG10_CONCENTRATION_CLOSENESS + " success=" + success );
        }
        return success;
    }
    
    /*
     * Strengths match if the right solution (the guess) is within 
     * STRENGTH_CLOSENESS of the left (random) solution.
     * If both solutions are strong, then it doesn't matter what the value is, they match.
     * Match is based on log10, since the concentration control is log.
     * If we did this based on linear closeness, matching would get more 
     * difficult as values got smaller.
     */
    private boolean strengthsMatch() {
        boolean success = false;
        if ( solutionLeft.getSolute().isStrong() && solutionRight.getSolute().isStrong() ) {
            if ( DEBUG_MATCH ) {
                System.out.println( "MatchingGameModel.strengthMatch: strong, success=true" );
            }
            success = true;
        }
        else {
            final double left = MathUtil.log10( solutionLeft.getSolute().getStrength() );
            final double right = MathUtil.log10( solutionRight.getSolute().getStrength() );
            final double closeness = Math.abs( left - right );
            success = ( closeness <= LOG10_STRENGTH_CLOSENESS );
            if ( DEBUG_MATCH ) {
                System.out.println( "MatchingGameModel.strengthMatch: left=" + left + " right=" + right + " closeness=" + closeness + " threshold=" + LOG10_STRENGTH_CLOSENESS + " success=" + success );
            }
        }
        return success;
    }
    
    private boolean getRandomAcid() {
        return ( Math.random() > 0.5 );
    }
    
    private double getRandomConcentration() {
        // limit the precision of the concentration so that we don't have issues with precision of controls
        concentrationValue.setValue( getRandomLog10( ABSConstants.CONCENTRATION_RANGE ) );
        return concentrationValue.getValue();
    }
    
    private double getRandomStrength() {
        return getRandomLog10( ABSConstants.CUSTOM_STRENGTH_RANGE );
    }
    
    /* 
     * Randomly picks the exponent for a value in a log range.
     */
    private static double getRandomLog10( DoubleRange range ) {
        double d = Math.random();
        double minExponent = MathUtil.log10( range.getMin() );
        double maxExponent = MathUtil.log10( range.getMax() );
        double exponent = minExponent + ( d * ( maxExponent - minExponent ) );
        double value = range.getMin() + Math.pow( 10, exponent );
        if ( !range.contains( value ) ) {
            value = range.getMin();
        }
        return value;
    }
    
    public interface MatchingGameModelListener {
        public void solutionChanged();
        public void pointsChanged( int points );
        public void numberOfSolutionsChanged( int numberOfSolutions );
    }
    
    public static class MatchingGameModelAdapter implements MatchingGameModelListener {
        public void solutionChanged() {}
        public void pointsChanged( int points ) {}
        public void numberOfSolutionsChanged( int numberOfSolutions ) {}
    }
    
    public void addMatchingGameModelListener( MatchingGameModelListener listener ) {
        listeners.add( listener );
    }
    
    public void removeMatchingGameModelListener( MatchingGameModelListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyNewSolution() {
        Iterator<MatchingGameModelListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().solutionChanged();
        }
    }
    
    private void notifyPointsChanged( int points ) {
        Iterator<MatchingGameModelListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().pointsChanged( points );
        }
    }
    
    private void notifyNumberOfSolutionsChanged( int numberOfSolutions ) {
        Iterator<MatchingGameModelListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().numberOfSolutionsChanged( numberOfSolutions );
        }
    }
}
