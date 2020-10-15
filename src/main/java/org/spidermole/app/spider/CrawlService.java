/*
 * CrawlService.java
 * 
 * Created: Oct 14, 2020
 */
package org.spidermole.app.spider;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import org.apache.juneau.rest.client2.RestClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spidermole.model.ResearchItem;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * CrawlService takes the following steps to acquire and store new ResearchItem documents:
 * <ol>
 * <li>Looks up a domain-specific {@link CrawlCorrespondent} instance;
 * <li>Requests the initial page at the given URL;
 * <li>Hands the content to the correspondent to convert the contents of the page into {@code ResearchItem} objects
 * populated with metadata from the page, as appropriate;
 * <li>Passes the parsed contents to a concurrent queue provided at construction time, in order for the
 * perpetually-running/waiting {@link CrawlPersistenceService} to receive and (safely) post them to the database;
 * <li>Requests the "next" URL from the parser, given the current URL and content;
 * <li>If the next URL is non-null, waits for a delay determined by the domain-specific parser;
 * <li>Repeats the cycle using the next URL if it is non-null.
 * </ol>
 * The value returned is the number of documents that were ultimately
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class CrawlService extends Service<Integer>
{
	// Log instance.
	private static final Logger LOG = LogManager.getLogger( CrawlService.class );

	/**
	 * This map contains one immutable instance of each known {@link CrawlCorrespondent} implementation, keyed by the
	 * exact domain string of the site it knows (e.g., {@code "api.biorxiv.org"}).
	 */
	private static final Map<String, CrawlCorrespondent> CORRESPONDENTS = new HashMap<>( );

	/*
	 * We need to initialize a static correspondent table. Here is fine for now.
	 */
	static
		{
		CORRESPONDENTS.put( BiorxivApiCorrespondent.DOMAIN_HOST, new BiorxivApiCorrespondent( ) );
		}

	// Data members.
	private final BlockingQueue<ResearchItem> fieldPersistQueue;
	private URL fieldUrl;

	public CrawlService( BlockingQueue<ResearchItem> persistQueue )
	{
		Objects.requireNonNull( persistQueue, "A queue to accept documents to be persisted is required." );

		fieldPersistQueue = persistQueue;

	} // CrawlService


	@Override
	protected Task<Integer> createTask( )
	{
		final URL baseUrl = fieldUrl;

		return new Task<>( )
		{
			@Override
			protected Integer call( ) throws Exception
			{
				Objects.requireNonNull( baseUrl, "A starting URL to crawl must be provided!" );

				// Look up a correspondent by domain.
				CrawlCorrespondent correspondent = CORRESPONDENTS.get( baseUrl.getHost( ) );
				if ( correspondent == null )
					{
					throw new Exception( "No known correspondent exists to report on the domain of the site/API at '"
							+ baseUrl.getHost( ) + "'!" );
					}

				// Prepare to crawl.
				int totalDiscovered = 0;
				URI nextUri = baseUrl.toURI( );

				// Crawl once with the initial URL. (Small loop unwind to work in the delay.)
				totalDiscovered += crawl( nextUri, correspondent );
				LOG.debug( "Discovered " + totalDiscovered + " new documents from '" + nextUri + "'." );
				nextUri = correspondent.getNextPageUri( nextUri );

				// Continue crawling as long as necessary (politely - wait between requests).
				while ( nextUri != null )
					{
					Thread.sleep( 1000 * correspondent.getCrawlDelaySeconds( ) );
					int discovered = crawl( nextUri, correspondent );

					LOG.debug( "Discovered " + discovered + " new documents from '" + nextUri + "'." );

					totalDiscovered += discovered;
					if ( discovered > 0 )
						nextUri = correspondent.getNextPageUri( nextUri );
					}

				return Integer.valueOf( totalDiscovered );

			} // call


			private int crawl( URI uri, CrawlCorrespondent correspondent ) throws Exception
			{
				try
					{
					// Perform the request using the slightly overzealous Apache Juneau - Rest Client (2) API.
					String responseBody;
					try ( RestClient client = RestClient.create( ).universal( ).build( ); )
						{
						responseBody = client.get( uri ).run( ).assertStatus( ).code( ).is( 200 ).getBody( )
								.asString( );
						}

					// If the request was successful, convert the response content into a list of properly populated
					// ResearchItems.
					List<ResearchItem> discoveredItems = correspondent.extractResearchItems( responseBody );

					// Pass the results to the persistence service. This is done individually, as we may need to wait
					// for space to become available in the queue before we continue.
					for ( ResearchItem item : discoveredItems )
						fieldPersistQueue.put( item );

					return discoveredItems.size( );
					}
				catch ( AssertionError assertionError )
					{
					// Okay, wow Apache. AssertionError is a little strong here. Nevermind, though; we'll soak it. Just
					// means the page didn't load as expected.
					LOG.error( "Failed to crawl URI '" + uri + "'.", assertionError );

					throw new Exception( "Failed to crawl URI '" + uri + "'.", assertionError );
					}
				catch ( InterruptedException exception )
					{
					LOG.error( "Interrupted while attempting to enqueue discovered items from URI '" + "'!" );

					// Restore the interrupt.
					Thread.currentThread( ).interrupt( );

					throw new Exception( "Interrupted while attempting to enqueue discovered items from URI '" + "'!" );
					}

			} // crawl

		};

	} // createTask


	/**
	 * @param url the url to set
	 */
	public void setUrl( URL url )
	{
		fieldUrl = url;

	} // setUrl

}
