/*
 * AbstractController.java
 * 
 * Created: Oct 07, 2020
 */
package org.spidermole.app.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.Initializable;

/**
 * AbstractController specifies default behaviors and provides a default log for UI controllers.
 * <p>
 * The FXML-based {@code Initializable} interface is provided as a convenience. If the controller is not intended to be
 * auto-initialized by JavaFX, simply throw an exception from the
 * {@link #initialize(java.net.URL, java.util.ResourceBundle)} method.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public abstract class AbstractController implements Initializable
{
	// Default controller log instance.
	protected final Logger fieldLog = LogManager.getLogger( getClass( ) );

	public AbstractController( )
	{
	} // AbstractController


	/**
	 * Called by the application main when the application is closing, and should be called by any responsible
	 * controller on its subcontrollers from its own {@code destroy}.
	 * <p>
	 * Subclasses may override this method if they need to perform resource cleanup, interrupt background services, etc.
	 * The default implementation only contains a log statement; all overrides should call super for consistent logging.
	 */
	public void destroy( )
	{
		fieldLog.debug( "Destroying controller of type " + getClass( ).getName( ) + "." );

	} // destroy


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		fieldLog.debug( "Initializing controller of type " + getClass( ).getName( ) + "." );

	} // initialize

}
