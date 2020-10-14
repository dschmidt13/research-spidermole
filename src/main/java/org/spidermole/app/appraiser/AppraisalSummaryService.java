/*
 * AppraisalSummaryService.java
 * 
 * Created: Oct 11, 2020
 */
package org.spidermole.app.appraiser;

import java.util.List;

import org.spidermole.model.ResearchItem;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * AppraisalSummaryService performs the (possibly) computationally intensive work of generating a summary of all
 * appraisal votes. It leans heavily on {@link AppraisalSummary} to perform the appropriate computations.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraisalSummaryService extends Service<AppraisalSummary>
{
	// Data members.
	private List<ResearchItem> fieldItems;

	public AppraisalSummaryService( )
	{
	} // AppraisalSummaryService


	@Override
	protected Task<AppraisalSummary> createTask( )
	{
		final List<ResearchItem> items = fieldItems;

		return new Task<>( )
		{
			@Override
			protected AppraisalSummary call( ) throws Exception
			{
				AppraisalSummary summary = new AppraisalSummary( );

				for ( int index = 0; index < items.size( ); index++ )
					{
					updateProgress( index, items.size( ) );

					// Respect cancellation requests.
					if ( isCancelled( ) )
						return summary;

					summary.addResearchItem( items.get( index ) );
					}
				updateProgress( items.size( ), items.size( ) );

				return summary;

			} // call
		};

	} // createTask


	/**
	 * @param items the items to set
	 */
	public void setItems( List<ResearchItem> items )
	{
		fieldItems = items;

	} // setItems

}
