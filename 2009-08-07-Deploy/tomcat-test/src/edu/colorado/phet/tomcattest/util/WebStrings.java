package edu.colorado.phet.tomcattest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class WebStrings {
    private ResourceBundle bundle;
    private ServletContext context;
    private Locale locale;

    public WebStrings( ServletContext context, Locale locale ) {
        this.context = context;
        this.locale = locale;

        try {
            InputStream stream = context.getResourceAsStream( "data/localization/tomcat-strings_" + LocaleUtils.localeToString( locale ) + ".properties" );
            if ( stream == null ) {
                System.out.println( "Could not find strings for " + LocaleUtils.localeToString( locale ) + ", using the default" );
                stream = context.getResourceAsStream( "data/localization/tomcat-strings_en.properties" );
            }
            bundle = new PropertyResourceBundle( stream );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public String get( String key ) {
        return bundle.getString( key );
    }

}
