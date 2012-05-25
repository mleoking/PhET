// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

/**
 * Class that defines a separation of the DNA strand.  This is used when
 * forcing the DNA strand to separate in certain locations, which happens,
 * for instance, when RNA polymerase is attached and transcribing the DNA.
 *
 * @author John Blanco
 */
public class DnaSeparation {
    private final double targetAmount;
    private double xPos; // X Position in model space.
    private double amount = 0; // Actual amount of separation.  Starts at 0 and is generally grown over time toward target.

    /**
     * Constructor.
     *
     * @param xPos         - X Position in model space where this separation should
     *                     exist.
     * @param targetAmount
     */
    public DnaSeparation( double xPos, double targetAmount ) {
        this.xPos = xPos;
        this.targetAmount = targetAmount;
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos( double xPos ) {
        this.xPos = xPos;
    }

    public double getAmount() {
        return amount;
    }

    public void setProportionOfTargetAmount( double proportion ) {
        assert proportion >= 0 && proportion <= 1;
        amount = targetAmount * proportion;
    }
}
