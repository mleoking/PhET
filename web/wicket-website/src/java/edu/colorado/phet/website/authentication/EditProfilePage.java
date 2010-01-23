package edu.colorado.phet.website.authentication;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.Linkable;

public class EditProfilePage extends PhetPage {

    // TODO: i18nize

    private static Logger logger = Logger.getLogger( EditProfilePage.class.getName() );

    public EditProfilePage( PageParameters parameters ) {
        super( parameters );

        AuthenticatedPage.checkSignedIn();

        addTitle( "Edit Profile" );

        add( new EditProfileForm( "edit-profile-form" ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^edit-profile$", EditProfilePage.class );
    }

    public static Linkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "edit-profile";
            }
        };
    }

    private List<String> getDescriptionOptions() {
        return Arrays.asList(
                "I am a teacher who uses PhET in my classes",
                "I am a teacher interested in using PhET in the future",
                "I am a student who uses PhET",
                "I am a student interested in using PhET in the future",
                "I am just interested in physics",
                "Other"
        );
    }

    private final class EditProfileForm extends Form {

        private TextField name;
        private TextField organization;
        private DropDownChoice description;
        private TextField jobTitle;

        private TextField address1;
        private TextField address2;
        private TextField city;
        private TextField state;
        private TextField country;
        private TextField zipcode;

        private TextField phone1;
        private TextField phone2;
        private TextField fax;

        private CheckBox receiveEmail;

        private final ValueMap properties = new ValueMap();

        public EditProfileForm( String id ) {
            super( id );

            PhetUser user = PhetSession.get().getUser();

            properties.add( "name", user.getName() );
            properties.add( "organization", user.getOrganization() );
            properties.add( "description", user.getDescription() );
            properties.add( "jobTitle", user.getJobTitle() );
            properties.add( "address1", user.getAddress1() );
            properties.add( "address2", user.getAddress2() );
            properties.add( "city", user.getCity() );
            properties.add( "state", user.getState() );
            properties.add( "country", user.getCountry() );
            properties.add( "zipcode", user.getZipcode() );
            properties.add( "phone1", user.getPhone1() );
            properties.add( "phone2", user.getPhone2() );
            properties.add( "fax", user.getFax() );

            add( name = new TextField( "name", new PropertyModel( properties, "name" ) ) );
            add( organization = new TextField( "organization", new PropertyModel( properties, "organization" ) ) );
            add( description = new DropDownChoice( "description", new PropertyModel( properties, "description" ), getDescriptionOptions() ) );
            add( jobTitle = new TextField( "jobTitle", new PropertyModel( properties, "jobTitle" ) ) );
            add( address1 = new TextField( "address1", new PropertyModel( properties, "address1" ) ) );
            add( address2 = new TextField( "address2", new PropertyModel( properties, "address2" ) ) );
            add( city = new TextField( "city", new PropertyModel( properties, "city" ) ) );
            add( state = new TextField( "state", new PropertyModel( properties, "state" ) ) );
            add( country = new TextField( "country", new PropertyModel( properties, "country" ) ) );
            add( zipcode = new TextField( "zipcode", new PropertyModel( properties, "zipcode" ) ) );
            add( phone1 = new TextField( "phone1", new PropertyModel( properties, "phone1" ) ) );
            add( phone2 = new TextField( "phone2", new PropertyModel( properties, "phone2" ) ) );
            add( fax = new TextField( "fax", new PropertyModel( properties, "fax" ) ) );
            add( receiveEmail = new CheckBox( "receiveEmail", new Model( new Boolean( user.isReceiveEmail() ) ) ) );


        }

        @Override
        protected void onSubmit() {
            // for some reason: very broken!
            for ( Object o : properties.keySet() ) {
                logger.debug( o.toString() + ": " + properties.get( o ) );
            }
            logger.debug( "receiveEmail: " + receiveEmail.getModelObject() );
        }
    }
}
