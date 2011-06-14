// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.tests;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.*;
import com.mongodb.Mongo;

/**
 * @author Sam Reid
 */
@Entity("employees")
public class Employee {
    @Id ObjectId id; // auto-generated, if not set (see ObjectId)
    String firstName, lastName; // value types are automatically persisted
    Long salary = null; // only non-null values are stored
    String address; // by default fields are @Embedded
    Key<Employee> manager; //references can be saved without automatic loading
    @Reference List<Employee> underlings = new ArrayList<Employee>(); //refs are stored*, and loaded automatically
    @Serialized String reviews; // stored in one binary field
    @Property("started") Date startDate; //fields can be renamed
    @Property("left") Date endDate;

    @Indexed boolean active = false; //fields can be indexed for better performance
    @NotSaved String readButNotStored; //fields can loaded, but not saved
    @Transient int notStored; //fields can be ignored (no load/save)
    transient boolean stored = true; //not @Transient, will be ignored by Serialization/GWT for example.
    private Employee boss;
    private int index;

    public Employee( String first, String last, Employee boss, int index ) {
        this.firstName = first;
        this.lastName = last;
        this.boss = boss;
        this.index = index;
    }

    public Employee() {
    }

    @Override public String toString() {
        return firstName + " " + lastName + ", boss=" + boss + ", index = " + index;
    }
    //Lifecycle methods -- Pre/PostLoad, Pre/PostPersist...
//    @PostLoad void postLoad( DBObject dbObj ) {...}

    public static void main( String[] args ) throws UnknownHostException {
        final Morphia morphia = new Morphia();
        Datastore ds = morphia.createDatastore( new Mongo(), "hr" );
        morphia.map( Employee.class );

        ds.save( new Employee( "MisterX", "Jenkins", null, 123 ) );
// get an employee without a manager
        Employee found = ds.find( Employee.class ).field( "firstName" ).equal( "MisterX" ).get();
        System.out.println( "found = " + found );

//        Key<Employee> scottsKey = ds.save( new Employee( "Scott", "Hernandez", ds.getKey( boss ), 150 * 1000 ) );

//add Scott as an employee of his manager
//        ds.update( boss, ds.createUpdateOperations( Employee.class ).add( "underlings", scottsKey ) );
//// get Scott's boss; the same as the one above.
//        Employee scottsBoss = ds.find( Employee.class ).filter( "underlings", scottsKey ).get();
//
//        for ( Employee e : ds.find( Employee.class, "manager", boss ) ) { print( e ); }
    }
}
