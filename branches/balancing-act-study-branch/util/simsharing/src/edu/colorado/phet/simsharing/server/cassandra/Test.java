//// Copyright 2002-2011, University of Colorado
//package edu.colorado.phet.simsharing.server.cassandra;
//
//import me.prettyprint.cassandra.serializers.StringSerializer;
//import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
//import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
//import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
//import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
//import me.prettyprint.hector.api.Cluster;
//import me.prettyprint.hector.api.Keyspace;
//import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
//import me.prettyprint.hector.api.exceptions.HectorException;
//import me.prettyprint.hector.api.factory.HFactory;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
//import edu.colorado.phet.simsharing.SimHelper;
//
///**
// * See test at: https://github.com/rantav/hector/wiki/Getting-started-%285-minutes%29
// *
// * @author Sam Reid
// */
//public class Test {
//
//    public static void main( String[] args ) {
//        Cluster myCluster = HFactory.getOrCreateCluster( "test-cluster", "localhost:9160" );
//        System.out.println( "myCluster = " + myCluster );
//
//        KeyspaceDefinition newKeyspace = CassandraStorage.createSchema();
//
//        // Add the schema to the cluster.
//        // "true" as the second param means that Hector will block until all nodes see the change.
//        myCluster.addKeyspace( newKeyspace, true );
//
//        KeyspaceDefinition keyspaceDef = myCluster.describeKeyspace( CassandraStorage.KEYSPACE_NAME );
//
//// If keyspace does not exist, the CFs don't exist either. => create them.
//        if ( keyspaceDef == null ) {
//            CassandraStorage.createSchema();
//        }
//
//        Keyspace ksp = HFactory.createKeyspace( CassandraStorage.KEYSPACE_NAME, myCluster );
//
//        System.out.println( "ksp = " + ksp );
//
//        ColumnFamilyTemplate<String, String> template = new ThriftColumnFamilyTemplate<String, String>( ksp, "ColumnFamilyName", StringSerializer.get(), StringSerializer.get() );
//
//        GravityAndOrbitsApplication sim = SimHelper.GRAVITY_AND_ORBITS_LAUNCHER.apply();
//        final byte[] state = toByteArray( sim.getState() );
//
//        int framesPerSec = 30;
//        int secPerMinute = 60;
//        int minutePerHour = 60;
//        for ( int i = 0; i < framesPerSec * secPerMinute * minutePerHour; i++ ) {
//
//            // <String, String> correspond to key and Column name.
//            ColumnFamilyUpdater<String, String> updater = template.createUpdater( "a key" );
//            updater.setString( "domain", "www.datastax.com" );
//            updater.setLong( "time", System.currentTimeMillis() );
//
//            updater.setByteArray( "frame_" + i, state );
//
//            if ( i % 100 == 0 ) {
//                System.out.println( "i = " + i );
//            }
//
//            try {
//                template.update( updater );
//            }
//            catch ( HectorException e ) {
//                // do something ...
//            }
//        }
//
//        try {
//            ColumnFamilyResult<String, String> res = template.queryColumns( "a key" );
//            String value = res.getString( "domain" );
//            System.out.println( "value = " + value );
//            System.out.println( "object = " + toObject( res.getByteArray( "object" ) ) );
//            // value should be "www.datastax.com" as per our previous insertion.
//        }
//        catch ( HectorException e ) {
//            // do something ...
//        }
//
//        try {
//            template.deleteColumn( "key", "column name" );
//        }
//        catch ( HectorException e ) {
//            // do something
//        }
//    }
//
//    public static byte[] toByteArray( Object obj ) {
//        byte[] bytes = null;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream( bos );
//            oos.writeObject( obj );
//            oos.flush();
//            oos.close();
//            bos.close();
//            bytes = bos.toByteArray();
//        }
//        catch ( IOException ex ) {
//            //TODO: Handle the exception
//        }
//        return bytes;
//    }
//
//    public static Object toObject( byte[] bytes ) {
//        Object obj = null;
//        try {
//            ByteArrayInputStream bis = new ByteArrayInputStream( bytes );
//            ObjectInputStream ois = new ObjectInputStream( bis );
//            obj = ois.readObject();
//        }
//        catch ( IOException ex ) {
//            //TODO: Handle the exception
//        }
//        catch ( ClassNotFoundException ex ) {
//            //TODO: Handle the exception
//        }
//        return obj;
//    }
//}
//
//
////object GeneratePath extends App{
////  val root: File = new File("C:\\workingcopy\\phet\\svn\\trunk\\util\\simsharing-cassandra\\contrib\\hector-core-0.8.0-2")
////  val names = (for (f:File <- root.listFiles if f.getName.toLowerCase.endsWith(".jar")) yield "contrib/"+root.getName+"/"+f.getName)
////  println(names.mkString(" : "))
////}