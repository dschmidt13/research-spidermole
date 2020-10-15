/*
 * BiorxivApiCorrespondent.java
 * 
 * Created: Oct 15, 2020
 */
package org.spidermole.app.spider;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spidermole.model.ResearchItem;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * BiorxivApiCorrespondent implements {@link CrawlCorrespondent} for some {@code api.biorxiv.org} requests.
 * <p>
 * <b>Please note:</b> This correspondent only supports the first "Content Detail" endpoint, i.e.
 * {@code https://api.biorxiv.org/details/[server]/[interval]/[cursor]/[format]}.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class BiorxivApiCorrespondent implements CrawlCorrespondent
{
	// Class constants.
	private static final Logger LOG = LogManager.getLogger( BiorxivApiCorrespondent.class );

	public static final String DOMAIN_HOST = "api.biorxiv.org";

	/**
	 * Expression: <pre><code>
	 * ^https://api\.biorxiv\.org/details/biorxiv/(?:(?:(\d{4}-\d{2}-\d{2})/(\d{4}-\d{2}-\d{2}))|(\d+))/(\d+)$
	 * </code></pre> Groups:
	 * <ol>
	 * <li>yyyy-MM-dd formatted date declaring the beginning of the search range. May be absent.
	 * <li>yyyy-MM-dd formatted date declaring the end of the search range. Will be present when Group 1 is present.
	 * <li>Integer indicating the position offset into the data set. This will only be present when groups 1 and 2 are
	 * absent.
	 * <li>Integer indicating the cursor, i.e. offset value of the requested data.
	 * </ol>
	 * <p>
	 */
	private static final Pattern PAGE_URI_PATTERN = Pattern.compile(
			"^https://api\\.biorxiv\\.org/details/biorxiv/(?:(?:(\\d{4}-\\d{2}-\\d{2})/(\\d{4}-\\d{2}-\\d{2}))|(\\d+))/(\\d+)$" );

	public BiorxivApiCorrespondent( )
	{
	} // BiorxivApiCorrespondent


	@Override
	public List<ResearchItem> extractResearchItems( String responseBody )
	{
		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd" );

		List<ResearchItem> items = new ArrayList<>( );

		JsonObject root = JsonParser.parseString( responseBody ).getAsJsonObject( );
		JsonArray docs = root.getAsJsonArray( "collection" );

		// Create and populate each ResearchItem instance.
		// NOTE: This defines the mapping between biorxiv data fields and our own internal db fields.
		for ( JsonElement docElement : docs )
			{
			JsonObject doc = docElement.getAsJsonObject( );
			ResearchItem item = new ResearchItem( );
			item.setCreateDate( new Date( ) );

			try
				{
				// Call these the "minimum criteria" for saving the item.
				item.setDOI( doc.get( "doi" ).getAsString( ).trim( ) );
				item.setTitle( doc.get( "title" ).getAsString( ).trim( ) );
				item.setAuthors( Arrays.asList( doc.get( "authors" ).getAsString( ).trim( ).split( ";" ) ) );
				item.setCategory( doc.get( "category" ).getAsString( ).trim( ) );

				// Now it's qualified to be in the database. If anything goes wrong from here, it's not a big deal.
				items.add( item );

				// These are extras.
				if ( doc.has( "author_corresponding" ) )
					item.setAuthorCorresponding( doc.get( "author_corresponding" ).getAsString( ).trim( ) );
				if ( doc.has( "author_corresponding_institution" ) )
					item.setAuthorCorrespondingInstitution(
							doc.get( "author_corresponding_institution" ).getAsString( ).trim( ) );
				if ( doc.has( "version" ) )
					item.setVersion( doc.get( "version" ).getAsInt( ) );
				if ( doc.has( "published" ) )
					item.setPublicationDetail( doc.get( "published" ).getAsString( ).trim( ) );
				if ( doc.has( "abstract" ) )
					item.setAbstract( doc.get( "abstract" ).getAsString( ).trim( ) );
				}
			catch ( Exception exception )
				{
				LOG.error( "A document from '" + DOMAIN_HOST
						+ "' may not meet the minimum criteria to be persisted. (Full document: '" + doc.getAsString( )
						+ "'.)" );
				}

			if ( doc.has( "date" ) )
				{
				try
					{
					item.setPublicationDate( DATE_FORMAT.parse( doc.get( "date" ).getAsString( ).trim( ) ) );
					}
				catch ( ParseException exception )
					{
					// Oh well.
					LOG.error( "Unable to parse publication date '" + doc.get( "date" ) + "' of document (doi: '"
							+ item.getDOI( ) + "') from '" + DOMAIN_HOST + "'.", exception );
					}
				}
			}

		return items;

	} // extractResearchItems


	@Override
	public int getCrawlDelaySeconds( )
	{
		// Defined in the general biorxiv.org robots.txt as of 2020-10-15.
		return 7;

	} // getCrawlDelaySeconds


	@Override
	public URI getNextPageUri( URI currentUri )
	{
		URI nextUri = null;
		Matcher matcher = PAGE_URI_PATTERN.matcher( currentUri.toString( ) );

		// This could be a lot better. The interface should be giving access to the previous response, but right now
		// it's not, so we'll have to guess.
		if ( matcher.matches( ) )
			{
			StringBuilder builder = new StringBuilder( );
			builder.append( "https://api.biorxiv.org/details/biorxiv/" );

			if ( matcher.group( 3 ) == null )
				{
				builder.append( matcher.group( 1 ) ).append( '/' );
				builder.append( matcher.group( 2 ) ).append( '/' );
				}
			else
				{
				builder.append( matcher.group( 3 ) ).append( '/' );
				}

			// This is the important part. The rest could have been simpler (unless we want to do more with this later).
			int cursor = Integer.parseInt( matcher.group( 4 ) );
			builder.append( cursor + 100 );

			try
				{
				nextUri = new URI( builder.toString( ) );
				}
			catch ( URISyntaxException exception )
				{
				// Couldn't do it. Log so we can figure out why.
				LOG.error( "Couldn't assemble next page URI for given currentUri '" + currentUri + "'.", exception );
				}
			}

		return nextUri;

	} // getNextPageUri

}
