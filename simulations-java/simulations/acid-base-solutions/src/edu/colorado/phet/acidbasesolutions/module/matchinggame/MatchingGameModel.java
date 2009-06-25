/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Acid.CustomAcid;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.module.ABSModel;

/**
 * MatchingGameModel is the model for MatchingGameModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameModel extends ABSModel {
    
    private static final double CONCENTRATION_CLOSENESS = 0.15; // percent, 0-1
    private static final double STRENGTH_CLOSENESS = 0.15; // percent, 0-1
    
    private static final int POINTS_ACIDBASE_CORRECT = 1;
    private static final int POINTS_ACIDBASE_WRONG = -1;
    private static final int POINTS_ACID_BASE_GIVEUP = 0;

    private static final int POINTS_MATCH_CORRECT = 5;
    private static final int POINTS_MATCH_WRONG = 1;
    private static final int POINTS_MATCH_GIVEUP = 0;

    private int numberOfSolutions;
    private int points;
    private final AqueousSolution solutionLeft, solutionRight;
    private final Random randomSolute, randomConcentration, randomStrength;
    private final ArrayList<MatchingGameModelListener> listeners;
    private boolean acidBaseGuessed, matchGuessed;

    public MatchingGameModel( ABSClock clock ) {
        super( clock );
        numberOfSolutions = 0;
        points = 0;
        solutionLeft = new AqueousSolution();
        solutionRight = new AqueousSolution();  
        randomSolute = new Random();
        randomConcentration = new Random();
        randomStrength = new Random();
        listeners = new ArrayList<MatchingGameModelListener>();
        acidBaseGuessed = matchGuessed = false;
        newSolution();
    }

    public AqueousSolution getSolutionLeft() {
        return solutionLeft;
    }

    public AqueousSolution getSolutionRight() {
        return solutionRight;
    }
    
    public void reset() {
        setPoints( 0 );
        setNumberOfSolutions( 0 );
        newSolution();
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
    
    /**
     * Generates the next solution to be matched.
     * The left and right solutions will be the same type (either Custom Acid or Custom Base),
     * but their concentrations and strengths initially will be different.
     */
    public void newSolution() {
        
        // deduct points for giving up
        if ( !acidBaseGuessed ) {
            changePoints( POINTS_ACID_BASE_GIVEUP );
        }
        else if ( !matchGuessed ) {
            changePoints( POINTS_MATCH_GIVEUP );
        }
        acidBaseGuessed = matchGuessed = false;
        
        // generate new solutions
        Solute soluteLeft = null;
        Solute soluteRight = null;
        if ( randomSolute.nextBoolean() ) {
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
    
    private double getRandomConcentration() {
        double d = randomConcentration.nextDouble();
        return ABSConstants.CONCENTRATION_RANGE.getMin() + ( d * ABSConstants.CONCENTRATION_RANGE.getLength() );
    }
    
    private double getRandomStrength() {
        double d = randomStrength.nextDouble();
        return ABSConstants.CUSTOM_STRENGTH_RANGE.getMin() + ( d * ABSConstants.CUSTOM_STRENGTH_RANGE.getLength() );
    }
    
    private void changePoints( int delta ) {
        if ( delta != 0 ) {
            setPoints( getPoints() + delta );
        }
    }
    
    private void setPoints( int points ) {
        if ( points != this.points ) {
            this.points = points;
            notifyPointsChanged( points );
        }
    }
    
    public int getPoints() {
        return points;
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
        boolean success = false;
        if ( solutionLeft.isAcidic() ) {
            success = true;
            if ( !acidBaseGuessed ) {
                changePoints( POINTS_ACIDBASE_CORRECT );
            }
        }
        else {
            success = false;
            changePoints( POINTS_ACIDBASE_WRONG );
        }
        acidBaseGuessed = true;
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
        boolean success = false;
        if ( solutesMatch() && concentrationsMatch() && strengthsMatch() ) { 
            success = true;
            if ( !matchGuessed ) {
                changePoints( POINTS_MATCH_CORRECT );
            }
        }
        else {
            success = false;
            changePoints( POINTS_MATCH_WRONG );
        }
        matchGuessed = true;
        return success;
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
    
    public interface MatchingGameModelListener {
        public void newSolution();
        public void pointChanged( int points );
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
            i.next().newSolution();
        }
    }
    
    private void notifyPointsChanged( int points ) {
        Iterator<MatchingGameModelListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().pointChanged( points );
        }
    }
    
    private void notifyNumberOfSolutionsChanged( int numberOfSolutions ) {
        Iterator<MatchingGameModelListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().numberOfSolutionsChanged( numberOfSolutions );
        }
    }
    
    
}
