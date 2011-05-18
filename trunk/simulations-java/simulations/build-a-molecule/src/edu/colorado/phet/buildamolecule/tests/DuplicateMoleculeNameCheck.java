//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.tests;

import java.util.List;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.MoleculeList;

public class DuplicateMoleculeNameCheck {
    public static void main( String[] args ) {
        List<CompleteMolecule> molecules = MoleculeList.getMasterInstance().getAllCompleteMolecules();

        for ( CompleteMolecule a : molecules ) {
            for ( CompleteMolecule b : molecules ) {
                if ( a.cid < b.cid && a.getCommonName().equals( b.getCommonName() ) ) {
                    System.out.println( "duplicate name: " + a.getCommonName() + " -- " + a.cid + "," + b.cid );
                    System.out.println( a.cid + ": " + a.getGeneralFormula() );
                    System.out.println( b.cid + ": " + b.getGeneralFormula() );
                }
            }
        }
    }
}
