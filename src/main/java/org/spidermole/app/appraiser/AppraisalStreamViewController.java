/*
 * AppraisalStreamViewController.java
 * 
 * Created: Oct 08, 2020
 */
package org.spidermole.app.appraiser;

import static com.cloudant.client.api.query.Expression.eq;
import static com.cloudant.client.api.query.Expression.exists;
import static com.cloudant.client.api.query.Expression.lte;
import static com.cloudant.client.api.query.Expression.regex;
import static com.cloudant.client.api.query.Operation.and;
import static com.cloudant.client.api.query.Operation.or;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.spidermole.app.AbstractController;
import org.spidermole.app.AppMain;
import org.spidermole.app.db.DbQueryService;
import org.spidermole.model.ResearchItem;
import org.spidermole.util.DbUtils;

import com.cloudant.client.api.query.EmptyExpression;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.Selector;

import javafx.beans.property.ListProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * AppraisalStreamViewController manages the UI updates and background services related to filters for user appraisal
 * streams and the querying of stream data itself from the database.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraisalStreamViewController extends AbstractController
{
	/**
	 * AppraisalStreamLoadedEvent is passed on the AppContext's EventBus to notify the AppraiserRootController that the
	 * stream data has been updated (and to send it said stream data).
	 * 
	 * @author David Schmidt (dschmidt13@gmail.com)
	 */
	class AppraisalStreamLoadedEvent
	{
		// Data members.
		private final List<ResearchItem> fieldStreamContents;

		public AppraisalStreamLoadedEvent( List<ResearchItem> streamContents )
		{
			fieldStreamContents = streamContents;

		} // AppraisalStreamLoadedEvent


		/**
		 * @return the streamContents
		 */
		public List<ResearchItem> getStreamContents( )
		{
			return fieldStreamContents;

		} // getStreamContents

	}

	/**
	 * SortBy specifies ways the user can choose to order stream elements returning from the database. It also provides
	 * the mapping from the display text the user sees to the field name the database sees.
	 * 
	 * @author David Schmidt (dschmidt13@gmail.com)
	 */
	public enum SortBy
	{
		NONE( "None", null ), // FIXME - Remove this and set the default to DATE_FOUND!
		RANDOM( "Random", "createDate" ); // Leave createDate as the default sort field for RANDOM.

		// TODO - Add other sort options once we have indices for them.
		// DATE_FOUND( "Date Found", "createDate" ),

		// Enum data members.
		private String fieldDisplayName;
		private String fieldDatabaseField;

		private SortBy( String displayName, String databaseField )
		{
			fieldDisplayName = displayName;
			fieldDatabaseField = databaseField;

		} // SortBy


		/**
		 * @return the databaseField
		 */
		public String getDatabaseField( )
		{
			return fieldDatabaseField;

		} // getDatabaseField


		/**
		 * @return the displayName
		 */
		public String getDisplayName( )
		{
			return fieldDisplayName;

		} // getDisplayName


		@Override
		public String toString( )
		{
			return fieldDisplayName;

		} // toString

	} // enum SortBy

	// Injected FXML members.
	@FXML
	private TextField fieldFilterText;

	@FXML
	private ComboBox<SortBy> fieldSortBy;

	@FXML
	private Label fieldRemainingLabel;

	@FXML
	private Button fieldGenerateButton;

	// Standard data members.
	private DbQueryService<ResearchItem> fieldStreamService;
	private ListProperty<ResearchItem> fieldStreamItems;
	private int fieldRemainingCount = 0;

	public AppraisalStreamViewController( )
	{
	} // AppraisalStreamViewController


	public void actionLoadStream( )
	{
		fieldLog.debug( "Loading appraisal stream." );

		// Prepare a QueryBuilder with the current options and set it into the service.
		fieldStreamService.setQueryBuilder( generateQueryBuilder( ) );

		fieldStreamService.reset( );
		fieldStreamService.restart( );

	} // actionLoadStream


	@Override
	public void destroy( )
	{
		fieldStreamService.cancel( );

		super.destroy( );

	} // destroy


	private void disableUI( )
	{
		fieldFilterText.setDisable( true );
		fieldSortBy.setDisable( true );
		fieldRemainingLabel.setDisable( true );
		fieldGenerateButton.setDisable( true );

	} // disableUI


	private void enableUI( )
	{
		fieldFilterText.setDisable( false );
		fieldSortBy.setDisable( false );
		fieldRemainingLabel.setDisable( false );
		fieldGenerateButton.setDisable( false );

	} // enableUI


	private QueryBuilder generateQueryBuilder( )
	{
		// LAM - Some of this can probably be put into a utility method...

		List<Selector> andList = new ArrayList<>( );

		// Always make sure we have the right document types.
		andList.add( eq( DbUtils.FIELD_TYPE, ResearchItem.DATABASE_TYPE ) );

		// FIXME - Need to expose this to UI somehow! (But right now, these are all I care about.)
		andList.add( eq( "category", "Systems Biology" ) );

		// Exclude documents that already have an appraisal vote.
		andList.add( or( exists( "noVotes", false ), lte( "noVotes", Integer.valueOf( 0 ) ) ) );
		andList.add( or( exists( "yesVotes", false ), lte( "yesVotes", Integer.valueOf( 0 ) ) ) );

		// If there's filter text, filter the title using regex.
		if ( !StringUtils.isEmpty( fieldFilterText.getText( ) ) )
			{
			String regex = "^.*?" + fieldFilterText.getText( ) + ".*$";
			andList.add( regex( "title", regex ) );
			}

		// These must be combined intelligently. The database doesn't like "and" expressions with only one operand (or
		// presumably with zero), and the API doesn't clean this up for us.
		Selector compoundSelector = ( andList.size( ) == 0 ) ? EmptyExpression.empty( )
				: ( andList.size( ) == 1 ) ? andList.get( 0 )
				: and( andList.toArray( new Selector[ andList.size( ) ] ) );

		// Generate the builder with the final choice of selector.
		QueryBuilder builder = new QueryBuilder( compoundSelector );

		// Raise query size limit past the Karman line!
		builder.limit( 10000 );

		// Set sort field.
		// FIXME - The table needs to be indexed for that to work. Not important right now.
		// builder.sort( Sort.asc( fieldSortBy.getValue( ).getDatabaseField( ) ) );

		return builder;

	} // generateQueryBuilder


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

		/*
		 * Initialize UI stuff first.
		 */
		updateRemainingCountLabel( );
		fieldSortBy.getItems( ).addAll( SortBy.values( ) );
		fieldSortBy.setValue( SortBy.NONE );

		/*
		 * Initialize service stuff.
		 */
		fieldStreamService = new DbQueryService<>( ResearchItem.class );
		fieldStreamService.setOnRunning( this::onStreamServiceStarted );
		fieldStreamService.setOnSucceeded( this::onStreamServiceSuccess );
		fieldStreamService.setOnFailed( this::onStreamServiceFailure );
		fieldStreamService.setOnCancelled( this::onStreamServiceCancel );

		// Attempt to load a default stream.
		actionLoadStream( );

	} // initialize


	public void notifyStreamItemConsumed( )
	{
		fieldRemainingCount-- ;
		updateRemainingCountLabel( );

	} // notifyStreamItemConsumed


	private void onStreamServiceCancel( WorkerStateEvent event )
	{
		fieldLog.debug( "AppraisalStreamService cancelled." );

		enableUI( );

	} // onStreamServiceCancel


	private void onStreamServiceFailure( WorkerStateEvent event )
	{
		fieldLog.error( "AppraisalStreamService failed!", event.getSource( ).getException( ) );

		enableUI( );

	} // onStreamServiceFailure


	private void onStreamServiceStarted( WorkerStateEvent event )
	{
		fieldLog.debug( "AppraisalStreamService started." );

		disableUI( );

	} // onStreamServiceStarted


	private void onStreamServiceSuccess( WorkerStateEvent event )
	{
		fieldLog.debug( "AppraisalStreamService successful." );

		// Note: This is the only type that can be returned, since it's the object type of the service.
		@SuppressWarnings( "unchecked" )
		List<ResearchItem> results = ( List<ResearchItem> ) event.getSource( ).getValue( );

		// Set the label.
		fieldRemainingCount = results.size( );
		updateRemainingCountLabel( );

		// Special case: Are we sorting randomly? (The db query won't do that for us.)
		if ( SortBy.RANDOM.equals( fieldSortBy.getValue( ) ) )
			Collections.shuffle( results );

		// Report the stream results to the parent component in the scene graph.
		AppMain.contextInstance( ).getEventBus( ).post( new AppraisalStreamLoadedEvent( results ) );

		enableUI( );

	} // onStreamServiceSuccess


	/**
	 * @param streamItems the streamItems to set
	 */
	public void setStreamItems( ListProperty<ResearchItem> streamItems )
	{
		fieldStreamItems = streamItems;

	} // setStreamItems


	private void updateRemainingCountLabel( )
	{
		String remainingText = "Remaining: " + fieldRemainingCount;

		fieldRemainingLabel.setText( remainingText );

	} // updateRemainingCount

}
