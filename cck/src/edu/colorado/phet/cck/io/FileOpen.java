/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.io;

import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.xml.CircuitData;
import org.exolab.castor.xml.Unmarshaller;
import org.srr.localjnlp.ServiceSource;
import org.xml.sax.InputSource;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import edu.colorado.phet.circuitconstructionkit.model.Circuit;

/**
 * User: Sam Reid
 * Date: Nov 23, 2003
 * Time: 12:37:12 AM
 * Copyright (c) Nov 23, 2003 by Sam Reid
 */
public class FileOpen {
    public Circuit open(Component parent) {
        ServiceSource ss = new ServiceSource();
        FileOpenService fos = ss.getFileOpenService(parent);
        try {
            FileContents out = fos.openFileDialog(null, new String[]{"cck"});
//            O.d("name="+out.getName());
            return openFileContents(out);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            throw new RuntimeException(e);
        }

    }
//
    public Circuit openFileContents(FileContents f) {
        try {
            InputSource source = new InputSource(f.getInputStream());
            InputStreamReader isr = new InputStreamReader(f.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            while (br.ready()) {
                String read = br.readLine();
                System.out.println("read = " + read);
            }
//            isr.read();
            CircuitData loaded = (CircuitData) Unmarshaller.unmarshal(CircuitData.class, source);
            System.out.println("loaded = " + loaded.toCircuit());
            return loaded.toCircuit();
//            Circuit loaded = (Circuit) XMLMapReader.readXML(f.getInputStream());
//            wka.setCircuit(loaded);
//            O.d("loaded: \n" + loaded);
//            if (wka.electronsVisible())
//                wka.getAddElectrons().addElectrons(loaded);
//            wka.getApparatusPanel().repaint();
//            wka.getPhetFrame().repaint();
//            wka.getCircuit().notifyPropertyChange();
//            wka.getCircuit().notifyArchitectureChange();
//        } catch (JDOMException e1) {
//            e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
//        } catch (XMLReadException e1) {
//            e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
