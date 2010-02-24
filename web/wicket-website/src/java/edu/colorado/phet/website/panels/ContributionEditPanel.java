package edu.colorado.phet.website.panels;

import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;
import org.hibernate.Session;

import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.panels.lists.LevelSetManager;
import edu.colorado.phet.website.panels.lists.SimSetManager;
import edu.colorado.phet.website.panels.lists.SubjectSetManager;
import edu.colorado.phet.website.panels.lists.TypeSetManager;
import edu.colorado.phet.website.util.PageContext;

public class ContributionEditPanel extends PhetPanel {

    private final Contribution contribution;
    private boolean creating;

    private PageContext context;

    private static Logger logger = Logger.getLogger( ContributionEditPanel.class.getName() );

    private SimSetManager simManager;
    private TypeSetManager typeManager;
    private LevelSetManager levelManager;
    private SubjectSetManager subjectManager;

    public ContributionEditPanel( String id, PageContext context, Contribution preContribution ) {
        super( id, context );

        this.context = context;

        add( HeaderContributor.forCss( "/css/contribution-main-v1.css" ) );

        contribution = preContribution;

        creating = contribution.getId() == 0;

        // initialize selectors

        simManager = new SimSetManager( getHibernateSession(), getLocale() ) {
            @Override
            public Set getInitialSimulations( Session session ) {
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    return activeContribution.getSimulations();
                }
                else {
                    return new HashSet();
                }
            }
        };

