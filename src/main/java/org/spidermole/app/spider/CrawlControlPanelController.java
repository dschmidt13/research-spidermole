/*
 * CrawlControlPanelController.java
 * 
 * Created: Oct 14, 2020
 */
package org.spidermole.app.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.spidermole.app.AbstractController;
import org.spidermole.model.ResearchItem;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * CrawlControlPanelController is the controller for {@code CrawlControlPanel.fxml}. Its services perform research item
 * metadata lookup via API or (respectful) scraping, depending on the domain.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class CrawlControlPanelController extends AbstractController
{
	/*
	 * Implementation note: The persistence in this is somewhat loosely-coupled, has no real backup plans if things fail
	 * or are dropped upon app closing, and offers no guarantees about retaining everything that gets downloaded.
	 */

	// Injected view members.
	@FXML
	private TextField fieldUrl;

	// Data members.
	private CrawlService fieldCrawlService;
	private CrawlPersistenceService fieldPersistenceService;

	public CrawlControlPanelController( )
	{
	} // CrawlControlPanelController


	public void actionCrawl( )
	{
		if ( !fieldUrl.getText( ).isEmpty( ) )
			{
			try
				{
				// Set the crawl URI and start it.
				fieldCrawlService.setUrl( new URL( fieldUrl.getText( ) ) );
				fieldCrawlService.restart( );
				}
			catch ( MalformedURLException exception )
				{
				fieldLog.error(
						"Couldn't convert the crawl URL string '" + fieldUrl.getText( ) + "' into a URL instance.",
						exception );
				}
			}

	} // actionCrawl


	@Override
	public void destroy( )
	{
		fieldCrawlService.cancel( );
		fieldPersistenceService.cancel( );

		super.destroy( );

	} // destroy


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

		// Initialize the background persistence service, and start it. It will run until the controller is destroyed.
		BlockingQueue<ResearchItem> persistQueue = new ArrayBlockingQueue<>( 500 );
		fieldPersistenceService = new CrawlPersistenceService( persistQueue );
		fieldPersistenceService.setOnRunning( this::onPersistenceServiceRunning );
		fieldPersistenceService.setOnSucceeded( this::onPersistenceServiceStopped );
		fieldPersistenceService.setOnCancelled( this::onPersistenceServiceStopped );
		fieldPersistenceService.setOnFailed( this::onPersistenceServiceStopped );
		fieldPersistenceService.restart( );

		// Initialize the crawl service.
		fieldCrawlService = new CrawlService( persistQueue );
		fieldCrawlService.setOnRunning( this::onCrawlServiceRunning );
		fieldCrawlService.setOnSucceeded( this::onCrawlServiceSucceeded );
		fieldCrawlService.setOnCancelled( this::onCrawlServiceCancelled );
		fieldCrawlService.setOnFailed( this::onCrawlServiceFailed );

	} // initialize


	private void onCrawlServiceCancelled( WorkerStateEvent event )
	{
		fieldLog.debug( "Crawl service cancelled." );

	} // onCrawlServiceCancelled


	private void onCrawlServiceFailed( WorkerStateEvent event )
	{
		fieldLog.error( "Crawl service failed!", event.getSource( ).getException( ) );

	} // onCrawlServiceFailed


	private void onCrawlServiceRunning( WorkerStateEvent event )
	{
		fieldLog.debug( "Beginning crawl over URL '" + fieldUrl.getText( ) + "'." );

	} // onCrawlServiceRunning


	private void onCrawlServiceSucceeded( WorkerStateEvent event )
	{
		fieldLog.debug( "Crawl service completed. " + event.getSource( ).getValue( ) + " results discovered." );

	} // onCrawlServiceSucceeded


	private void onPersistenceServiceRunning( WorkerStateEvent event )
	{
		fieldLog.debug( "Persistence service running." );

	} // onPersistenceServiceRunning


	private void onPersistenceServiceStopped( WorkerStateEvent event )
	{
		fieldLog.debug( "Persistence service stopped." );

		// Shouldn't happen; not worth its own method.
		if ( event.getSource( ).getException( ) != null )
			{
			fieldLog.error( "Persistence service halted unexpectedly due to exception!",
					event.getSource( ).getException( ) );
			}

	} // onPersistenceServiceStopped

}
