package edu.colorado.phet.website.admin;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;

import edu.colorado.phet.website.data.transfer.SqlResultTask;
import edu.colorado.phet.website.data.transfer.SqlUtils;
import edu.colorado.phet.website.util.StringUtils;

public class AdminMainPage extends AdminPage {

    private TextField keyText;
    private TextField valueText;

    private static Logger logger = Logger.getLogger( AdminMainPage.class.getName() );

    public AdminMainPage( PageParameters parameters ) {
        super( parameters );

        add( new SetStringForm( "set-string-form" ) );

        add( new Link( "debug-action" ) {
            public void onClick() {
                // DO NOT REMOVE for future everything
                //LuceneTest.addSimulations( getHibernateSession(), (PhetLocalizer) getLocalizer(), getNavMenu() );

                SqlUtils.wrapTransaction( getServletContext(), "SELECT * FROM contribution WHERE contribution_approved = 1", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        logger.info( "#" + result.getInt( "contribution_id" ) + " " + result.getString( "contribution_title" ) );
                        return true;
                    }
                } );
            }
        } );
    }

    public final class SetStringForm extends Form {
        private static final long serialVersionUID = 1L;

        private final ValueMap properties = new ValueMap();

        public SetStringForm( final String id ) {
            super( id );

            add( keyText = new TextField( "key", new PropertyModel( properties, "key" ) ) );
            add( valueText = new TextField( "value", new PropertyModel( properties, "value" ) ) );

            // don't turn <'s and other characters into HTML/XML entities!!!
            valueText.setEscapeModelStrings( false );
        }

        public final void onSubmit() {
            String key = keyText.getModelObjectAsString();
            String value = valueText.getModelObjectAsString();
            logger.info( "Submitted new string: " + key + " = " + value );
            StringUtils.setEnglishString( getHibernateSession(), key, value );
        }
    }

}
