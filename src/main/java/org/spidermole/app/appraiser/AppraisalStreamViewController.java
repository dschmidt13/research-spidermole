/*
 * AppraisalStreamViewController.java
 * 
 * Created: Oct 08, 2020
 */
package org.spidermole.app.appraiser;

import java.net.URL;
import java.util.ResourceBundle;

import org.spidermole.app.AbstractController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * AppraisalStreamViewController manages the UI updates and background services related to filters for user appraisal
 * streams and the querying of stream data itself from the database.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraisalStreamViewController extends AbstractController
{
	// Injected FXML members.
	@FXML
	private TextField fieldFilterText;

	@FXML
	private ComboBox<String> fieldSortBy;

	@FXML
	private Label fieldRemainingLabel;

	@FXML
	private Button fieldGenerateButton;

	public AppraisalStreamViewController( )
	{
	} // AppraisalStreamViewController


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

		// Attempt to load a default stream.
		actionLoadStream( );

	} // initialize


	@Override
	public void destroy( )
	{
		super.destroy( );

	} // destroy


	public void actionLoadStream( )
	{
		fieldLog.debug( "Loading appraisal stream." );

		// TODO

	} // actionLoadStream

}
