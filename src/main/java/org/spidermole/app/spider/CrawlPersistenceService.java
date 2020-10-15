/*
 * CrawlPersistenceService.java
 * 
 * Created: Oct 14, 2020
 */
package org.spidermole.app.spider;

import static com.cloudant.client.api.query.Expression.eq;
import static com.cloudant.client.api.query.Operation.and;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spidermole.app.Constants;
import org.spidermole.model.ResearchItem;
import org.spidermole.util.DbUtils;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Document;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.QueryResult;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * CrawlPersistenceService remains running for the lifetime of the CrawlControlPanel and awaits {@link Document}s to
 * persist being added to its queue. A thread-safe queue must be provided in the service constructor. The service is
 * sensitive to interrupts and task cancellation.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class CrawlPersistenceService extends Service<Void>
{
	// Log instance.
	private static final Logger LOG = LogManager.getLogger( CrawlPersistenceService.class );

	// Data members.
	private final BlockingQueue<ResearchItem> fieldQueue;

	public CrawlPersistenceService( BlockingQueue<ResearchItem> queue )
	{
		Objects.requireNonNull( queue, "A queue must be provided." );
		fieldQueue = queue;

	} // CrawlPersistenceService


	private static int compareVersions( ResearchItem original, ResearchItem newItem )
	{
		// Attempt to parse the versions. Default to using the new value (returning negative) if we fail.
		if ( original.getVersion( ) == null || newItem.getVersion( ) == null )
			return -1;
		else
			return ( original.getVersion( ).intValue( ) - newItem.getVersion( ).intValue( ) );

	} // compareVersions


	@Override
	protected Task<Void> createTask( )
	{
		return new Task<>( )
		{
			@Override
			protected Void call( ) throws Exception
			{
				Database database = DbUtils.buildDefaultClient( ).database( Constants.DATABASE_DBNAME, true );

				while ( !isCancelled( ) )
					{
					/*
					 * We could write this to run on batches, but since we still have to search for individual
					 * duplicates, that becomes slightly more complicated (and is ultimately less of a performance gain
					 * than it would otherwise be). So for now, we'll stick to dequeueing, checking, and posting one
					 * document at a time. (It may well still outpace the producer.)
					 */
					try
						{
						// Await a document arriving. This will spend a lot of time waiting over the service's lifetime.
						// It will eventually throw InterruptedException when the service is cancelled.
						ResearchItem item = fieldQueue.take( );

						// Search for an existing record by DOI. If we have one, we'll overwrite it with the new values
						// from the server (by stealing its id and rev).
						String dupQuery = new QueryBuilder( and( eq( DbUtils.FIELD_TYPE, ResearchItem.DATABASE_TYPE ),
								eq( "doi", item.getDOI( ) ) ) ).build( );
						QueryResult<ResearchItem> dupResult = database.query( dupQuery, ResearchItem.class );
						if ( !dupResult.getDocs( ).isEmpty( ) )
							{
							ResearchItem original = dupResult.getDocs( ).get( 0 );

							// Special case: Don't persist anything if the version we have stored is the same or
							// greater. Just move on.
							if ( compareVersions( original, item ) >= 0 )
								continue;

							item.setId( original.getId( ) );
							item.setRevision( original.getRevision( ) );
							item.setCreateDate( original.getCreateDate( ) );

							// Also preserve existing votes. This is a terrible place to do this, but that's mainly a
							// consequence of the way votes are implemented (and they aren't going to change for now).
							item.setYesVotes( original.getYesVotes( ) );
							item.setNoVotes( original.getNoVotes( ) );
							}

						// Persist the item.
						Response response;
						if ( item.getRevision( ) == null )
							response = database.post( item );
						else
							response = database.update( item );

						if ( response.getError( ) != null )
							{
							LOG.error( "Error persisting document with DOI '" + item.getDOI( ) + "': "
									+ response.getError( ) );
							}
						}
					catch ( InterruptedException exception )
						{
						LOG.debug( "Interrupted while awaiting documents to persist. Service task has "
								+ ( isCancelled( ) ? "" : "not " ) + "been cancelled." );

						// Restore the interrupt only if we're cancelled. (We're basically sure to be cancelled here,
						// anyway, but restoring the interrupt would come with a high performance penalty otherwise.)
						if ( isCancelled( ) )
							Thread.currentThread( ).interrupt( );
						}
					}

				return null;

			} // call


		};

	} // createTask


}
