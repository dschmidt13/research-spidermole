/*
 * AppMain.java
 * 
 * Created: Oct 07, 2020
 */
package org.spidermole.app;

import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * AppMain launches the JavaFX app.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppMain extends Application
{
	// Class constants.
	private static final Logger LOG = LogManager.getLogger( AppMain.class );
	private static final URL MAIN_UI_FXML_DOC = AppMain.class.getResource( "Main.fxml" );
	private static final String WINDOW_TITLE = "Spidermole Meta-Research Tool";

	// Data members.
	private MainController fieldMainController;


	private static void handleUncaughtException( Thread dead, Throwable cause )
	{
		LOG.error( "An uncaught throwable in the thread '" + dead + "' has resulted in its demise.", cause );

	} // handleUncaughtException


	public static void main( String[ ] args )
	{
		// JavaFX apps can be complex multi-threaded creatures. This will ensure we see *everything*.
		Thread.setDefaultUncaughtExceptionHandler( AppMain::handleUncaughtException );

		launch( args );

	} // main


	@Override
	public void start( Stage primaryStage )
	{
		try
			{
			FXMLLoader loader = new FXMLLoader( MAIN_UI_FXML_DOC );
			Parent uiRoot = loader.load( );
			fieldMainController = loader.getController( );

			Scene scene = new Scene( uiRoot );
			primaryStage.setScene( scene );

			primaryStage.setTitle( WINDOW_TITLE );

			primaryStage.show( );
			}
		catch ( Exception exception )
			{
			LOG.error( "Exception initializing main app window.", exception );
			}

	} // start


	@Override
	public void stop( ) throws Exception
	{
		if ( fieldMainController != null )
			{
			try
				{
				fieldMainController.destroy( );
				}
			catch ( Exception exception )
				{
				LOG.error( "Exception destroying the MainController. Resources could have been leaked!", exception );
				}
			}

		super.stop( );

	} // stop

}
