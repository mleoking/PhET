package edu.colorado.phet.common.phetcommon.tests.reports;

import java.util.Locale;

/**
 * This class can be used to return a constant dummy string instead of the correct dynamically loaded string
 * for purposes of testing localization coverage.
 */
public class DummyConstantStringTester {
    //This can be used for testing. If non-null, this string is returned on calls to getLocalizedString
    private static String dummyString = null;

    public static String getString( String string ) {
        if ( dummyString != null ) {
            return dummyString;
        }
        else {
            return string;
        }
    }

    //This can be used for testing. If non-null, dummyString is returned on calls to getLocalizedString
    public static void setConstantTestString( String dummyStringValue ) {
        dummyString = dummyStringValue;
    }

    public static void setTestScenario( Locale locale, String dummyStringValue ) {
        Locale.setDefault( locale );
        setConstantTestString( dummyStringValue );
    }
}
