/*
 * CrawlCorrespondent.java
 * 
 * Created: Oct 15, 2020
 */
package org.spidermole.app.spider;

import java.net.URI;
import java.util.List;

import org.spidermole.model.ResearchItem;

/**
 * CrawlCorrespondent defines an object that knows how to interpret API or web page responses for a specific domain,
 * will convert those into persistable objects, and can provide information on how to properly and politely navigate the
 * API or site.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public interface CrawlCorrespondent
{
	// FIXME - Fix this API. It should be giving these functions more context to work with - possibly even a copy of the
	// robots.txt would be nice (upon construction?).

	List<ResearchItem> extractResearchItems( String responseBody );


	int getCrawlDelaySeconds( );


	URI getNextPageUri( URI currentUri );

}
