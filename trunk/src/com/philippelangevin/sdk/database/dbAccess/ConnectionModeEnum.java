package com.philippelangevin.sdk.database.dbAccess;

/**
  * <p> Title: {@link ConnectionModeEnum} <p>
  * <p> Description: This enum describes the different connecting mode for databases.
  * 				 These same modes could be supported with XML as well. </p>
  * <p> Company : C-Tec <p>
  * 
  * @author plefebvre
  * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
  */

 /*
  * History
  * ------------------------------------------------
  * Date			Name		BT		Description
  * 2010-06-01		plefebvre
  */

public enum ConnectionModeEnum {
	READ_ONLY,
	READ_WRITE_AUTO_COMMIT,
	READ_WRITE_MANUAL_COMMIT,
	;
}
