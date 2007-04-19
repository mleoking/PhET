/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.*;

public class PhetBuildJnlpTask extends AbstractPhetBuildTask {
    private static final File JNLP_TEMPLATE = new File("../../templates/webstart2.jnlp");


    private volatile String flavor;
    private volatile String deployUrl;

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        if (flavor == null) flavor = phetProject.getName();

        if (deployUrl == null) {
            deployUrl = phetProject.getDefaultDeployDir().toURL().toString();

            System.out.println("Deploy url = " + deployUrl );
        }

        String jnlpFile = loadJnlpTemplate();

        
    }

    private String loadJnlpTemplate() throws IOException {
        InputStream inStream = new FileInputStream( JNLP_TEMPLATE );

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        int c;

        while ((c = inStream.read()) >= 0) {
            outStream.write(c);
        }

        outStream.flush();

        return new String( outStream.toByteArray(), "utf-16" );
    }


    public void setFlavor( String flavor ) {
        this.flavor = flavor;
    }

    public void setDeployUrl( String deployUrl ) {
        this.deployUrl = deployUrl;
    }


    public static void main( String[] args ) throws Exception {
        System.out.println( new File(".").getAbsolutePath() );
        System.out.println( new PhetBuildJnlpTask().loadJnlpTemplate() );
    }
}
