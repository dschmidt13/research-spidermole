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

	@SerializedName( "authorCorresponding" )
	private String fieldAuthorCorresponding;

	@SerializedName( "authorCorrespondingInstitution" )
	private String fieldAuthorCorrespondingInstitution;

	@SerializedName( "archiveVersion" )
	private Integer fieldVersion;

	@SerializedName( "publishDetails" )
	private String fieldPublicationDetail;

	@SerializedName( "category" )
	private String fieldCategory;

	@SerializedName( "abstract" )
	private String fieldAbstract;

	@SerializedName( "yesVotes" )
	private Integer fieldYesVotes;

	@SerializedName( "noVotes" )
	private Integer fieldNoVotes;

	public ResearchItem( )
	{
		super( DATABASE_TYPE );

	} // ResearchItem


	/**
	 * @return the abstract
	 */
	public String getAbstract( )
	{
		return fieldAbstract;

	} // getAbstract


	/**
	 * @return the authorCorresponding
	 */
	public String getAuthorCorresponding( )
	{
		return fieldAuthorCorresponding;

	} // getAuthorCorresponding


	/**
	 * @return the authorCorrespondingInstitution
	 */
	public String getAuthorCorrespondingInstitution( )
	{
		return fieldAuthorCorrespondingInstitution;

	} // getAuthorCorrespondingInstitution


	/**
	 * @return the authors
	 */
	public List<String> getAuthors( )
	{
		return fieldAuthors;

	} // getAuthors


	/**
	 * @return the category
	 */
	public String getCategory( )
	{
		return fieldCategory;

	} // getCategory


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
	 * @return the publicationDetail
	 */
	public String getPublicationDetail( )
	{
		return fieldPublicationDetail;

	} // getPublicationDetail


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
	 * @return the version
	 */
	public Integer getVersion( )
	{
		return fieldVersion;

	} // getVersion


	/**
	 * @return the yesVotes
	 */
	public Integer getYesVotes( )
	{
		return fieldYesVotes;

	} // getYesVotes


	/**
	 * @param abstract1 the abstract to set
	 */
	public void setAbstract( String abstract1 )
	{
		fieldAbstract = abstract1;

	} // setAbstract


	/**
	 * @param authorCorresponding the authorCorresponding to set
	 */
	public void setAuthorCorresponding( String authorCorresponding )
	{
		fieldAuthorCorresponding = authorCorresponding;

	} // setAuthorCorresponding


	/**
	 * @param authorCorrespondingInstitution the authorCorrespondingInstitution to set
	 */
	public void setAuthorCorrespondingInstitution( String authorCorrespondingInstitution )
	{
		fieldAuthorCorrespondingInstitution = authorCorrespondingInstitution;

	} // setAuthorCorrespondingInstitution


	/**
	 * @param authors the authors to set
	 */
	public void setAuthors( List<String> authors )
	{
		fieldAuthors = authors;

	} // setAuthors


	/**
	 * @param category the category to set
	 */
	public void setCategory( String category )
	{
		fieldCategory = category;

	} // setCategory


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

	} // setDetailUrl


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
	 * @param publicationDetail the publicationDetail to set
	 */
	public void setPublicationDetail( String publicationDetail )
	{
		fieldPublicationDetail = publicationDetail;

	} // setPublicationDetail


	/**
	 * @param sourceUrl the sourceUrl to set
	 */
	public void setSourceUrl( String sourceUrl )
	{
		fieldSourceUrl = sourceUrl;

	} // setSourceUrl


	/**
	 * @param title the title to set
	 */
	public void setTitle( String title )
	{
		fieldTitle = title;

	} // setTitle


	/**
	 * @param version the version to set
	 */
	public void setVersion( Integer version )
	{
		fieldVersion = version;

	} // setVersion


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
