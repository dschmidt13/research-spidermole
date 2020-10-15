/*
 * AppraisalTileView.java
 * 
 * Created: Oct 08, 2020
 */
package org.spidermole.app.appraiser;

import java.net.URL;
import java.util.ResourceBundle;

import org.spidermole.model.ResearchItem;
import org.spidermole.util.FXUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

/**
 * AppraisalTileView presents a cleanly laid out research item (such as a paper).
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppraisalTileView extends BorderPane implements Initializable
{
	// Class constants.
	private static final String COMPONENT_FXML_FILENAME = "AppraisalTileView.fxml";

	// Data members.
	private ResearchItem fieldResearchItem;

	// Injected view members.
	@FXML
	private Label fieldTitle;

	@FXML
	private TextArea fieldAbstract;

	public AppraisalTileView( )
	{
		FXUtils.loadAsControlRoot( COMPONENT_FXML_FILENAME, this );

	} // AppraisalTileView


	/**
	 * @return the researchItem
	 */
	public ResearchItem getResearchItem( )
	{
		return fieldResearchItem;

	} // getResearchItem


	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		update( );

	} // initialize


	/**
	 * @param researchItem the researchItem to set
	 */
	public void setResearchItem( ResearchItem researchItem )
	{
		fieldResearchItem = researchItem;
		update( );

	} // setResearchItem


	private void update( )
	{
		if ( fieldResearchItem == null )
			{
			// Clear all field values.
			fieldTitle.setText( "" );
			fieldAbstract.setText( "" );
			}
		else
			{
			// Update all field values.
			fieldTitle.setText( fieldResearchItem.getTitle( ) == null ? "" : fieldResearchItem.getTitle( ) );
			fieldAbstract.setText( fieldResearchItem.getAbstract( ) == null ? "" : fieldResearchItem.getAbstract( ) );
			}

	} // update

}
