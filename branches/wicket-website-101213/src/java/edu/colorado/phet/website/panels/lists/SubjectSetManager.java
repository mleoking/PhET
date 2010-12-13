package edu.colorado.phet.website.panels.lists;

import java.util.Collection;
import java.util.Locale;

import org.hibernate.Session;

import edu.colorado.phet.website.data.contribution.ContributionSubject;
import edu.colorado.phet.website.data.contribution.Subject;

/**
 * Handles selection of a subset of levels
 */
public abstract class SubjectSetManager extends EnumSetManager<Subject> {

    public SubjectSetManager( Session session, final Locale locale ) {
        super( session, locale );
    }

    public Collection<Subject> getAllValues() {
        return ContributionSubject.getCurrentSubjects();
    }

    public String getTranslationKey( Subject val ) {
        return val.getTranslationKey();
    }
}