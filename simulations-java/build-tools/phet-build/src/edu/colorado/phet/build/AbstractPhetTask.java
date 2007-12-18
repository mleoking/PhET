package edu.colorado.phet.build;

import org.apache.tools.ant.Task;

import java.io.File;

/**
 * Author: Sam Reid
 * May 14, 2007, 3:46:24 PM
 */
public class AbstractPhetTask extends Task implements AntTaskRunner {

    public void runTask( Task childTask ) {
        childTask.setProject( getProject() );
        childTask.setLocation( getLocation() );
        childTask.setOwningTarget( getOwningTarget() );
        childTask.init();
        childTask.execute();
    }

    protected void echo( String string ) {
        PhetBuildUtils.antEcho( this, string, getClass() );
    }


    public File getBaseDir() {
        return getProject().getBaseDir();
    }

//    private void setAllProjects( Object object ) {
//        Method[] methods = object.getClass().getMethods();
//
//        for ( int i = 0; i < methods.length; i++ ) {
//            Method m = methods[i];
//            if ( m.getName().equals( "setProject" ) && m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == Project.class ) {
//                try {
//                    System.out.println( "Setting project of " + object.toString() );
//
//                    m.invoke( object, new Object[]{ getProject() });
//                }
//                catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//                catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            }
//            else if ( m.getName().startsWith( "get" ) && m.getParameterTypes().length == 0 ) {
//                try {
//                    setAllProjects( m.invoke( object, new Object[]{} ) );
//                }
//                catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//                catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
