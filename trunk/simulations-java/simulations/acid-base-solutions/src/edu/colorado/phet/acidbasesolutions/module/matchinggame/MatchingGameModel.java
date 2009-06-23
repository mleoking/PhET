/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

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

    private final AqueousSolution solutionLeft, solutionRight;
    private final Random randomSolute, randomConcentration, randomStrength;

    public MatchingGameModel( ABSClock clock ) {
        super( clock );
        solutionLeft = new AqueousSolution();
        solutionRight = new AqueousSolution();  
        randomSolute = new Random();
        randomConcentration = new Random();
        randomStrength = new Random();
        newSolution();
    }

    public AqueousSolution getSolutionLeft() {
        return solutionLeft;
    }

    public AqueousSolution getSolutionRight() {
        return solutionRight;
    }
    
    /**
     * Generates random solutions.
     * The left and right solutions must be the same type (either Custom Acid or Custom Base),
     * but their concentrations and strengths will be different.
     */
    public void newSolution() {
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
    }
    
    private double getRandomConcentration() {
        double d = randomConcentration.nextDouble();
        return ABSConstants.CONCENTRATION_RANGE.getMin() + ( d * ABSConstants.CONCENTRATION_RANGE.getLength() );
    }
    
    private double getRandomStrength() {
        double d = randomStrength.nextDouble();
        return ABSConstants.CUSTOM_STRENGTH_RANGE.getMin() + ( d * ABSConstants.CUSTOM_STRENGTH_RANGE.getLength() );
    }
    
    /**
     * Are we trying to match an acid?
     * @return
     */
    public boolean isAcid() {
        return solutionLeft.isAcidic();
    }
    
    /**
     * Are we trying to match a base?
     * @return
     */
    public boolean isBase() {
        return solutionLeft.isBasic();
    }
    
    /**
     * Check whether the left and right solutions match.
     * Solutions match is their types are the same, and the right solution (the guess)
     * has concentration and strength that is "sufficiently close" to the left solution
     * (the randomly generated solution.)
     * 
     * @return true or false
     */
    public boolean checkMatch() {
        return solutesMatch() && concentrationsMatch() && strengthsMatch();
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
}
