/*
 * AppMain.java
 * 
 * Created: Oct 07, 2020
 */
package org.spidermole.app;

import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

	// @formatter:off
	private static final URL[] ICON_URLS = {
			AppMain.class.getResource( "/icons/icon-256.png" ),
			AppMain.class.getResource( "/icons/icon-128.png" ),
			AppMain.class.getResource( "/icons/icon-64.png" ),
			AppMain.class.getResource( "/icons/icon-32.png" ),
			AppMain.class.getResource( "/icons/icon-16.png" ),
			AppMain.class.getResource( "/icons/icon-8.png" ),
	};
	// @formatter:on

	private static final URL MAIN_UI_FXML_DOC = AppMain.class.getResource( "Main.fxml" );

	private static final String WINDOW_TITLE = "Spidermole Meta-Research Tool";

	/**
	 * Just one Singleton couldn't hurt, right? It shall only be used to provide access to other, separately-testable,
	 * shared object resources.
	 * <p>
	 * Also, it's only available on the FX thread.
	 */
	private static final ThreadLocal<AppContext> CONTEXT_INSTANCE = new ThreadLocal<>( );

	// Data members.
	private EventBus fieldEventBus = new EventBus( );
	private MainController fieldMainController;


	/**
	 * @return the {@link AppContext} instance if called from the JavaFX application thread, or {@code null} otherwise.
	 */
	public static AppContext contextInstance( )
	{
		return CONTEXT_INSTANCE.get( );

	} // contextInstance


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


	private void loadAndSetIcons( Stage stage )
	{
		for ( URL iconUrl : ICON_URLS )
			{
			Image icon;
			try ( InputStream iconStream = iconUrl.openStream( ) )
				{
				icon = new Image( iconStream );
				stage.getIcons( ).add( icon );
				}
			catch ( Exception exception )
				{
				LOG.error( "Exception loading icon '" + iconUrl + "'.", exception );
				}
			}

	} // loadAndSetIcons


	@Override
	public void start( Stage primaryStage )
	{
		// First initialize the AppContext instance.
		AppContext appContext = new AppContext( );
		CONTEXT_INSTANCE.set( appContext );

		// Initialize the UI.
		try
			{
			FXMLLoader loader = new FXMLLoader( MAIN_UI_FXML_DOC );
			Parent uiRoot = loader.load( );
			fieldMainController = loader.getController( );

			Scene scene = new Scene( uiRoot );
			primaryStage.setScene( scene );

			primaryStage.setTitle( WINDOW_TITLE );
			loadAndSetIcons( primaryStage );

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
				// Other controllers are destroyed hierarchically by the MainController.
				fieldMainController.destroy( );
				}
			catch ( Exception exception )
				{
				LOG.error( "Exception destroying the MainController. Resources could have been leaked!", exception );
				}
			}

		// If anything is trying to use it at this point, it's wrong, and we'd want to know by letting it occasionally
		// crash.
		AppContext context = CONTEXT_INSTANCE.get( );
		CONTEXT_INSTANCE.set( null );
		try
			{
			context.destroy( );
			}
		catch ( Exception exception )
			{
			LOG.error( "Exception destroy the AppContext. Resources could have been leaked!", exception );
			}

		super.stop( );

	} // stop

}
