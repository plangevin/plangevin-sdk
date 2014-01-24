package com.philippelangevin.sdk.database.exception;

import java.sql.SQLException;

/**
 * <p> Title: {@link TONotFoundException} <p>
 * <p> Description: SQLException used to indicate a TO was not found during
 * 					a SQL operation. </p>
 * <p> Company : C-Tec <p>
 *
 * @author plefebvre
 * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2010-12-13	plefebvre
 */
public class TONotFoundException extends SQLException {
	private static final long serialVersionUID = 6589565597257707706L;
	
	public TONotFoundException() {
		super();
	}
	
	public TONotFoundException(String reason) {
		super(reason);
	}
}
