/*
 * Created on Feb 20, 2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.assign;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate Util class to initialize Session Factory and reuse it.
 * Also opens session per thread and reuses it to increase efficiency.
 * 
 * @author alif
 * 
 * 
 */
public class HibernateUtil
{
    private static Logger log = Logger.getLogger("dictionary-applogger"); 
    private static final SessionFactory sessionFactory;

    static
    {
	try
	{
	    // Create the SessionFactory
	    sessionFactory = new Configuration().configure()
		    .buildSessionFactory();
	}
	catch (Throwable ex)
	{
	    // Make sure you log the exception, as it might be swallowed
	    log.error("Initial SessionFactory creation failed. ", ex);
	    throw new ExceptionInInitializerError(ex);
	}
    }

    public static final ThreadLocal<Session> session = new ThreadLocal<Session>();

    public static Session currentSession()
    {
	Session s = (Session) session.get();
	// Open a new Session, if this Thread has none yet
	if (s == null)
	{
	    s = sessionFactory.openSession();
	    session.set(s);
	}
	return s;
    }

    public static void closeSession()
    {
	Session s = (Session) session.get();
	if (s != null)
	    s.close();
	session.set(null);
    }

}
