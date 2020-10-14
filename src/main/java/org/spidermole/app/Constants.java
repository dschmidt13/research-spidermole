/*
 * Constants.java
 * 
 * Created: Oct 09, 2020
 */
package org.spidermole.app;

/**
 * Constants defines immutable public values that may be used in various places throughout the app.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class Constants
{
	// Database constants.
	public static final String DATABASE_URL = "http://localhost:5984";
	public static final String DATABASE_USERNAME = "admin";
	public static final String DATABASE_PASSWORD = "admin";
	public static final String DATABASE_DBNAME = "spidermole";

	private Constants( )
	{
		throw new AssertionError( "Cannot instantiate this class." );

	} // Constants

}
