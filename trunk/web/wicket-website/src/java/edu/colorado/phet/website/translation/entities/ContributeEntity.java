package edu.colorado.phet.website.translation.entities;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.contribution.ContributionEditPanel;
import edu.colorado.phet.website.panels.contribution.ContributionMainPanel;
import edu.colorado.phet.website.panels.contribution.ContributionSearchPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class ContributeEntity extends TranslationEntity {
    public ContributeEntity() {
        addString( "contribution.view.downloadableFiles" );
        addString( "contribution.view.zipDownload" );
        addString( "contribution.view.submissionInformation" );
        addString( "contribution.view.authors" );
        addString( "contribution.view.email" );
        addString( "contribution.view.organization" );
        addString( "contribution.view.submitted" );
        addString( "contribution.view.updated" );
        addString( "contribution.view.descriptionSection" );
        addString( "contribution.view.title" );
        addString( "contribution.view.simulations" );
        addString( "contribution.view.keywords" );
        addString( "contribution.view.description" );
        addString( "contribution.view.level" );
        addString( "contribution.view.type" );
        addString( "contribution.view.subject" );
        addString( "contribution.view.duration" );
        addString( "contribution.view.answers" );
        addString( "contribution.view.language" );
        addString( "contribution.view.comments" );
        addString( "contribution.view.addComment" );
        addString( "contribution.view.commentsHeader" );
        addString( "contribution.view.nominateButton" );
        addString( "contribution.view.nominateHeader" );
        addString( "contribution.view.nominateText" );
        addString( "contribution.view.nominationReason" );

        addString( "contribution.standards.header" );
        addString( "contribution.standards.contentLevel" );
        addString( "contribution.standards.contentStandard" );
        addString( "contribution.standards.k4" );
        addString( "contribution.standards.58" );
        addString( "contribution.standards.912" );
        addString( "contribution.standards.A" );
        addString( "contribution.standards.B" );
        addString( "contribution.standards.C" );
        addString( "contribution.standards.D" );
        addString( "contribution.standards.E" );
        addString( "contribution.standards.F" );
        addString( "contribution.standards.G" );

        addString( "contribution.edit.pageTitle" );
        addString( "contribution.edit.requiredInformation" );
        addString( "contribution.edit.authors" );
        addString( "contribution.edit.authors.tip" );
        addString( "contribution.edit.organization" );
        addString( "contribution.edit.email" );
        addString( "contribution.edit.title" );
        addString( "contribution.edit.keywords" );
        addString( "contribution.edit.keywords.tip" );
        addString( "contribution.edit.simulations" );
        addString( "contribution.edit.types" );
        addString( "contribution.edit.levels" );
        addString( "contribution.edit.existingFiles" );
        addString( "contribution.edit.removeFile" );
        addString( "contribution.edit.newFiles" );
        addString( "contribution.edit.optionalInformation" );
        addString( "contribution.edit.description" );
        addString( "contribution.edit.subjects" );
        addString( "contribution.edit.duration" );
        addString( "contribution.edit.answers" );
        addString( "contribution.edit.language" );

        addString( "contribution.create.pageTitle" );
        addString( "contribution.create.checkGuidelines" );

        addString( "contribution.manage.pageTitle" );

        addString( "list.add" );
        addString( "list.empty" );

        addString( "contribution.browse.title" );
        addString( "contribution.browse.authors" );
        addString( "contribution.browse.level" );
        addString( "contribution.browse.type" );
        addString( "contribution.browse.simulations" );
        addString( "contribution.browse.updated" );

        addString( "contribution.search.simulations" );
        addString( "contribution.search.types" );
        addString( "contribution.search.levels" );
        addString( "contribution.search.languages" );
        addString( "contribution.search.allSimulations" );
        addString( "contribution.search.allTypes" );
        addString( "contribution.search.allLevels" );
        addString( "contribution.search.allLocales" );
        addString( "contribution.search.text" );
        addString( "contribution.search.browse" );

        addString( "contribution.level.k5" );
        addString( "contribution.level.middleSchool" );
        addString( "contribution.level.highSchool" );
        addString( "contribution.level.undergraduateIntro" );
        addString( "contribution.level.undergraduateAdvanced" );
        addString( "contribution.level.graduate" );
        addString( "contribution.level.other" );
        addString( "contribution.level.k5.abbrev" );
        addString( "contribution.level.middleSchool.abbrev" );
        addString( "contribution.level.highSchool.abbrev" );
        addString( "contribution.level.undergraduateIntro.abbrev" );
        addString( "contribution.level.undergraduateAdvanced.abbrev" );
        addString( "contribution.level.graduate.abbrev" );
        addString( "contribution.level.other.abbrev" );
        addString( "contribution.subject.astronomy" );
        addString( "contribution.subject.biology" );
        addString( "contribution.subject.chemistry" );
        addString( "contribution.subject.earthScience" );
        addString( "contribution.subject.mathematics" );
        addString( "contribution.subject.physics" );
        addString( "contribution.subject.other" );
        addString( "contribution.subject.astronomy.abbrev" );
        addString( "contribution.subject.biology.abbrev" );
        addString( "contribution.subject.chemistry.abbrev" );
        addString( "contribution.subject.earthScience.abbrev" );
        addString( "contribution.subject.mathematics.abbrev" );
        addString( "contribution.subject.physics.abbrev" );
        addString( "contribution.subject.other.abbrev" );
        addString( "contribution.type.lab" );
        addString( "contribution.type.homework" );
        addString( "contribution.type.conceptQuestions" );
        addString( "contribution.type.demonstration" );
        addString( "contribution.type.other" );
        addString( "contribution.type.lab.abbrev" );
        addString( "contribution.type.homework.abbrev" );
        addString( "contribution.type.conceptQuestions.abbrev" );
        addString( "contribution.type.demonstration.abbrev" );
        addString( "contribution.type.other.abbrev" );
        addString( "contribution.duration" );

        addString( "contribution.answers.yes" );
        addString( "contribution.answers.no" );

        addString( "contribution.edit.authors.Required" );
        addString( "contribution.edit.organization.Required" );
        addString( "contribution.edit.email.Required" );
        addString( "contribution.edit.title.Required" );
        addString( "contribution.edit.keywords.Required" );
        addString( "org.apache.wicket.mfu.delete", "Shown on the remove button for contribution file uploads" );
        addString( "contribution.edit.email.EmailAddressValidator" );
        addString( "contribution.edit.validation.mustHaveSims" );
        addString( "contribution.edit.validation.mustHaveFiles" );
        addString( "contribution.edit.validation.mustHaveTypes" );
        addString( "contribution.edit.validation.mustHaveLevels" );
        addString( "contribution.edit.validation.fileType" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                final Contribution contribution[] = new Contribution[1];
                HibernateUtils.wrapTransaction( requestCycle.getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        List list = session.createQuery( "select c from Contribution as c" ).setMaxResults( 1 ).list();
                        contribution[0] = (Contribution) list.get( 0 );
                        return true;
                    }
                } );
                return new ContributionMainPanel( id, contribution[0], context );
            }
        }, "Contribution view" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ContributionSearchPanel( id, context, new PageParameters() );
            }
        }, "Contribution search / browse" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ContributionEditPanel( id, context, new Contribution() );
            }
        }, "Contribution create / edit" );
    }

    public String getDisplayName() {
        return "Contribute";
    }
}