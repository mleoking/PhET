package edu.colorado.phet.molecule;

import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.util.Pair;

/**
 * Allows one to iterate through pairs of equivalent 2d and 3d data.
 */
public class FilteredMoleculeIterator implements Iterator<Pair<MoleculeFile, MoleculeFile>> {
    private MoleculeReader reader2d;
    private MoleculeReader reader3d;

    private MoleculeFile file2d;
    private MoleculeFile file3d;

    int old2dcid = -1;
    int old3dcid = -1;

    public FilteredMoleculeIterator( MoleculeReader reader2d, MoleculeReader reader3d ) {
        this.reader2d = reader2d;
        this.reader3d = reader3d;

        read2d();
        read3d();

    }

    public boolean hasNext() {
        return file2d != null;
    }

    public Pair<MoleculeFile, MoleculeFile> next() {
        while ( file3d != null && file2d.cid > file3d.cid ) {
//            System.out.println( "2d cid " + file2d.cid + " > 3d cid " + file3d.cid );
            read3d();
        }
        boolean hasValid3d = file3d != null && file2d.cid == file3d.cid;
        Pair<MoleculeFile, MoleculeFile> result = new Pair<MoleculeFile, MoleculeFile>( file2d, hasValid3d ? file3d : null );
        read2d();
        return result;
    }

    private void read2d() {
        file2d = reader2d.nextMoleculeFile();
        if ( file2d != null ) {
            assert ( old2dcid < file2d.cid );
            old2dcid = file2d.cid;
        }
    }

    private void read3d() {
        file3d = reader3d.nextMoleculeFile();
        if ( file3d != null ) {
            assert ( old3dcid < file3d.cid );
            old3dcid = file3d.cid;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
