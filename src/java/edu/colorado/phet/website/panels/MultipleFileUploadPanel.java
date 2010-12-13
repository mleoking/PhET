package edu.colorado.phet.website.panels;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.model.PropertyModel;

import edu.colorado.phet.website.util.PageContext;

public class MultipleFileUploadPanel extends PhetPanel {

    private MultiFileUploadField field;

    private final Collection<FileUpload> uploads = new LinkedList<FileUpload>();

    public MultipleFileUploadPanel( String id, PageContext context ) {
        super( id, context );

        field = new MultiFileUploadField( "uploader", new PropertyModel<Collection<FileUpload>>( this, "uploads" ) );
        ( (MarkupContainer) field.get( "container" ) ).get( "caption" ).setVisible( false );
        add( field );
    }

    public List<FileUpload> getUploadedFiles() {
        LinkedList<FileUpload> ret = new LinkedList<FileUpload>();
        Iterator iter = uploads.iterator();
        while ( iter.hasNext() ) {
            FileUpload fup = (FileUpload) iter.next();
            ret.add( fup );
        }
        return ret;
    }

    public MultiFileUploadField getField() {
        return field;
    }
}
