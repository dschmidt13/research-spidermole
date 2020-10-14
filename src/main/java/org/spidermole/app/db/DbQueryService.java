/*
 * DbQueryService.java
 * 
 * Created: Oct 11, 2020
 */
package org.spidermole.app.db;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spidermole.app.Constants;
import org.spidermole.util.DbUtils;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.QueryResult;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * DbQueryService is a general-purpose service for querying the database using a QueryBuilder.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class DbQueryService<T> extends Service<List<T>>
{
	// Class constants.
	private static final Logger LOG = LogManager.getLogger( DbQueryService.class );

	// Data members.
	private QueryBuilder fieldQueryBuilder;
	private final Class<T> fieldResultType;

	public DbQueryService( Class<T> resultType )
	{
		Objects.requireNonNull( resultType, "A resultType is required to run the service." );
		fieldResultType = resultType;

	} // DbQueryService


	@Override
	protected Task<List<T>> createTask( )
	{
		// This is what the caller sets to control the query.
		final String queryString = fieldQueryBuilder.build( );

		return new Task<>( )
		{
			@Override
			protected List<T> call( ) throws Exception
			{
				// Obtain a handle to the database. Create db if necessary.
				Database db = DbUtils.buildDefaultClient( ).database( Constants.DATABASE_DBNAME, true );

				// Run the query.
				QueryResult<T> queryResult = db.query( queryString, fieldResultType );

				// Log results.
				if ( queryResult.getWarning( ) != null )
					LOG.warn( "Query '" + queryString + "' returned warning: " + queryResult.getWarning( ) );

				if ( queryResult.getExecutionStats( ) != null )
					{
					LOG.debug( "Query '" + queryString + "' returned "
							+ queryResult.getExecutionStats( ).getResultsReturned( ) + " docs in "
							+ queryResult.getExecutionStats( ).getExecutionTimeMs( ) + "ms. ("
							+ queryResult.getExecutionStats( ).getTotalDocsExamined( ) + " docs examined; "
							+ queryResult.getExecutionStats( ).getTotalKeysExamined( ) + " keys examined; "
							+ queryResult.getExecutionStats( ).getTotalQuorumDocsExamined( )
							+ " total quorum docs examined.)" );
					}

				return queryResult.getDocs( );

			} // call
		};

	} // createTask


	/**
	 * @param queryBuilder the queryBuilder to set
	 */
	public void setQueryBuilder( QueryBuilder queryBuilder )
	{
		fieldQueryBuilder = queryBuilder;

	} // setQueryBuilder

}
