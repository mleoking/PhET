/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test;

import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.model.BaseModel;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;

/**
 * TestMulitMapPersistence
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestMulitMapPersistence {
    MultiMap multiMap = new MultiMap();
    String filename = "/temp/mm.xml";

    public TestMulitMapPersistence() {
    }

    void save() {
        multiMap.put( new Integer( 1 ), "ONE" );
        multiMap.put( new Integer( 5 ), "FIVE" );
        multiMap.put( new Integer( 3 ), "THREE" );
//        multiMap.putObject(new Integer(1), "ONE");
//        multiMap.putObject(new Integer(5), "FIVE");
//        multiMap.putObject(new Integer(3), "THREE");
        System.out.println( "save = " + multiMap );
        XMLEncoder e = null;
        try {
            e = new XMLEncoder( new BufferedOutputStream( new FileOutputStream( filename ) ) );
        }
        catch( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
//        XMLEncoder e = new XMLEncoder( System.out );
        e.writeObject( multiMap );
        e.close();
    }

    void restore() {
        XMLDecoder decoder = null;
        try {
            decoder = new XMLDecoder( new BufferedInputStream( new FileInputStream( filename ) ) );
        }
        catch( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        MultiMap mm = (MultiMap)decoder.readObject();
        System.out.println( "restore = " + mm );
    }

    public MultiMap getMultiMap() {
        return multiMap;
    }

    public void setMultiMap( MultiMap multiMap ) {
        this.multiMap = multiMap;
    }

    public static void main( String[] args ) {
        TestMulitMapPersistence test = new TestMulitMapPersistence();
        test.save();
        test.restore();
    }
}
