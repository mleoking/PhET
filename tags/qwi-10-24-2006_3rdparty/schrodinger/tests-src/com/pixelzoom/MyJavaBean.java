package com.pixelzoom;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

/**
 * MyJavaBean demonstrates how to save and load
 * a Java Bean using XMLEncoder and XMLDecoder.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MyJavaBean {

    /**
     * Test harness.
     *
     * @param args
     */
    public static void main( String[] args ) {

        final String filename = "foo.xml";

        try {
            // Create a bean...
            MyJavaBean bean1 = new MyJavaBean();
            bean1.setName( "Jack" );

            // Save the bean using XMLEncoder...
            MyJavaBean.save( bean1, filename );

            // Load the bean using XMLDecoder...
            MyJavaBean bean2 = (MyJavaBean)MyJavaBean.load( filename );

            // Compare the beans...
            System.out.println( "save " + bean1 );
            System.out.println( "load " + bean2 );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    /* Zero-arg constructor, for Java Bean compliance */
    public MyJavaBean() {
    }

    /* Instance data, with get/set methods */
    private String name;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String toString() {
        return getClass().getName() + "[name=" + name + "]";
    }

    /**
     * Saves an object to a file using XMLEncoder.
     *
     * @param object
     * @param filename
     * @throws FileNotFoundException
     */
    public static void save( Object object, String filename ) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream( filename );
        BufferedOutputStream bos = new BufferedOutputStream( fos );
        XMLEncoder encoder = new XMLEncoder( bos );
        encoder.setExceptionListener( new ExceptionListener() {
            public void exceptionThrown( Exception e ) {
                e.printStackTrace();
            }
        } );
        encoder.writeObject( object );
        encoder.close();
    }

    /**
     * Loads an object from a file using XMLDecoder.
     *
     * @param filename
     * @return an object, possibly null
     * @throws FileNotFoundException
     */
    public static Object load( String filename ) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream( filename );
        BufferedInputStream bis = new BufferedInputStream( fis );
        XMLDecoder decoder = new XMLDecoder( bis );
        decoder.setExceptionListener( new ExceptionListener() {
            public void exceptionThrown( Exception e ) {
                e.printStackTrace();
            }
        } );
        Object object = decoder.readObject();
        decoder.close();
        return object;
    }
}
