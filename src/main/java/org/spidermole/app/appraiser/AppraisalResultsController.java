/*
 * AppraisalResultsController.java
 * 
 * Created: Oct 11, 2020
 */
package org.spidermole.app.appraiser;

import static com.cloudant.client.api.query.Expression.eq;
import static com.cloudant.client.api.query.Expression.gt;
import static com.cloudant.client.api.query.Operation.and;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.spidermole.app.AbstractController;
import org.spidermole.app.appraiser.AppraisalSummary.AuthorStats;
import org.spidermole.app.db.DbQueryService;
import org.spidermole.model.ResearchItem;
import org.spidermole.util.DbUtils;

import com.cloudant.client.api.query.QueryBuilder;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * AppraisalResultsController manages the services related to loading, computing, and storing results, as well as
 * presentation logic for presenting them.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraisalResultsController extends AbstractController
{
	// Injected FXML members.
	@FXML
	private ListView<String> fieldTopAuthors;

	// Standard data members.
	private DbQueryService<ResearchItem> fieldVoteLookupService;
	private AppraisalSummaryService fieldSummaryService;

	public AppraisalResultsController( )
	{
	} // AppraisalResultsController


	public void actionLookupVotedResearch( )
	{
		fieldLog.debug( "Looking up ResearchItems that have been voted on." );

		fieldVoteLookupService.reset( );
		fieldVoteLookupService.restart( );

	} // actionLookupVotedResearch


	public void actionSummarizeResults( )
	{
		fieldLog.debug( "Generating a summary of ResearchItem appraisals." );

		fieldSummaryService.reset( );
		fieldSummaryService.restart( );

	} // actionSummarizeResults


	@Override
	public void destroy( )
	{
		fieldVoteLookupService.cancel( );
		fieldSummaryService.cancel( );

		super.destroy( );

	} // destroy


	private QueryBuilder generateQueryBuilder( )
	{
		QueryBuilder builder = new QueryBuilder(
				and( eq( DbUtils.FIELD_TYPE, ResearchItem.DATABASE_TYPE ), gt( "yesVotes", 0 ) ) );
		builder.limit( 5000 );

		return builder;

	} // generateQueryBuilder


	private void handleLookupCancellation( WorkerStateEvent event )
	{
		// Whatever.
		fieldLog.debug( "Voted ResearchItem lookup was cancelled." );

	} // handleLookupCancellation


	private void handleLookupFailure( WorkerStateEvent event )
	{
		fieldLog.error( "Lookup of voted ResearchItems has failed!", event.getSource( ).getException( ) );

	} // handleLookupFailure


	private void handleLookupRunning( WorkerStateEvent event )
	{
		// Whatever.
		fieldLog.debug( "Voted ResearchItem lookup is running." );

	} // handleLookupRunning


	private void handleLookupSuccess( WorkerStateEvent event )
	{
		List<ResearchItem> results = fieldVoteLookupService.getValue( );

		fieldLog.debug( "Lookup of voted ResearchItems succeeded. " + results.size( ) + " documents found." );

		// Pipe them straight into the next service for processing.
		fieldSummaryService.setItems( results );
		actionSummarizeResults( );

	} // handleLookupSuccess


	private void handleSummaryCancellation( WorkerStateEvent event )
	{
		// Whatever.
		fieldLog.debug( "Voted ResearchItem summary was cancelled." );

	} // handleSummaryCancellation


	private void handleSummaryFailure( WorkerStateEvent event )
	{
		fieldLog.error( "Summarizing voted ResearchItems has failed!", event.getSource( ).getException( ) );

	} // handleSummaryFailure


	private void handleSummaryRunning( WorkerStateEvent event )
	{
		// Whatever.
		fieldLog.debug( "Voted ResearchItem summary is running." );

	} // handleSummaryRunning


	private void handleSummarySuccess( WorkerStateEvent event )
	{
		fieldLog.debug( "Summary service completed." );

		updateSummary( fieldSummaryService.getValue( ) );

	} // handleSummarySuccess


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

		// Initialize the lookup service for items that have been voted on. Note that we only need one copy of the
		// QueryBuilder, since the same query should be running each time this service runs (if more than once).
		fieldVoteLookupService = new DbQueryService<>( ResearchItem.class );
		fieldVoteLookupService.setOnCancelled( this::handleLookupCancellation );
		fieldVoteLookupService.setOnSucceeded( this::handleLookupSuccess );
		fieldVoteLookupService.setOnFailed( this::handleLookupFailure );
		fieldVoteLookupService.setOnRunning( this::handleLookupRunning );
		fieldVoteLookupService.setQueryBuilder( generateQueryBuilder( ) );

		// Initialize the summary service.
		fieldSummaryService = new AppraisalSummaryService( );
		fieldSummaryService.setOnCancelled( this::handleSummaryCancellation );
		fieldSummaryService.setOnSucceeded( this::handleSummarySuccess );
		fieldSummaryService.setOnFailed( this::handleSummaryFailure );
		fieldSummaryService.setOnRunning( this::handleSummaryRunning );

		// Kick off the services with a lookup!
		fieldVoteLookupService.restart( );

	} // initialize


	private void updateSummary( AppraisalSummary summary )
	{
		// FIXME - We should actually do most of this with JavaFX bindings.
		List<AuthorStats> authorList = summary.getAuthorStatsByYes( );

		// Build some cheap summaries of the most popular authors.
		List<String> authorSummaries = authorList.stream( ).limit( 20 ).filter( ( stats ) -> stats.getYesCount( ) > 0 )
				.map( ( stats ) -> stats.getAuthor( ) + " (" + stats.getYesCount( ) + ")" )
				.collect( Collectors.toList( ) );
		fieldTopAuthors.getItems( ).setAll( authorSummaries );

	} // updateSummary

}
