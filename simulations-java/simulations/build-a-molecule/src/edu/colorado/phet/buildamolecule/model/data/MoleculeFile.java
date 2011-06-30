//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model.data;

/**
 * Represents a simple molecule file.
 */
public class MoleculeFile {
    public final int cid;
    public final String content;

    //REVIEW describe the cid param
    public MoleculeFile( int cid, String content ) {
        this.cid = cid;
        this.content = content;
    }
}
