package edu.colorado.phet.website.data.transfer;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SqlResultTask {
    public boolean process( ResultSet result ) throws SQLException;
}
