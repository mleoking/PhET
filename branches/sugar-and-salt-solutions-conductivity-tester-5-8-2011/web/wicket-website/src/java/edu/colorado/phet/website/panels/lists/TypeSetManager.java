package edu.colorado.phet.website.panels.lists;

import java.util.Collection;
import java.util.Locale;

import org.hibernate.Session;

import edu.colorado.phet.website.data.contribution.ContributionType;
import edu.colorado.phet.website.data.contribution.Type;

/**
 * Handles selection of a subset of types
 */
public abstract class TypeSetManager extends EnumSetManager<Type> {

    public TypeSetManager( Session session, final Locale locale ) {
        super( session, locale );
    }

    public Collection<Type> getAllValues() {
        return ContributionType.getCurrentTypes();
    }

    public String getTranslationKey( Type val ) {
        return val.getTranslationKey();
    }
}