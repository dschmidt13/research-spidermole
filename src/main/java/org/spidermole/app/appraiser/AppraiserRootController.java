/*
 * AppraiserRootController.java
 * 
 * Created: Oct 07, 2020
 */
package org.spidermole.app.appraiser;

import java.net.URL;
import java.util.ResourceBundle;

import org.spidermole.app.AbstractController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * AppraiserRootController
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraiserRootController extends AbstractController
{
	// Injected FXML view members.
	@FXML
	private BorderPane fieldStreamViewContainer;

	@FXML
	private SwipeView fieldSwipeView;

	@FXML
	private BorderPane fieldResultViewContainer;

	// Standard data members.
	private AppraisalStreamViewController fieldStreamViewController;

	public AppraiserRootController( )
	{
	} // AppraiserRootController


	@Override
	public void destroy( )
	{
		try
			{
			fieldStreamViewController.destroy( );
			}
		finally
			{
			super.destroy( );
			}

	} // destroy


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

		// Initialize stream view.
		try
			{
			FXMLLoader loader = new FXMLLoader( getClass( ).getResource( "AppraisalStreamView.fxml" ) );
			Node view = loader.load( );
			fieldStreamViewController = loader.getController( );
			fieldStreamViewContainer.setCenter( view );
			}
		catch ( Exception exception )
			{
			fieldLog.error( "Failed to initialize appraisal stream view.", exception );
			}

		// TODO - Initialize results view.

	} // initialize

}
