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
	public static class VoteInfo
	{
		// Data members.
		private final String fieldValue;
		private int fieldYesCount;
		private int fieldNoCount;

		public VoteInfo( String value )
		{
			fieldValue = value;

		} // VoteDatum


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


		public int getNoCount( )
		{
			return fieldNoCount;

		} // getNoCount


		public String getValue( )
		{
			return fieldValue;

		} // getValue


		public int getYesCount( )
		{
			return fieldYesCount;

		} // getYesCount

	} // class VoteInfo


	private static final Comparator<VoteInfo> YES_COMPARATOR = new Comparator<>( )
	{
		@Override
		public int compare( VoteInfo o1, VoteInfo o2 )
		{
			return o1.getYesCount( ) - o2.getYesCount( );

		} // compare
	};


	// Data members.
	private Map<String, VoteInfo> fieldVotesByAuthor = new HashMap<>( );
	private Map<String, VoteInfo> fieldVotesByInstitution = new HashMap<>( );

	public AppraisalSummary( )
	{
	} // AppraisalSummary


	private static List<VoteInfo> extractStatsByYes( Map<String, VoteInfo> votes, boolean ascending )
	{
		List<VoteInfo> result = new ArrayList<>( votes.values( ) );
		Collections.sort( result, YES_COMPARATOR );

		// The comparator will naturally sort in ascending order. We usually want descending.
		if ( !ascending )
			Collections.reverse( result );

		return result;

	} // getStatsByYes


	public void addResearchItem( ResearchItem item )
	{
		// Crude, but potentially effective.
		// TODO - Sadly, the utility of the entire tool comes down to this small bit of code, so more work may be
		// warranted here.
		if ( item.getAuthors( ) != null )
			{
			for ( String author : item.getAuthors( ) )
				{
				VoteInfo info = fieldVotesByAuthor.get( author );
				if ( info == null )
					{
					info = new VoteInfo( author );
					fieldVotesByAuthor.put( author, info );
					}

				info.addYesVotes( item.getYesVotes( ) );
				info.addNoVotes( item.getNoVotes( ) );
				}
			}

		String inst = item.getAuthorCorrespondingInstitution( );
		if ( inst != null )
			{
			VoteInfo info = fieldVotesByInstitution.get( inst );
			if ( info == null )
				{
				info = new VoteInfo( inst );
				fieldVotesByInstitution.put( inst, info );
				}

			info.addYesVotes( item.getYesVotes( ) );
			info.addNoVotes( item.getNoVotes( ) );
			}

	} // addResearchItem


	public List<VoteInfo> getAuthorStatsByYes( )
	{
		return extractStatsByYes( fieldVotesByAuthor, false );

	} // getAuthorStatsByYes


	public List<VoteInfo> getInstitutionStatsByYes( )
	{
		return extractStatsByYes( fieldVotesByInstitution, false );

	} // getInstitutionStatsByYes

}
