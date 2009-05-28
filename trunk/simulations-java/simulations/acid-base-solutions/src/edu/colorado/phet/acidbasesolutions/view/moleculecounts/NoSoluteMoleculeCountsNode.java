/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.moleculecounts;


class NoSoluteMoleculeCountsNode extends AbstractMoleculeCountsNode {

    public NoSoluteMoleculeCountsNode() {
        super();
        setVisible( getReactantRow(), false );
        setVisible( getProductRow(), false );
    }
}