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

/**
 * AppraiserRootController
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraiserRootController extends AbstractController
{
	// Injected FXML view members.
	// TODO
	//	@FXML
	//	private Object fieldSwipeFilter;

	@FXML
	private SwipeView fieldSwipeView;

	// TODO
	//	@FXML
	//	private Object fieldResults;

	public AppraiserRootController( )
	{
	} // AppraiserRootController


	@Override
	public void destroy( )
	{
		super.destroy( );

	} // destroy


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

	} // initialize

}
