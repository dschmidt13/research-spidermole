/*
 * MainController.java
 * 
 * Created: Oct 07, 2020
 */
package org.spidermole.app;

import java.net.URL;
import java.util.ResourceBundle;

import org.spidermole.app.appraiser.AppraiserRootController;

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

	private AppraiserRootController fieldAppraiserController;

	public MainController( )
	{
	} // MainController


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		super.initialize( location, resources );

		// Initialize the appraiser by loading its FXML.
		try
			{
			FXMLLoader loader = new FXMLLoader( MainController.class.getResource( "appraiser/AppraiserRoot.fxml" ) );
			Node node = loader.load( );
			fieldAppraiserController = loader.getController( );
			fieldMainPane.setCenter( node );
			}
		catch ( Exception exception )
			{
			fieldLog.error( "Failed to load Appraiser view. "
					+ "Please ensure the component's definitions are wired together properly.", exception );
			}


	} // initialize


	@Override
	public void destroy( )
	{
		if ( fieldAppraiserController != null )
			{
			try
				{
				fieldAppraiserController.destroy( );
				}
			catch ( Exception exception )
				{
				fieldLog.error( "Exception destroying appraiser controller.", exception );
				}
			}

		super.destroy( );

	} // destroy

}
