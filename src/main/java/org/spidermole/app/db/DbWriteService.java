/*
 * DbWriteService.java
 * 
 * Created: Oct 14, 2020
 */
package org.spidermole.app.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spidermole.app.Constants;
import org.spidermole.model.ModelDocument;
import org.spidermole.util.DbUtils;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Document;
import com.cloudant.client.api.model.Response;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * DbWriteService a general-purpose write service capable of writing (creating or updating) a model object extending
 * {@link Document} to the database. Note that the {@code document} field is cleared as soon as the service begins
 * (regardless of success or failure).
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class DbWriteService extends Service<Response>
{
	// Class constants.
	private static final Logger LOG = LogManager.getLogger( DbWriteService.class );

	// Data members.
	private ModelDocument fieldDocument;

	public DbWriteService( )
	{
	} // DbWriteService


	@Override
	protected Task<Response> createTask( )
	{
		final ModelDocument doc = fieldDocument;
		fieldDocument = null;

		return new Task<>( )
		{
			@Override
			protected Response call( ) throws Exception
			{
				// Get a reference to the database.
				Database db = DbUtils.buildDefaultClient( ).database( Constants.DATABASE_DBNAME, true );

				// Create or update the document-based object.
				Response response;
				if ( doc.getRevision( ) == null )
					response = db.post( doc );
				else
					response = db.update( doc );

				if ( response.getError( ) != null )
					LOG.error( "Failed to write to the database: " + response.getError( ) );

				return response;

			} // call
		};

	} // createTask


	/**
	 * @param document the document to set
	 */
	public void setDocument( ModelDocument document )
	{
		fieldDocument = document;

	} // setDocument

}
