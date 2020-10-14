/*
 * AppContext.java
 * 
 * Created: Oct 11, 2020
 */
package org.spidermole.app;

import com.google.common.eventbus.EventBus;

/**
 * AppContext provides a safe organizational place for functionality that must be shared across disconnected FX UI
 * classes. A Singleton copy is owned and presented by the AppMain for code running on the JavaFX Application Thread.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 * @see    AppMain#instance()
 */
public class AppContext
{
	// Data members.
	private EventBus fieldEventBus = new EventBus( );

	public AppContext( )
	{
	} // AppContext


	public void destroy( )
	{
	} // destroy


	/**
	 * @return the eventBus, an instance of EventBus which may be used to pass events to other components in the system.
	 */
	public EventBus getEventBus( )
	{
		return fieldEventBus;

	} // getEventBus


	public void initialize( )
	{
	} // initialize

}
