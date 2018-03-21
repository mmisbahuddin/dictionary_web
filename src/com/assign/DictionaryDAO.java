/**
 * 
 */
package com.assign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * The Data Access Object (DAO) also used as managed bean (singleton) to
 * execute and retrive Dictionary data from database.
 * 
 * @author alif
 * 
 */
public class DictionaryDAO
{
    private static Logger log = Logger.getLogger("dictionary-applogger"); 
    private static HashMap<String, String> fieldMap = new HashMap<String, String>();
    private static HashMap<String, String> orderMap = new HashMap<String, String>();

    static
    {
	fieldMap.put("0", "uid");
	fieldMap.put("1", "words");
	fieldMap.put("2", "definitions");

	orderMap.put("0", "asc");
	orderMap.put("1", "desc");
    }

    /**
	 *  
	 */
    public DictionaryDAO()
    {
	super();
    }

    /**
     * Used to add new word and it's definition in Dictionary entry using persistence.
     * This method does allows duplicate word entries for now.
     * 
     * @param word
     * @param definition
     * @return
     */
    public String storeDictionaryEntry(String word, String definition)
    {
	boolean result = false;
	String uniqueID = "0";
	Session session = HibernateUtil.currentSession();
	if(log.isDebugEnabled())
	    log.debug("Add called with parameter word: "+word+ " definition: "+definition);
	Transaction tx = session.beginTransaction();

	Dictionary theDictionary = new Dictionary();
	theDictionary.setWord(word);
	theDictionary.setDefinition(definition);

	session.save(theDictionary);

	tx.commit();
	HibernateUtil.closeSession();

	uniqueID = String.valueOf(theDictionary.getId());
	log.info("Add successful id: "+uniqueID+ " word: "+theDictionary.getWord());
	result = true;
	return uniqueID;
    }

    /**
     * Returns all the entries from Dictionary.
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List getAllDictionaryEntry()
    {
	List dictionaryList = new ArrayList();

	Session session = HibernateUtil.currentSession();
	Transaction tx = session.beginTransaction();
	Query query = session.createQuery("select d from Dictionary as d");
	dictionaryList = query.list();
	tx.commit();
	HibernateUtil.closeSession();

	return dictionaryList;
    }

    /**
     * Sort the Dictionary based on column and order and return all result back.
     * 
     * @param sortBy
     * @param sortDirection
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List getAllDictionaryEntry(String sortBy, String sortDirection)
    {
	List dictionaryList = new ArrayList();

	Session session = HibernateUtil.currentSession();
	Transaction tx = session.beginTransaction();
	String qryString = "select d from Dictionary as d order by "
		+ fieldMap.get(sortBy) + " " + orderMap.get(sortDirection);
	Query query = session.createQuery(qryString);
	dictionaryList = query.list();
	tx.commit();
	HibernateUtil.closeSession();

	return dictionaryList;
    }

    /**
     * Get unique Dictionary entry by Id.
     * 
     * @param dictId
     * @return
     */
    public Dictionary getDictionaryById(int dictId)
    {
	Dictionary theDictionary = null;

	Session session = HibernateUtil.currentSession();
	Transaction tx = session.beginTransaction();
	Query query = session
		.createQuery("select d from Dictionary as d where d.id = :uid");
	query.setInteger("uid", dictId);
	for (Iterator it = query.iterate(); it.hasNext();)
	{
	    theDictionary = (Dictionary)it.next();
	}
	tx.commit();
	HibernateUtil.closeSession();

	return theDictionary;
    }

    /**
     * Used to delete one entry from Dictionary based on Id.
     * 
     * @param dictId
     * @return
     */
    public boolean removeDictionaryById(int dictId)
    {
	boolean removed = false;

	Session session = HibernateUtil.currentSession();
	Transaction tx = session.beginTransaction();
	Query query = session.createQuery("delete from Dictionary where uid = :uid");
	query.setInteger("uid", dictId);

	query.executeUpdate();
	tx.commit();
	HibernateUtil.closeSession();
	removed = true;

	return removed;
    }

    public boolean removeDictionaryEntry(int dictId, String word, String definition)
    {
	boolean removed = false;

	Session session = HibernateUtil.currentSession();
	Transaction tx = session.beginTransaction();
	Dictionary theDictionary = new Dictionary();
	theDictionary.setId(dictId);
	theDictionary.setWord(word);
	theDictionary.setDefinition(definition);

	session.delete(theDictionary);

	tx.commit();
	HibernateUtil.closeSession();
	removed = true;

	return removed;
    }
    
    /**
     * Search the Dictionary for a given word.
     * 
     * @param searchWord
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List searchDictionaryEntry(String searchWord)
    {
	List dictionaryList = new ArrayList();

	Session session = HibernateUtil.currentSession();
	Transaction tx = session.beginTransaction();
	Query query = session
		.createQuery("select d from Dictionary as d where d.word = :words");
	query.setString("words", searchWord);
	for (Iterator it = query.iterate(); it.hasNext();)
	{
	    Dictionary theDictionary = (Dictionary)it.next();
	    dictionaryList.add(theDictionary);
	}
	tx.commit();
	HibernateUtil.closeSession();

	return dictionaryList;
    }
    
    /**
     * Update the definition on Dictionary entry for given Id.
     * 
     * @param uniqueID
     * @param definitionElement
     * @return
     */
    public boolean updateDefinitionDictionary(String uniqueID, String definitionElement)
    {
	boolean updated = false;
	Dictionary theDictionary = null;

	Session session = HibernateUtil.currentSession();
	Transaction tx = session.beginTransaction();
	Query query = session.createQuery("select d from Dictionary as d where d.id = :uid");
	query.setInteger("uid", Integer.parseInt(uniqueID));

	for (Iterator it = query.iterate(); it.hasNext();)
	{
	    theDictionary = (Dictionary)it.next();
	}
	if(theDictionary != null)
	{
	    theDictionary.setDefinition(definitionElement);
	    updated = true;
	    log.info("Update successful id: "+uniqueID+ " word: "+theDictionary.getWord() +" definitionElement: "+theDictionary.getDefinition());
	}
	tx.commit();
	HibernateUtil.closeSession();
	return updated;
    }

}
