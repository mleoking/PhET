package edu.colorado.phet.wickettest.translation;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.resource.loader.IStringResourceLoader;

import edu.colorado.phet.wickettest.panels.PhetPanel;

public class TranslationStringResourceLoader implements IStringResourceLoader {
    public String loadStringResource( Class clazz, String key, Locale locale, String style ) {
        System.out.println( "load:clazz " + key );
        return null;
    }

    public String loadStringResource( Component component, String key ) {
        Locale locale = null;
        Component comp = component;
        System.out.println( "load:component " + key );

        while ( !( comp instanceof Page ) && comp != null ) {
            if ( locale == null && comp instanceof PhetPanel ) {
                locale = ( (PhetPanel) comp ).getMyLocale();
            }
            comp = comp.getParent();
        }

        if ( comp != null && comp instanceof TranslationPage ) {
            return ( (TranslationPage) comp ).translateString( component, locale, key );
        }
        else {
            return null;
        }

    }
}
