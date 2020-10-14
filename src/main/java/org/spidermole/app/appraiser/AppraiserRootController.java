/*
 * AppraiserRootController.java
 * 
 * Created: Oct 07, 2020
 */
package org.spidermole.app.appraiser;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.ResourceBundle;

import org.spidermole.app.AbstractController;
import org.spidermole.app.AppMain;
import org.spidermole.app.appraiser.AppraisalStreamViewController.AppraisalStreamLoadedEvent;
import org.spidermole.app.appraiser.SwipeView.AppraisalSwipeEvent;
import org.spidermole.app.db.DbWriteService;
import org.spidermole.model.ResearchItem;

import com.google.common.eventbus.Subscribe;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * AppraiserRootController orchestrates the dynamics between the three pieces of the research item appraisal process:
 * loading discrete items from the database, presenting items to the user for a yes/no vote, and summarizing the total
 * current and past vote results. Subcontrollers handle the details of each step, while this controller (aside from
 * accompanying the UI containers needed) keeps track of the process flow outlined above and ensures that the three
 * pieces communicate properly.
 * <p>
 * Information that needs to be sent to subcontrollers/components is sent directly using APIs that the components
 * provide (as this controller already owns references to them). Information that needs to be sent back upstream,
 * resulting from work that the subcontroller/component did, is posted to the AppContext's EventBus and handled by a
 * listener in this controller. This prevents circular references, and exposes the events to potentially other future
 * components that may wish to report on them.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraiserRootController extends AbstractController
{
	// Injected FXML view members.
	@FXML
	private BorderPane fieldStreamViewContainer;

	@FXML
	private SwipeView fieldSwipeView;

	@FXML
	private BorderPane fieldResultViewContainer;

	// Standard data members.
	private DbWriteService fieldVoteService;
	private AppraisalStreamViewController fieldStreamViewController;
	private AppraisalResultsController fieldResultViewController;
	private Deque<ResearchItem> fieldItemDeque = new ArrayDeque<>( );

	public AppraiserRootController( )
	{
	} // AppraiserRootController


	public void actionNextItem( )
	{
		fieldSwipeView.setResearchItem( fieldItemDeque.poll( ) );

	} // actionNextItem


	@Override
	public void destroy( )
	{
		try
			{
			fieldStreamViewController.destroy( );
			}
		catch ( Exception exception )
			{
			fieldLog.error( "Exception destroying the child StreamViewController.", exception );
			}

		try
			{
			fieldResultViewController.destroy( );
			}
		catch ( Exception exception )
			{
			fieldLog.error( "Exception destroying the child AppraisalResultsController.", exception );
			}

		// Hopefully we're not in the middle of something... But re-voting on one item is probably not a huge waste.
		fieldVoteService.cancel( );

		// Unregister from the app's EventBus.
		AppMain.contextInstance( ).getEventBus( ).unregister( this );

		super.destroy( );

	} // destroy


	@Subscribe
	public void handleAppraisalSwipeEvent( AppraisalSwipeEvent event )
	{
		// Update the document and persist the vote.
		ResearchItem item = event.getItem( );
		if ( event.isInterested( ) )
			{
			item.setYesVotes( item.getYesVotes( ) == null ? Integer.valueOf( 1 )
					: Integer.valueOf( item.getYesVotes( ).intValue( ) + 1 ) );
			}
		else
			{
			item.setNoVotes( item.getNoVotes( ) == null ? Integer.valueOf( 1 )
					: Integer.valueOf( item.getNoVotes( ).intValue( ) + 1 ) );
			}
		fieldVoteService.setDocument( item );
		fieldVoteService.restart( );

		// We'll make sure the vote was successful (and can't be cast twice) before we prepare a new item to swipe on.
		// So for now, we'll send a null to the view. This will prevent service interruption.
		fieldSwipeView.setResearchItem( null );

		// We will, however, decrement the counter immediately.
		fieldStreamViewController.notifyStreamItemConsumed( );

	} // handleAppraisalSwipeEvent


	@Subscribe
	public void handleStreamLoadedEvent( AppraisalStreamLoadedEvent event )
	{
		fieldItemDeque.clear( );
		fieldItemDeque.addAll( event.getStreamContents( ) );

		// Present the first research item.
		actionNextItem( );

	} // onListChange


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

		// Register on the app's EventBus so our children can send us events without something even *more* complicated
		// than this power tool.
		AppMain.contextInstance( ).getEventBus( ).register( this );

		// Initialize the voting service (before child UIs).
		fieldVoteService = new DbWriteService( );
		fieldVoteService.setOnRunning( this::onVoteRunning );
		fieldVoteService.setOnSucceeded( this::onVoteSuccess );
		fieldVoteService.setOnCancelled( this::onVoteCancellation );
		fieldVoteService.setOnFailed( this::onVoteFailure );

		// Initialize stream view.
		try
			{
			FXMLLoader loader = new FXMLLoader( getClass( ).getResource( "AppraisalStreamView.fxml" ) );
			Node view = loader.load( );
			fieldStreamViewController = loader.getController( );
			fieldStreamViewContainer.setCenter( view );
			}
		catch ( Exception exception )
			{
			fieldLog.error( "Failed to initialize appraisal stream view.", exception );
			}

		// Initialize results view.
		try
			{
			FXMLLoader loader = new FXMLLoader( getClass( ).getResource( "AppraisalResultsView.fxml" ) );
			Node view = loader.load( );
			fieldResultViewController = loader.getController( );
			fieldResultViewContainer.setCenter( view );
			}
		catch ( Exception exception )
			{
			fieldLog.error( "Failed to initialize appraisal results view.", exception );
			}

	} // initialize


	private void onVoteCancellation( WorkerStateEvent event )
	{
		fieldLog.debug( "The vote service has been cancelled." );

	} // onVoteCancellation


	private void onVoteFailure( WorkerStateEvent event )
	{
		// We can get away with this for now. It's not really a big deal if we lose a vote here. If we start losing a
		// bunch, it's likely a bug or service disruption, but those items will come back around in the stream if it's
		// reloaded for another chance.
		fieldLog.error( "Vote persisting failed!", event.getSource( ).getException( ) );

		// But still try the next one!
		actionNextItem( );

	} // onVoteFailure


	private void onVoteRunning( WorkerStateEvent event )
	{
		fieldLog.debug( "A vote is being cast." );

	} // onVoteRunning


	private void onVoteSuccess( WorkerStateEvent event )
	{
		fieldLog.debug( "Vote cast successfully." );

		// Now that it's part of the database, we can include it in the official results.
		fieldResultViewController.actionLookupVotedResearch( );

		// Ready for more.
		actionNextItem( );


	} // onVoteSuccess

}
