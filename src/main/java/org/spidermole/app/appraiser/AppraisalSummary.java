/*
 * AppraisalSummary.java
 * 
 * Created: Oct 11, 2020
 */
package org.spidermole.app.appraiser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spidermole.model.ResearchItem;

/**
 * AppraisalSummary performs computation of appraisal results and encapsulates them, as well as some of the running
 * calculation fields, in a single portable container.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraisalSummary
{
	/**
	 * A simple struct-type class to associate and sort data together.
	 */
	public static class AuthorStats
	{
		// Data members.
		private final String fieldAuthor;
		private int fieldYesCount;
		private int fieldNoCount;

		public AuthorStats( String author )
		{
			fieldAuthor = author;

		} // AuthorStats


		private void addNoVotes( Integer count )
		{
			if ( count != null )
				fieldNoCount += count.intValue( );

		} // addNoVotes


		private void addYesVotes( Integer count )
		{
			if ( count != null )
				fieldYesCount += count.intValue( );

		} // addYesVotes


		public String getAuthor( )
		{
			return fieldAuthor;

		} // getAuthor


		public int getNoCount( )
		{
			return fieldNoCount;

		} // getNoCount


		public int getYesCount( )
		{
			return fieldYesCount;

		} // getYesCount

	} // AuthorStats


	private static final Comparator<AuthorStats> YES_COMPARATOR = new Comparator<>( )
	{
		@Override
		public int compare( AuthorStats o1, AuthorStats o2 )
		{
			return o1.getYesCount( ) - o2.getYesCount( );

		} // compare
	};


	// Data members.
	private Map<String, AuthorStats> fieldVotesByAuthor = new HashMap<>( );

	public AppraisalSummary( )
	{
	} // AppraisalSummary


	public void addResearchItem( ResearchItem item )
	{
		// Crude, but potentially effective.
		// TODO - Sadly, the utility of the entire tool comes down to this small bit of code, so more work may be
		// warranted here.
		if ( item.getAuthors( ) != null )
			{
			for ( String author : item.getAuthors( ) )
				{
				AuthorStats stats = fieldVotesByAuthor.get( author );
				if ( stats == null )
					{
					stats = new AuthorStats( author );
					fieldVotesByAuthor.put( author, stats );
					}

				stats.addYesVotes( item.getYesVotes( ) );
				stats.addNoVotes( item.getNoVotes( ) );
				}
			}

	} // addResearchItem


	public List<AuthorStats> getAuthorStatsByYes( )
	{
		List<AuthorStats> result = new ArrayList<>( fieldVotesByAuthor.values( ) );
		Collections.sort( result, YES_COMPARATOR );

		// The comparator will sort ascending. We want descending.
		Collections.reverse( result );

		return result;

	} // getAuthorStatsByYes

}
