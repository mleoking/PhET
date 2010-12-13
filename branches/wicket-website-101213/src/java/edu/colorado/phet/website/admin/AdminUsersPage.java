package edu.colorado.phet.website.admin;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.hibernate.HibernateTask;
import edu.colorado.phet.website.util.hibernate.HibernateUtils;
import edu.colorado.phet.website.util.hibernate.VoidTask;

public class AdminUsersPage extends AdminPage {

    private static final Logger logger = Logger.getLogger( AdminUsersPage.class.getName() );

    public AdminUsersPage( PageParameters parameters ) {
        super( parameters );

        final List<PhetUser> teamMembers = new LinkedList<PhetUser>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List list = session.createQuery( "select u from PhetUser as u where u.teamMember = true" ).list();
                for ( Object o : list ) {
                    teamMembers.add( (PhetUser) o );
                }
                return true;
            }
        } );

        add( new ListView<PhetUser>( "member", teamMembers ) {
            protected void populateItem( ListItem<PhetUser> item ) {
                PhetUser user = item.getModelObject();
                Link link = AdminEditProfilePage.getLinker( user ).getLink( "member-link", getPageContext(), getPhetCycle() );
                item.add( link );
                link.add( new Label( "email", user.getEmail() ) );
            }
        } );

        add( new UserEmailForm( "edit-user-profile-form" ) {
            @Override
            protected void onSubmit() {
                PageParameters params = new PageParameters();
                params.add( AdminEditProfilePage.USER_EMAIL, getEmailAddress() );
                setResponsePage( AdminEditProfilePage.class, params );
            }
        } );

        add( new UserEmailForm( "deactivate-form" ) {
            @Override
            protected void onSubmit() {
                HibernateUtils.wrapTransaction( PhetRequestCycle.get().getHibernateSession(), new VoidTask() {
                    public Void run( Session session ) {
                        PhetUser user = (PhetUser) session.createQuery( "select u from PhetUser as u where u.email = :email" )
                                .setString( "email", getEmailAddress() ).uniqueResult();
                        user.setConfirmed( false );
                        session.update( user );
                        return null;
                    }
                } );
            }
        } );

        add( new UserEmailForm( "delete-form" ) {
            @Override
            protected void onSubmit() {
                HibernateUtils.wrapTransaction( PhetRequestCycle.get().getHibernateSession(), new VoidTask() {
                    public Void run( Session session ) {
                        PhetUser user = (PhetUser) session.createQuery( "select u from PhetUser as u where u.email = :email" )
                                .setString( "email", getEmailAddress() ).uniqueResult();
                        session.delete( user );
                        return null;
                    }
                } );
            }
        } );

    }

    private abstract static class UserEmailForm extends Form {
        private TextField<String> emailField;

        public UserEmailForm( String id ) {
            super( id );

            emailField = new TextField<String>( "email", new Model<String>( "" ) );
            add( emailField );
        }

        public String getEmailAddress() {
            return emailField.getModelObject();
        }
    }
}