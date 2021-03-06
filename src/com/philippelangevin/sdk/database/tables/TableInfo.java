package com.philippelangevin.sdk.database.tables;

import com.philippelangevin.sdk.database.transferableObject.TransferableObject;

/**
 * <p> Title: {@link TableInfo} <p>
 * <p> Description:  </p>
 * <p> Company : C-Tec world <p>
 * 
 * @author israel israel@ctecworld.com
 * Copyright: (c) 2009, C-Tec Inc. - All rights reserved
 */
/*
 * History
 * ------------------------------------------------
 * Date       Name        BT      Description
 * 2009-05-19 israel              initial Revision
 */
public interface TableInfo {
	public Class<? extends TransferableObject> getTransferableObjectClass();
/*
  _   _       _   _     _               ____    _                 _                           _
 | \ | | ___ | |_| |__ (_)_ __   __ _  |___ \  (_)_ __ ___  _ __ | | ___ _ __ ___   ___ _ __ | |_
 |  \| |/ _ \| __| '_ \| | '_ \ / _` |   __) | | | '_ ` _ \| '_ \| |/ _ \ '_ ` _ \ / _ \ '_ \| __|
 | |\  | (_) | |_| | | | | | | | (_| |  / __/  | | | | | | | |_) | |  __/ | | | | |  __/ | | | |_
 |_| \_|\___/ \__|_| |_|_|_| |_|\__, | |_____| |_|_| |_| |_| .__/|_|\___|_| |_| |_|\___|_| |_|\__|
                                |___/                      |_|
 This interface is a sort of a tag for table enums to help identify these objects as table
 identifier.
 */
}
