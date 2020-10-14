/*
 * SwipeView.java
 * 
 * Created: Oct 08, 2020
 */
package org.spidermole.app.appraiser;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spidermole.app.AppMain;
import org.spidermole.model.ResearchItem;
import org.spidermole.util.FXUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * SwipeView is a JavaFX component that wraps the UI for appraising a research item and indicating interest or
 * disinterest. It's a similar concept to swipe interfaces in dating apps, hence the name. It's worth noting that the UI
 * effect of "swiping" is not currently implemented; choices are indicated by button clicks.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class SwipeView extends BorderPane implements Initializable
{
	class AppraisalSwipeEvent
	{
		// Data members.
		private final ResearchItem fieldItem;
		private final boolean fieldInterested;

		public AppraisalSwipeEvent( ResearchItem item, boolean interested )
		{
			fieldItem = item;
			fieldInterested = interested;

		} // AppraisalSwipeEvent


		/**
		 * @return the item
		 */
		public ResearchItem getItem( )
		{
			return fieldItem;

		} // getItem


		/**
		 * @return the interested
		 */
		public boolean isInterested( )
		{
			return fieldInterested;

		} // isInterested

	}

	// Class constants.
	private static final Logger LOG = LogManager.getLogger( SwipeView.class );
	private static final String COMPONENT_FXML_FILENAME = "SwipeView.fxml";

	// Injected FXML members.
	@FXML
	private AppraisalTileView fieldTile;

	@FXML
	private Button fieldYesButton;

	@FXML
	private Button fieldNoButton;


	public SwipeView( )
	{
		FXUtils.loadAsControlRoot( COMPONENT_FXML_FILENAME, this );

	} // SwipeView


	public void actionNo( )
	{
		LOG.debug( "User said no to item '" + getResearchItem( ) + "'." );

		// Root controller is listening for these.
		AppMain.contextInstance( ).getEventBus( ).post( new AppraisalSwipeEvent( getResearchItem( ), false ) );

	} // actionNo


	public void actionYes( )
	{
		LOG.debug( "User said yes to item '" + getResearchItem( ) + "'." );

		// Root controller is listening for these.
		AppMain.contextInstance( ).getEventBus( ).post( new AppraisalSwipeEvent( getResearchItem( ), true ) );

	} // actionYes


	private void disableButtons( boolean disable )
	{
		fieldYesButton.setDisable( disable );
		fieldNoButton.setDisable( disable );

	} // disableButtons


	public ResearchItem getResearchItem( )
	{
		return fieldTile.getResearchItem( );

	} // getResearchItem


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		disableButtons( true );

	} // initialize


	public void setResearchItem( ResearchItem researchItem )
	{
		fieldTile.setResearchItem( researchItem );
		disableButtons( researchItem == null );

	} // setResearchItem

}
