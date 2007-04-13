/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.test;

import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.xml.CircuitData;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

import java.io.FileReader;
import java.io.FileWriter;

/**
 * User: Sam Reid
 * Date: Nov 22, 2003
 * Time: 5:16:19 PM
 * Copyright (c) Nov 22, 2003 by Sam Reid
 */
public class TestSerialize {
    public static void main( String[] args ) throws Exception, MarshalException, ValidationException, MappingException {
        // Create a File to marshal to
//        FileWriter writer = new FileWriter("test.xml");

//        String object = "Hello";


// Marshal the person object

//        MappingTool.main(new String[]{"-i",Circuit.class.getName(),"-o","circuit-mainmap.xml"});
//                                      TestSerialize.class.getName()});

//        MappingTool mt=new MappingTool();
//        mt.addClass(Circuit.class);
//        mt.write(new FileWriter("circuit-map.xml"));
//
//        Mapping mapping=new Mapping();
//        mapping.loadMapping("circuit-map.xml");
//        mapping.loadMapping("circuit-mainmap.xml");

//
//        UIController.main(new String[0]);
//
//        Circuit object=new Circuit();
//        Marshaller marshaller=new Marshaller(new FileWriter("mycircuit.xml"));
//        marshaller.setMapping(mapping);
//        marshaller.marshal(object);

//        Person object=new Person("Larry");
//        Marshaller.marshal(object, new FileWriter("person.xml"));
//
//        Person alice=new Person("Alice");
//        Person bob=new Person("Bob");
//        PersonList list=new PersonList();
//        ArrayList myList=new ArrayList();
//        myList.add(alice);
//        myList.add(bob);
//        list.setPeople(myList);
//
//        Marshaller.marshal(list, new FileWriter("personlist.xml"));
//        System.out.println("System.getProperties() = " + System.getProperties());
//        PersonList loadlist=(PersonList) Unmarshaller.unmarshal(PersonList.class,new FileReader("personlist.xml"));
//
//        System.out.println("Loaded list="+loadlist);
//        FileReader reader=new FileReader("test.xml");
//                Unmarshaller.unmarshal(Circuit.class,new FileReader("mycircuit.xml"));

        Circuit c = new Circuit();
//        Wire w = new Wire(c, 0, 0, 100, 100);
//        c.addBranch(w);
//        Wire w2 = new Wire(c, 100, 100, 300, 450);
//        c.addBranch(w2);
//        Bulb bulb = new Bulb(c, 234, 234.9999, 0, 1, new ImmutableVector2D.Double(.5, 0), 1);
//        c.addBranch(bulb);
        Battery batt = new Battery( c, 0, 0, 130, 0, 20, 1 );
        c.addBranch( batt );
//        Resistor r=new Resistor(c, 0,0,0,0,1000);
//        c.addBranch(r);
        CircuitData cd = new CircuitData( c );
        Marshaller.marshal( cd, new FileWriter( "mycircuit-data.xml" ) );

        CircuitData loaded = (CircuitData)Unmarshaller.unmarshal( CircuitData.class, new FileReader( "mycircuit-data.xml" ) );
        Circuit lc = loaded.toCircuit();
        System.out.println( "c = " + c );
        System.out.println( "lc = " + lc );
    }
}
