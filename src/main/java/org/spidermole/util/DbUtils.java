/*
 * DbUtils.java
 * 
 * Created: Oct 10, 2020
 */
package org.spidermole.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spidermole.app.Constants;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.query.Operation;
import com.cloudant.client.api.query.QueryBuilder;

/**
 * DbUtils provides some standardizing wrapping around the Cloudant API, as it is used in this app.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class DbUtils
{
	// Class constants.
	private static final Logger LOG = LogManager.getLogger( DbUtils.class );

	/**
	 * The name of the database document field defining what type of document it is. ALWAYS include this using
	 * {@link Operation#and(com.cloudant.client.api.query.Selector...)} in the selector for queries made with
	 * {@link QueryBuilder}.
	 */
	public static final String FIELD_TYPE = "type";

	private DbUtils( )
	{
		throw new AssertionError( "Cannot instantiate this class." );

	} // DbUtils


	public static CloudantClient buildDefaultClient( )
	{
		CloudantClient client = null;

		try
			{
			client = ClientBuilder.url( new URL( Constants.DATABASE_URL ) ).username( Constants.DATABASE_USERNAME )
					.password( Constants.DATABASE_PASSWORD ).build( );
			}
		catch ( MalformedURLException exception )
			{
			// This should never happen, but if this ever becomes dynamic we'll have a direction to grow.
			LOG.error( "Unable to convert the DATABASE_URL constant into a URL!", exception );
			}

		return client;

	} // getDefaultClient

}
