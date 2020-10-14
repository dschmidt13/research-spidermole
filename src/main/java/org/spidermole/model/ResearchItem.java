/*
 * ResearchItem.java
 * 
 * Created: Oct 08, 2020
 */
package org.spidermole.model;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * ResearchItem is a simple POJO that gets persisted as JSON in the database.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class ResearchItem extends ModelDocument
{
	// Class constants.
	/**
	 * This is the value of the {@code type} field for which documents correspond to ResearchItems.
	 */
	public static final String DATABASE_TYPE = "researchItem";

	// Data members.
	@SerializedName( "title" )
	private String fieldTitle;

	@SerializedName( "doi" )
	private String fieldDOI;

	@SerializedName( "authors" )
	private List<String> fieldAuthors;

	@SerializedName( "publicationDate" )
	private Date fieldPublicationDate;

	@SerializedName( "detailUrl" )
	private String fieldDetailUrl;

	@SerializedName( "sourceUrl" )
	private String fieldSourceUrl;

	@SerializedName( "createDate" )
	private Date fieldCreateDate;

	@SerializedName( "yesVotes" )
	private Integer fieldYesVotes;

	@SerializedName( "noVotes" )
	private Integer fieldNoVotes;

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
	 * @return the createDate
	 */
	public Date getCreateDate( )
	{
		return fieldCreateDate;

	} // getCreateDate


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
	 * @return the noVotes
	 */
	public Integer getNoVotes( )
	{
		return fieldNoVotes;

	} // getNoVotes


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
	 * @return the yesVotes
	 */
	public Integer getYesVotes( )
	{
		return fieldYesVotes;

	} // getYesVotes


	/**
	 * @param authors the authors to set
	 */
	public void setAuthors( List<String> authors )
	{
		fieldAuthors = authors;

	} // setAuthors


	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate( Date createDate )
	{
		fieldCreateDate = createDate;

	} // setCreateDate


	/**
	 * @param detailUrl the detailUrl to set
	 */
	public void setDetailUrl( String detailUrl )
	{
		fieldDetailUrl = detailUrl;
	}


	/**
	 * @param dOI the dOI to set
	 */
	public void setDOI( String dOI )
	{
		fieldDOI = dOI;

	} // setDOI


	/**
	 * @param noVotes the noVotes to set
	 */
	public void setNoVotes( Integer noVotes )
	{
		fieldNoVotes = noVotes;

	} // setNoVotes


	/**
	 * @param publicationDate the publicationDate to set
	 */
	public void setPublicationDate( Date publicationDate )
	{
		fieldPublicationDate = publicationDate;

	} // setPublicationDate


	/**
	 * @param sourceUrl the sourceUrl to set
	 */
	public void setSourceUrl( String sourceUrl )
	{
		fieldSourceUrl = sourceUrl;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle( String title )
	{
		fieldTitle = title;

	} // setTitle


	/**
	 * @param yesVotes the yesVotes to set
	 */
	public void setYesVotes( Integer yesVotes )
	{
		fieldYesVotes = yesVotes;

	} // setYesVotes


	@Override
	public String toString( )
	{
		return fieldDOI;

	} // toString


}