        typeManager = new TypeSetManager( getHibernateSession(), getLocale() ) {
            @Override
            public Set getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionTypes = activeContribution.getTypes();
                    for ( Object o : contributionTypes ) {
                        set.add( ( (ContributionType) o ).getType() );
                    }
                }
                return set;
            }
        };

        levelManager = new LevelSetManager( getHibernateSession(), getLocale() ) {
            public Collection<Level> getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionLevels = activeContribution.getLevels();
                    for ( Object o : contributionLevels ) {
                        set.add( ( (ContributionLevel) o ).getLevel() );
                    }
                }
                return set;
            }
        };

        subjectManager = new SubjectSetManager( getHibernateSession(), getLocale() ) {
            public Collection<Subject> getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionSubjects = activeContribution.getSubjects();
                    for ( Object o : contributionSubjects ) {
                        set.add( ( (ContributionSubject) o ).getSubject() );
                    }
                }
                return set;
            }
        };

        add( new ContributionForm( "contributionform" ) );

        add( new FeedbackPanel( "feedback" ) );
    }

    public final class ContributionForm extends Form {

        private RequiredTextField authorsText;
        private RequiredTextField organizationText;
        private RequiredTextField emailText;
        private RequiredTextField titleText;
        private RequiredTextField keywordsText;

        private MultipleFileUploadPanel uploadPanel;

        private TextArea descriptionText;

        private DurationDropDownChoice durationChoice;

        private CheckBox answersCheck;

        private CheckBox stdK4A;
        private CheckBox stdK4B;
        private CheckBox stdK4C;
        private CheckBox stdK4D;
        private CheckBox stdK4E;
        private CheckBox stdK4F;
        private CheckBox stdK4G;
        private CheckBox std58A;
        private CheckBox std58B;
        private CheckBox std58C;
        private CheckBox std58D;
        private CheckBox std58E;
        private CheckBox std58F;
        private CheckBox std58G;
        private CheckBox std912A;
        private CheckBox std912B;
        private CheckBox std912C;
        private CheckBox std912D;
        private CheckBox std912E;
        private CheckBox std912F;
        private CheckBox std912G;

        public ContributionForm( String id ) {
            super( id );

            setMultiPart( true );
            setMaxSize( Bytes.megabytes( 64 ) );

            authorsText = new RequiredTextField( "contribution.edit.authors", new Model( creating ? "" : contribution.getAuthors() ) );
            add( authorsText );

            organizationText = new RequiredTextField( "contribution.edit.organization", new Model( creating ? "" : contribution.getAuthorOrganization() ) );
            add( organizationText );

            emailText = new RequiredTextField( "contribution.edit.email", new Model( creating ? "" : contribution.getContactEmail() ) );
            add( emailText );

            titleText = new RequiredTextField( "contribution.edit.title", new Model( creating ? "" : contribution.getTitle() ) );
            add( titleText );

            keywordsText = new RequiredTextField( "contribution.edit.keywords", new Model( creating ? "" : contribution.getKeywords() ) );
            add( keywordsText );

            descriptionText = new TextArea( "contribution.edit.description", new Model( creating ? "" : contribution.getDescription() ) );
            add( descriptionText );

            uploadPanel = new MultipleFileUploadPanel( "file-upload", context );
            add( uploadPanel );

            add( simManager.getComponent( "simulations", context ) );
            add( typeManager.getComponent( "types", context ) );
            add( levelManager.getComponent( "levels", context ) );

            add( subjectManager.getComponent( "subjects", context ) );

            durationChoice = new DurationDropDownChoice( "duration", creating ? 0 : contribution.getDuration() );
            add( durationChoice );

            answersCheck = new CheckBox( "answers", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isAnswersIncluded() ) ) );
            add( answersCheck );

            //stdK4A = new CheckBox( "stdK4A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4A() ) ) ); add( stdK4A );

            stdK4A = new CheckBox( "stdK4A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4A() ) ) );
            add( stdK4A );
            stdK4B = new CheckBox( "stdK4B", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4B() ) ) );
            add( stdK4B );
            stdK4C = new CheckBox( "stdK4C", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4C() ) ) );
            add( stdK4C );
            stdK4D = new CheckBox( "stdK4D", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4D() ) ) );
            add( stdK4D );
            stdK4E = new CheckBox( "stdK4E", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4E() ) ) );
            add( stdK4E );
            stdK4F = new CheckBox( "stdK4F", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4F() ) ) );
            add( stdK4F );
            stdK4G = new CheckBox( "stdK4G", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandardK4G() ) ) );
            add( stdK4G );
            std58A = new CheckBox( "std58A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58A() ) ) );
            add( std58A );
            std58B = new CheckBox( "std58B", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58B() ) ) );
            add( std58B );
            std58C = new CheckBox( "std58C", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58C() ) ) );
            add( std58C );
            std58D = new CheckBox( "std58D", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58D() ) ) );
            add( std58D );
            std58E = new CheckBox( "std58E", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58E() ) ) );
            add( std58E );
            std58F = new CheckBox( "std58F", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58F() ) ) );
            add( std58F );
            std58G = new CheckBox( "std58G", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard58G() ) ) );
            add( std58G );
            std912A = new CheckBox( "std912A", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912A() ) ) );
            add( std912A );
            std912B = new CheckBox( "std912B", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912B() ) ) );
            add( std912B );
            std912C = new CheckBox( "std912C", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912C() ) ) );
            add( std912C );
            std912D = new CheckBox( "std912D", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912D() ) ) );
            add( std912D );
            std912E = new CheckBox( "std912E", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912E() ) ) );
            add( std912E );
            std912F = new CheckBox( "std912F", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912F() ) ) );
            add( std912F );
            std912G = new CheckBox( "std912G", new Model( creating ? Boolean.FALSE : new Boolean( contribution.isStandard912G() ) ) );
            add( std912G );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            logger.debug( "Main submission" );

            logger.debug( "authors: " + authorsText.getModelObjectAsString() );

            for ( Simulation simulation : simManager.getSimulations() ) {
                logger.debug( "simulation: " + simulation.getName() );
            }

            for ( Type type : typeManager.getValues() ) {
                logger.debug( "type: " + type );
            }

            for ( Level level : levelManager.getValues() ) {
                logger.debug( "level: " + level );
            }

            for ( Subject subject : subjectManager.getValues() ) {
                logger.debug( "subject: " + subject );
            }

            for ( FileUpload fileUpload : uploadPanel.getUploadedFiles() ) {
                logger.debug( "file upload: " + fileUpload.getClientFileName() );
            }

        }

        @Override
        protected void delegateSubmit( IFormSubmittingComponent submittingComponent ) {
            // we must override the delegate submit because we will have child ajax buttons to handle adding / removing
            // things like the simulations. they should NOT trigger a full form submission.

            // yes this is ugly, maybe nested forms could be used?

            logger.debug( "submit delegation from: " + submittingComponent );

            if ( submittingComponent == null ) {
                // submit like normal
                super.delegateSubmit( submittingComponent );
            }
            else {
                submittingComponent.onSubmit();
            }
        }
    }

    public class DurationDropDownChoice extends DropDownChoice {
        public DurationDropDownChoice( String id, int initialDuration ) {
            super( id, new Model( new DurationItem( initialDuration ) ), DurationItem.getAllItems(), new IChoiceRenderer() {
                public Object getDisplayValue( Object object ) {
                    if ( object instanceof DurationItem ) {
                        return ( (DurationItem) object ).getDisplayValue();
                    }
                    else {
                        throw new RuntimeException( "Not an DurationItem" );
                    }
                }

                public String getIdValue( Object object, int index ) {
                    if ( object instanceof DurationItem ) {
                        return String.valueOf( ( (DurationItem) object ).getId() );
                    }
                    else {
                        throw new RuntimeException( "Not an DurationItem" );
                    }
                }
            } );
        }
    }

    private static class DurationItem implements Serializable {

        private int duration;

        private DurationItem( int duration ) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public static List getAllItems() {
            // TODO: build into contribution code so we can keep values in one place
            return Arrays.asList( new DurationItem( 0 ), new DurationItem( 30 ), new DurationItem( 60 ), new DurationItem( 90 ), new DurationItem( 120 ) );
        }

        public String getDisplayValue() {
            // TODO: localize
            if ( duration == 0 ) {
                return "NA";
            }
            return String.valueOf( duration ) + " minutes";
        }

        public Component getDisplayComponent( String id ) {
            return new Label( id, getDisplayValue() );
        }

        public int getId() {
            return duration;
        }

    }


}
