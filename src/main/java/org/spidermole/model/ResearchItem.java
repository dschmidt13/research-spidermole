/*
 * ResearchItem.java
 * 
 * Created: Oct 08, 2020
 */
package org.spidermole.model;

import java.util.Date;
import java.util.List;

/**
 * ResearchItem is a simple POJO that gets persisted as JSON in the database.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ResearchItem
{
	// Data members.
	private String fieldTitle;
	private String fieldDOI;
	private List<String> fieldAuthors;
	private Date fieldPublicationDate;
	private String fieldDetailUrl;
	private String fieldSourceUrl;

	public ResearchItem( )
	{
	} // ResearchItem


	/**
	 * @return the authors
	 */
	public List<String> getAuthors( )
	{
		return fieldAuthors;

	} // getAuthors


	/**
	 * @return the detailUrl
	 */
	public String getDetailUrl( )
	{
		return fieldDetailUrl;

	} // getDetailUrl


	/**
	 * @return the dOI
	 */
	public String getDOI( )
	{
		return fieldDOI;

	} // getDOI


	/**
	 * @return the publicationDate
	 */
	public Date getPublicationDate( )
	{
		return fieldPublicationDate;

	} // getPublicationDate


	/**
	 * @return the sourceUrl
	 */
	public String getSourceUrl( )
	{
		return fieldSourceUrl;

	} // getSourceUrl


	/**
	 * @return the title
	 */
	public String getTitle( )
	{
		return fieldTitle;

	} // getTitle


	/**
	 * @param authors
	 *                the authors to set
	 */
	public void setAuthors( List<String> authors )
	{
		fieldAuthors = authors;

	} // setAuthors


	/**
	 * @param detailUrl
	 *                  the detailUrl to set
	 */
	public void setDetailUrl( String detailUrl )
	{
		fieldDetailUrl = detailUrl;
	}


	/**
	 * @param dOI
	 *            the dOI to set
	 */
	public void setDOI( String dOI )
	{
		fieldDOI = dOI;

	} // setDOI


	/**
	 * @param publicationDate
	 *                        the publicationDate to set
	 */
	public void setPublicationDate( Date publicationDate )
	{
		fieldPublicationDate = publicationDate;

	} // setPublicationDate


	/**
	 * @param sourceUrl
	 *                  the sourceUrl to set
	 */
	public void setSourceUrl( String sourceUrl )
	{
		fieldSourceUrl = sourceUrl;
	}


	/**
	 * @param title
	 *              the title to set
	 */
	public void setTitle( String title )
	{
		fieldTitle = title;

	} // setTitle


	@Override
	public String toString( )
	{
		return fieldDOI;

	} // toString


}
