package edu.colorado.phet.website.admin;

import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminCategoryPage extends AdminPage {
    private Category category;

    public AdminCategoryPage( PageParameters parameters ) {
        super( parameters );

        final int categoryId = parameters.getInt( "categoryId" );

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                category = (Category) session.load( Category.class, categoryId );
                return true;
            }
        } );

        add( new LocalizedText( "category-title", category.getNavLocation( getNavMenu() ).getLocalizationKey() ) );
    }
}
