package edu.colorado.phet.buildamolecule.model.data;

/**
 * Represents a simple molecule file.
 */
public class MoleculeFile {
    public final int cid;
    public final String content;

    public MoleculeFile( int cid, String content ) {
        this.cid = cid;
        this.content = content;
    }
}
