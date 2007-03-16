/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.io;

//import edu.colorado.phet.cck.elements.circuit.Circuit;
//import edu.colorado.phet.circuitconstructionkit.xml.CircuitCodec;

import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.xml.CircuitData;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.srr.localjnlp.ServiceSource;
import org.srr.localjnlp.local.InputStreamFileContents;

import javax.jnlp.FileContents;
import javax.jnlp.FileSaveService;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * User: Sam Reid
 * Date: Nov 23, 2003
 * Time: 12:31:16 AM
 * Copyright (c) Nov 23, 2003 by Sam Reid
 */
public class FileSave {
    ServiceSource ss = new ServiceSource();

    public FileSave() {
    }

    public String toXMLString( Circuit circuit ) throws ValidationException, MarshalException {
        CircuitData cd = new CircuitData( circuit );
        StringWriter sw = new StringWriter();
        Marshaller.marshal( cd, sw );
        String circuitxml = sw.toString();
        return circuitxml;
    }

    public void save( Circuit circuit, Component parent ) {
        ServiceSource ss = new ServiceSource();
        FileSaveService fos = ss.getFileSaveService( parent );

//        CircuitCodec cc = new CircuitCodec();
//        XMLMap map = cc.toXML(s.getCircuit());
//        String circuitxml = XMLMapWriter.toXMLString(map);
//        CircuitData cd = new CircuitData(circuit);
//        StringWriter sw = new StringWriter();
        try {
//            Marshaller.marshal(cd, sw);
//            String circuitxml = sw.toString();
            String circuitxml = toXMLString( circuit );
            InputStream stream = new ByteArrayInputStream( circuitxml.getBytes() );
            FileContents data = new InputStreamFileContents( "circuitxml", stream );
            FileContents out = fos.saveAsFileDialog( "circuit.cck", new String[]{"cck"}, data );
        }
        catch( MarshalException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch( ValidationException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

//    public void save(Circuit c, Component parent) {
//        FileSaveService service = ss.getFileSaveService(parent);
//        FileContents fc = new
//                service.saveAsFileDialog(System.getProperty("system.path"), new String[]{".cck"},)
//    }
}
