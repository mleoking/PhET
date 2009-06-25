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
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * MatchingGameModel is the model for MatchingGameModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameModel extends ABSModel {
    
    // parameters that define what constitutes a match
    private static final double CONCENTRATION_CLOSENESS = 0.15; // percent, 0-1
    private static final double STRENGTH_CLOSENESS = 0.15; // percent, 0-1
    
    // points related to "Acid or Base?" question
    private static final int POINTS_ACIDBASE_CORRECT_FIRST = +1;
    private static final int POINTS_ACIDBASE_CORRECT_RETRY = 0;
    private static final int POINTS_ACIDBASE_WRONG_FIRST = -1;
    private static final int POINTS_ACIDBASE_WRONG_RETRY = -1;
    private static final int POINTS_ACID_BASE_GIVEUP = 0;

    // points related to "Match the solution" challenge
    private static final int POINTS_MATCH_CORRECT_FIRST = +5;
    private static final int POINTS_MATCH_CORRECT_RETRY = 0;
    private static final int POINTS_MATCH_WRONG_FIRST = -1;
    private static final int POINTS_MATCH_WRONG_RETRY = -1;
    private static final int POINTS_MATCH_GIVEUP = 0;

    private int numberOfSolutions;
    private int points;
    private final AqueousSolution solutionLeft, solutionRight;
    private final ArrayList<MatchingGameModelListener> listeners;
    private boolean acidBaseGuessed, matchGuessed;

    public MatchingGameModel( ABSClock clock ) {
        super( clock );
        numberOfSolutions = 0;
        points = 0;
        solutionLeft = new AqueousSolution();
        solutionRight = new AqueousSolution();  
        listeners = new ArrayList<MatchingGameModelListener>();
        acidBaseGuessed = matchGuessed = false;
        reset();
    }
    
    public void reset() {
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
        if ( points != this.points ) {
            this.points = points;
            notifyPointsChanged( points );
        }
    }
    
    private void changePoints( int delta ) {
        if ( delta != 0 ) {
            setPoints( getPoints() + delta );
        }
    }
    
    public int getPoints() {
        return points;
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
        notifyNewSolution();
    }
    
    /**
     * Checks if the solution on the left is an acid.
     * <p>
     * If this is our first guess and we're correct, we get points.
     * If it's not our first guess and we're correct, we get no points.
     * If the guess is wrong, we lose points.
     * @return
     */
    public boolean checkAcid() {
        boolean success = solutionLeft.isAcidic();
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
     * Checks it the solution on the left is a base.
     * See checkAcid.
     * @return
     */
    public boolean checkBase() {
        return !checkAcid();
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
     * Concentrations match is the right solution (the guess) is within 
     * CONCENTRATION_CLOSENESS percent of the left (random) solution.
     */
    private boolean concentrationsMatch() {
        final double leftC = solutionLeft.getSolute().getConcentration();
        final double rightC = solutionRight.getSolute().getConcentration();
        return ( Math.abs( leftC - rightC ) / leftC ) <= CONCENTRATION_CLOSENESS;
    }
    
    /*
     * Strength match is the right solution (the guess) is within 
     * STRENGTH_CLOSENESS percent of the left (random) solution.
     */
    private boolean strengthsMatch() {
        final double leftK = solutionLeft.getSolute().getStrength();
        final double rightK = solutionRight.getSolute().getStrength();
        return ( Math.abs( leftK - rightK ) / leftK ) <= STRENGTH_CLOSENESS;
    }
    
    private boolean getRandomAcid() {
        return ( Math.random() > 0.5 );
    }
    
    private double getRandomConcentration() {
        return getRandom( ABSConstants.CONCENTRATION_RANGE );
    }
    
    private double getRandomStrength() {
        return getRandom( ABSConstants.CUSTOM_STRENGTH_RANGE );
    }
    
    private static double getRandom( DoubleRange range ) {
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
