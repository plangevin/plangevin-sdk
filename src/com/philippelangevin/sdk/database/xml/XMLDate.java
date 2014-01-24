package com.philippelangevin.sdk.database.xml;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Title: {@link XMLDate}
 * Description: This class is used to bypass a problem with the way Timestamp
 * objects are displayed in Java VS XML. The TOs use Timestamp for fields
 * requiring either date or datetime, however when we save in XML we need to
 * know specifically if a field is a date or a datetime (their formats are
 * different). Therefore, XMLDAO verifies whether an object is a XMLDate before
 * printing it, if it is then it will use printXMLDate() instead of toString().
 * TransferableObject takes care of creating the XMLDate objects.
 * Company : C-Tec World
 * 
 * @author plefebvre
 * Copyright: (c) 2009, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2009-04-15	plefebvre				
 * 2009-07-14	ipainchaud			Removed the milliseconds from the format. It is not always 
 * 									available, and certainly not very useful.
 */

public class XMLDate extends Timestamp {
	private static final long serialVersionUID = 2043265899793782423L;
	
	/*
	 * Be careful: DateFormat is not Thread safe!
	 */
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DATE_TIME_FORMAT_SQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat DATE_TIME_FORMAT_XML = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private boolean withTime;
	
	public XMLDate(final String value, boolean withTime) throws ParseException {
		super(0);
		
		this.withTime = withTime;
		SimpleDateFormat dateFormat;
		
		/*
		 * We might be reading the value in either SQL or XML,
		 * with or without date.
		 */
		if (value.contains("T")) {
			dateFormat = DATE_TIME_FORMAT_XML;
		} else if (value.contains(" ")) {
			dateFormat = DATE_TIME_FORMAT_SQL;
		} else {
			dateFormat = DATE_FORMAT;
		}
		
		/*
		 * Be careful: DateFormat is not Thread safe!
		 */
		synchronized (dateFormat) {
			setTime(dateFormat.parse(value).getTime());
		}
	}
	
	public XMLDate(Timestamp timeStamp) throws ParseException {
		
		super(0);
		
		this.withTime = false;
		SimpleDateFormat dateFormat;
		
		/*
		 * We might be reading the value in either SQL or XML,
		 * with or without date.
		 */
		if (timeStamp.toString().contains("T")) {
			dateFormat = DATE_TIME_FORMAT_XML;
		} else if (timeStamp.toString().contains(" ")) {
			dateFormat = DATE_TIME_FORMAT_SQL;
		} else {
			dateFormat = DATE_FORMAT;
		}
		
		/*
		 * Be careful: DateFormat is not Thread safe!
		 */
		synchronized (dateFormat) {
			setTime(dateFormat.parse(timeStamp.toString()).getTime());
		}
	}
	
	public XMLDate(Long time) throws ParseException {
		super(0);
		
		this.withTime = false;
		SimpleDateFormat dateFormat;
		Timestamp timeStamp = new Timestamp(time);
		
		/*
		 * We might be reading the value in either SQL or XML,
		 * with or without date.
		 */
		if (timeStamp.toString().contains("T")) {
			dateFormat = DATE_TIME_FORMAT_XML;
		} else if (timeStamp.toString().contains(" ")) {
			dateFormat = DATE_TIME_FORMAT_SQL;
		} else {
			dateFormat = DATE_FORMAT;
		}
		
		/*
		 * Be careful: DateFormat is not Thread safe!
		 */
		synchronized (dateFormat) {
			setTime(dateFormat.parse(timeStamp.toString()).getTime());
		}
	}
	
	public String printXMLDate() {
		/*
		 * This method is exclusively called in XML mode.
		 */
		SimpleDateFormat dateFormat = (withTime)? DATE_TIME_FORMAT_XML: DATE_FORMAT;
		
		/*
		 * Be careful: DateFormat is not Thread safe!
		 */
		synchronized (dateFormat) {
			return dateFormat.format(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.sql.Timestamp#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object ts) {
		return ts instanceof XMLDate && super.equals(ts);
	}
}
