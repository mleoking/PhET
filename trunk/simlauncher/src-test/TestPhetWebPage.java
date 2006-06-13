/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.simlauncher.PhetWebPage;

import java.util.List;

/**
 * TestPhetWebPage
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestPhetWebPage {
//    static String pagePath = "file:///Phet/temp/top-simulations.htm";
    static String pagePath = "http://www.colorado.edu/physics/phet/web-pages/simulation-pages/top-simulations.htm";

    public static void main( String[] args ) {
        PhetWebPage phetWebPage = new PhetWebPage(pagePath);
        StringBuffer text = phetWebPage.getText( pagePath);
        System.out.println( "text = " + text );

        System.out.println( "=============================================" );
        System.out.println( "=============================================" );
        System.out.println( "=============================================" );

//        System.out.println( "phetWebPage.stripComments( ) = " + phetWebPage.stripComments() );

        List simSpecs = phetWebPage.getSimSpecs();
        for( int i = 0; i < simSpecs.size(); i++ ) {
            PhetWebPage.SimSpec simSpec = (PhetWebPage.SimSpec)simSpecs.get( i );
            System.out.println( "simSpec = "+ simSpec.getName() + "\t" + simSpec.getJnlpPath() + "\t" + simSpec.getThumbnailPath() + "\t" + simSpec.getAbstractPath() );
        }
    }
}
