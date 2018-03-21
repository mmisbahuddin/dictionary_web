/**
 * 
 */
package com.assign;

/**
 * Domain object that represent each individual Dictionary entry
 * in the database. 
 * 
 * @author alif
 * 
 */
public class Dictionary
{
    private int id;
    private String word;
    private String definition;

    /**
	 * 
	 */
    public Dictionary()
    {
	super();
    }

    /**
     * @return Returns the id.
     */
    public int getId()
    {
	return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id)
    {
	this.id = id;
    }

    /**
     * @return Returns the word.
     */
    public String getWord()
    {
	return word;
    }

    /**
     * @param word
     *            The word to set.
     */
    public void setWord(String word)
    {
	this.word = word;
    }

    /**
     * @return Returns the definition.
     */
    public String getDefinition()
    {
	return definition;
    }

    /**
     * @param definition
     *            The definition to set.
     */
    public void setDefinition(String definition)
    {
	this.definition = definition;
    }
}
