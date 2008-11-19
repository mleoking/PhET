package edu.colorado.phet.build.ant;

import java.util.Locale;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.util.PhetBuildUtils;

/**
 * Constructs an iterable list of simulations for use in ant-contrib.
 */
public class PhetListLocalesTask extends AbstractPhetBuildTask implements PropertyTask {
    private String property = "sim.locales";

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        String flavorsList = PhetBuildUtils.convertArrayToList( getLocaleCodeList( phetProject.getLocales() ) );
        getProject().setProperty( property, flavorsList );
    }

    private String[] getLocaleCodeList( Locale[] locales ) {
        String[] s = new String[locales.length];
        for ( int i = 0; i < s.length; i++ ) {
            s[i] = locales[i].getLanguage();
        }
        return s;
    }

    public void setProperty( String property ) {
        this.property = property;
    }
}
