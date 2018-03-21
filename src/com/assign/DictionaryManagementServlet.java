/**
 * 
 */
package com.assign;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author alif
 * 
 * The controller servlet used to send all incoming web request add, update, delete,
 * and search to appropriate method using the DAO as managed bean after validating the
 * basic data.
 * 
 */
public class DictionaryManagementServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    DictionaryDAO dictDao = null;
    private Logger log;
    /*
     * Initializing Log4j and DAO in this method.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
	super.init();
	dictDao = new DictionaryDAO();

	String prefix = getServletContext().getRealPath("/");
	String file = getInitParameter("log4j-init-file");
	// if the log4j-init-file context parameter is not set, then no point in
	// trying
	if (file != null)
	{
	    PropertyConfigurator.configure(prefix + file);
	    System.out.println("Log4J Logging started: " + prefix + file);
	    log = Logger.getLogger("dictionary-applogger");
	}
	else
	{
	    System.out.println("Log4J Is not configured for your Application: "
		    + prefix + file);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy()
    {
	super.destroy();
	dictDao = null;
	log = null;
    }

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException
    {
	//For now Get and Post are same 
	doGet(req, resp);
    }

    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
	String action = request.getParameter("action");
	if(log.isDebugEnabled())
	    log.debug("Got action: "+action);
	if (action.equals("add"))
	{
	    addEntry(request, response);
	}
	else if (action.equals("delete"))
	{
	    deleteEntry(request, response);
	}
	else if (action.equals("sort"))
	{
	    sortDictionary(request, response);
	}
	else if (action.equals("search"))
	{
	    searchDictionary(request, response);
	}
	else if (action.equals("update"))
	{
	    updateDefinitionDictionary(request, response);
	}
    }
    
    /**
     * Validate two fields word and definition for blank values
     * as blank should not be allowed.
     * 
     * @param response
     * @param word
     * @param definition
     * @return
     * @throws IOException
     */
    private boolean validAdd(HttpServletResponse response, String word, String definition) throws IOException
    {
	String errorMessage = "Add new dictionary entry service failed because";
	boolean error = false;

	if(word == null || word.trim().length() <= 0)
	{
	    errorMessage = errorMessage + " word was blank, ";
	    error = true;
	}
	if(definition == null || definition.trim().length() <= 0)
	{
	    errorMessage = errorMessage + " definition was blank ";
	    error = true;
	}
	
	if(error)
	{
	    log.error(errorMessage);
	    // Create the error response XML
	    StringBuffer xml = createErrorXMLResponse(errorMessage);

	    // Send the response back to the browser
	    sendResponse(response, xml.toString());
	    return false;
	}
	return true;
    }

    /**
     * Used to add new word and it's definition in Dictionary entry.
     * This method does allows duplicate word entries for now.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void addEntry(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
	boolean result = false;

	String word = request.getParameter("word");
	String definition = request.getParameter("definition");

	if (validAdd(response, word, definition))
	{
	    // Store the object in the database
	    String uniqueId = dictDao.storeDictionaryEntry(word.trim(),
		    definition.trim());
	    if (uniqueId != null && !"".equals(uniqueId.trim())
		    && !"0".equals(uniqueId.trim()))
	    {
		result = true;
	    }

	    // Create the response XML
	    StringBuffer xml = createXMLResponse(result, uniqueId);

	    // Send the response back to the browser
	    sendResponse(response, xml.toString());
	}
    }

    /**
     * Used to delete one entry from Dictionary based on Id.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void deleteEntry(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
	boolean removed = false;
	String id = request.getParameter("id");

	// Delete the user from database
	if (id != null && !"".equals(id.trim()))
	{
	    removed = dictDao.removeDictionaryById(Integer.parseInt(id));
	}

	// Create the response XML
	StringBuffer xml = createXMLResponse(removed, id);

	// Send the response back to the browser
	sendResponse(response, xml.toString());
    }

    /**
     * Returns all the entries from Dictionary.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void listDictionaryEntries(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
	boolean result = true;

	// Create the response XML
	StringBuffer xml = createCompleteXMLResponse(dictDao.getAllDictionaryEntry());

	// Send the response back to the browser
	sendResponse(response, xml.toString());
    }

    /**
     * Sort the Dictionary based on column and order and return all result back.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void sortDictionary(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
	boolean result = true;
	String sortBy = request.getParameter("orderBy");
	String sortDirection = request.getParameter("sortDirection");

	// Create the response XML
	StringBuffer xml = createCompleteXMLResponse(dictDao.getAllDictionaryEntry(sortBy,
		sortDirection));

	// Send the response back to the browser
	sendResponse(response, xml.toString());
    }
    
    /**
     * Search the Dictionary for a given word.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void searchDictionary(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
	boolean result = false;

	String searchText = request.getParameter("searchText");

	// Search the object in the database
	List dictionaryList = dictDao.searchDictionaryEntry(searchText);
	if (dictionaryList != null && dictionaryList.size() > 0)
	{
	    result = true;
	}

	StringBuffer xml = new StringBuffer();
	if (result)
	{
	    // Create the response XML
	    xml = createCompleteXMLResponse(dictionaryList);
	}
	else
	{
	    xml = createXMLResponse(result, "0");
	}

	// Send the response back to the browser
	sendResponse(response, xml.toString());
    }
    
    /**
     * Validate the definition to make sure it's not blank.
     * 
     * @param response
     * @param definition
     * @return
     * @throws IOException
     */
    private boolean validUpdateDefinition(HttpServletResponse response, String definition) throws IOException
    {
	String errorMessage = "Update of dictionary element service failed because";
	boolean error = false;

	if(definition == null || definition.trim().length() <= 0)
	{
	    errorMessage = errorMessage + " definition was blank ";
	    error = true;
	}
	
	if(error)
	{
	    log.error(errorMessage);
	    // Create the error response XML
	    StringBuffer xml = createErrorXMLResponse(errorMessage);

	    // Send the response back to the browser
	    sendResponse(response, xml.toString());
	    return false;
	}
	return true;
    }
    
    /**
     * Update the definition on Dictionary entry for given Id.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void updateDefinitionDictionary(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
	boolean result = false;

	String uniqueID = request.getParameter("uniqueID");
	String definitionElement = request.getParameter("definitionElement");

	if (validUpdateDefinition(response, definitionElement))
	{
	    // Store the object in the database
	    boolean done = dictDao.updateDefinitionDictionary(uniqueID,
		    definitionElement.trim());
	    result = done;

	    // Create the response XML
	    StringBuffer xml = createXMLResponse(result, uniqueID);

	    // Send the response back to the browser
	    sendResponse(response, xml.toString());
	}
    }
    
    /**
     * Create Error message response to be sent to user.
     * 
     * @param result
     * @param uniqueId
     * @return
     */
    private StringBuffer createErrorXMLResponse(String errorMessage)
    {
	StringBuffer xml = new StringBuffer("<result>");
	xml.append("<status>0</status>");

	xml.append("<error>");
	xml.append(errorMessage);
	xml.append("</error>");
	
	xml.append("</result>");
	if(log.isDebugEnabled())
	    log.debug("Response XML: "+xml.toString());
	return xml;
    }

    /**
     * Create message response in XML format to be sent to user.
     * 
     * @param result
     * @param uniqueId
     * @return
     */
    private StringBuffer createXMLResponse(boolean result, String uniqueId)
    {
	StringBuffer xml = new StringBuffer("<result>");
	if (result)
	{
	    xml.append("<status>1</status>");
	}
	else
	{
	    xml.append("<status>0</status>");
	}
	xml.append("<dict>");

	xml.append("<uniqueID>");
	xml.append(uniqueId);
	xml.append("</uniqueID>");

	xml.append("</dict>");
	xml.append("</result>");
	if(log.isDebugEnabled())
	    log.debug("Response XML: "+xml.toString());
	return xml;
    }

    /**
     * Create message response in XML format by looping through and
     * creating one Dictionary element for each entry to be sent to user.
     * 
     * @param result
     * @return
     */
    @SuppressWarnings("rawtypes")
    private StringBuffer createCompleteXMLResponse(List dictionaryList)
    {
	StringBuffer xml = new StringBuffer("<result>");
	xml.append("<status>1</status>");
	for (int i = 0; i < dictionaryList.size(); i++)
	{
	    Dictionary theDictionary = (Dictionary)dictionaryList.get(i);
	    xml.append("<dict>");

	    xml.append("<uniqueID>");
	    xml.append(theDictionary.getId());
	    xml.append("</uniqueID>");

	    xml.append("<word>");
	    xml.append(theDictionary.getWord());
	    xml.append("</word>");

	    xml.append("<definition>");
	    xml.append(theDictionary.getDefinition());
	    xml.append("</definition>");

	    xml.append("</dict>");
	}
	xml.append("</result>");
	if(log.isDebugEnabled())
	    log.debug("Response XML: "+xml.toString());
	return xml;
    }

    /**
     * Used the response writer to send the response back.
     * 
     * @param response
     * @param responseText
     * @throws IOException
     */
    private void sendResponse(HttpServletResponse response, String responseText)
	    throws IOException
    {
	response.setContentType("text/xml");
	response.getWriter().write(responseText);
    }
}
