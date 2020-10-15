/*
 * MainController.java
 * 
 * Created: Oct 07, 2020
 */
package org.spidermole.app;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * MainController manages other major controllers and provides a communication bus between app components.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class MainController extends AbstractController
{
	// Injected view members.
	@FXML
	private BorderPane fieldMainPane;

	// Data members.
	private List<AbstractController> fieldChildControllers = new ArrayList<>( );

	public MainController( )
	{
	} // MainController


	@Override
	public void destroy( )
	{
		for ( AbstractController controller : fieldChildControllers )
			{
			try
				{
				controller.destroy( );
				}
			catch ( Exception exception )
				{
				fieldLog.error( "Exception destroying child controller of type " + controller.getClass( ) + ".",
						exception );
				}
			}

		super.destroy( );

	} // destroy


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

		// Initialize the spider by loading its FXML.
		try
			{
			FXMLLoader loader = new FXMLLoader( getClass( ).getResource( "spider/CrawlControlPanel.fxml" ) );
			Node node = loader.load( );
			fieldChildControllers.add( loader.getController( ) );
			fieldMainPane.setTop( node );
			}
		catch ( Exception exception )
			{
			fieldLog.error( "Failed to load Spider view. "
					+ "Please ensure the component's definitions are wired together properly.", exception );
			}

		// Initialize the appraiser by loading its FXML.
		try
			{
			FXMLLoader loader = new FXMLLoader( getClass( ).getResource( "appraiser/AppraiserRoot.fxml" ) );
			Node node = loader.load( );
			fieldChildControllers.add( loader.getController( ) );
			fieldMainPane.setCenter( node );
			}
		catch ( Exception exception )
			{
			fieldLog.error( "Failed to load Appraiser view. "
					+ "Please ensure the component's definitions are wired together properly.", exception );
			}

	} // initialize

}
