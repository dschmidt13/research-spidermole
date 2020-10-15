/*
 * ModelDocument.java
 * 
 * Created: Oct 14, 2020
 */
package org.spidermole.model;

import com.cloudant.client.api.model.Document;
import com.google.gson.annotations.SerializedName;

/**
 * ModelDocument imposes a "type" field on model items intended to be persisted.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public abstract class ModelDocument extends Document
{
	// Data members.
	@SerializedName( "type" )
	private String fieldType;


	public ModelDocument( String type )
	{
		fieldType = type;

	} // ModelDocument


	/**
	 * @return the type
	 */
	public String getType( )
	{
		return fieldType;

	} // getType


	/**
	 * @param type the type to set
	 */
	public void setType( String type )
	{
		fieldType = type;

	} // setType


}
