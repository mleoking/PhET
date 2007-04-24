/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import org.apache.tools.ant.taskdefs.Echo;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
//todo: needs better error handling for loading flavors
//todo: test support for deploying with online url

//todo: see other todos below
public class PhetBuildJnlpTask extends AbstractPhetBuildTask {
    private static final File JNLP_TEMPLATE = new File( "templates/webstart2.jnlp" );

    private volatile String flavorName;
    private volatile String deployUrl;
    private volatile String locale = "en";

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        if( flavorName == null ) {
            flavorName = phetProject.getName();
        }
        PhetProjectFlavor flavor = phetProject.getFlavor( flavorName, locale );
        System.out.println( "loaded flavor=" + flavor );
        if( deployUrl == null ) {
            deployUrl = phetProject.getDefaultDeployDir().toURL().toString();
        }
        String jnlpFile = filterJNLP( createJNLPFilterMap( flavor, phetProject ), loadJnlpTemplate() );
        System.out.println( "filtered jnlp = " + jnlpFile );
        writeJNLP( new File( phetProject.getDefaultDeployDir(), "" + flavorName + ".jnlp" ), jnlpFile );
    }

    private String filterJNLP( HashMap map, String jnlpFile ) {
        Set set = map.keySet();
        for( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            String key = (String)iterator.next();
            String value = (String)map.get( key );
            Echo echo=new Echo();
            echo.setMessage( "key = " + key+", value="+value );
            runTask( echo );
            jnlpFile = jnlpFile.replaceAll( "@" + key + "@", value );
        }
        return jnlpFile;
    }

    private void writeJNLP( File destFile, String jnlpFile ) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( destFile ), "UTF-16" ) );//todo: user specified output dir
        bufferedWriter.write( jnlpFile );
        bufferedWriter.close();
    }

    private HashMap createJNLPFilterMap( PhetProjectFlavor flavor, PhetProject phetProject ) {
        HashMap map = new HashMap();
        map.put( "PROJECT.NAME", flavor.getName() );
        map.put( "JNLP.NAME", flavorName + ".jnlp" );
        map.put( "PROJECT.DESCRIPTION", flavor.getDescription() );
        map.put( "PROJECT.JAR", phetProject.getJarFile().getName() );
        map.put( "PROJECT.SCREENSHOT", "http://phet.colorado.edu/Design/Assets/images/Phet-Kavli-logo.jpg" );//todo: map this to correct sim-specific (possibly online) URL
        map.put( "PROJECT.MAINCLASS", flavor.getMainclass() );
        map.put( "PROJECT.ARGS", toJNLPArgs( flavor.getArgs() ) );
        map.put( "PROJECT.PROPERTIES", getJNLPProperties() );
        map.put( "PROJECT.DEPLOY.PATH", deployUrl );
        return map;
    }

    private String getJNLPProperties() {//todo: locale support
        return "";
    }

    private String toJNLPArgs( String[] args ) {
        String string = "";
        for( int i = 0; i < args.length; i++ ) {
            string += "<argument>" + args[i] + "</argument>\n";
        }
        return string;
    }

    private String loadJnlpTemplate() throws IOException {
        InputStream inStream = new FileInputStream( JNLP_TEMPLATE );
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int c;
        while( ( c = inStream.read() ) >= 0 ) {
            outStream.write( c );
        }
        outStream.flush();
        return new String( outStream.toByteArray(), "utf-16" );
    }

    public void setFlavor( String flavorName ) {
        this.flavorName = flavorName;
    }

    /**
     * Sets the 2-character locale code to be used for reading title and description for jnlp file.
     *
     * @param locale
     */
    public void setLocale( String locale ) {//todo: not supported yet
        this.locale = locale;
    }

    public void setDeployUrl( String deployUrl ) {
        this.deployUrl = deployUrl;
    }

    public static void main( String[] args ) throws Exception {
        System.out.println( new File( "." ).getAbsolutePath() );
        System.out.println( new PhetBuildJnlpTask().loadJnlpTemplate() );

        PhetProject phetProject = new PhetProject( new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations\\cck" ) );

        PhetBuildJnlpTask phetBuildJnlpTask = new PhetBuildJnlpTask();
        phetBuildJnlpTask.setFlavor( "cck-ac" );
        phetBuildJnlpTask.executeImpl( phetProject );
    }
}
