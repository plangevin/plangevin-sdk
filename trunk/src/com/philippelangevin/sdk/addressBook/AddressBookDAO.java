package com.philippelangevin.sdk.addressBook;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.philippelangevin.sdk.addressBook.tos.AddressTO;
import com.philippelangevin.sdk.addressBook.tos.AddressTOStructure;
import com.philippelangevin.sdk.addressBook.tos.ContactCategoryTO;
import com.philippelangevin.sdk.addressBook.tos.ContactCategoryTOStructure;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTOIF;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTypeTO;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTypeTOStructure;
import com.philippelangevin.sdk.addressBook.tos.ContactTO;
import com.philippelangevin.sdk.addressBook.tos.ContactTOStructure;
import com.philippelangevin.sdk.addressBook.tos.ContactTypeTO;
import com.philippelangevin.sdk.addressBook.tos.ContactTypeTOStructure;
import com.philippelangevin.sdk.addressBook.tos.EmailTO;
import com.philippelangevin.sdk.addressBook.tos.EmailTOStructure;
import com.philippelangevin.sdk.addressBook.tos.PhoneTO;
import com.philippelangevin.sdk.addressBook.tos.PhoneTOStructure;
import com.philippelangevin.sdk.addressBook.tos.PreferedContactInfoTOStructure;
import com.philippelangevin.sdk.addressBook.tos.Tables;
import com.philippelangevin.sdk.database.connector.DatabaseDefinitionIF;
import com.philippelangevin.sdk.database.dbAccess.AbstractDatabaseDAO;
import com.philippelangevin.sdk.database.dbAccess.ConnectionModeEnum;
import com.philippelangevin.sdk.database.dbAccess.DatabaseConnectionFactory;
import com.philippelangevin.sdk.database.tables.TableInfo;
import com.philippelangevin.sdk.database.transferableObject.ColumnInfo;

public class AddressBookDAO extends AbstractDatabaseDAO {
	
	public AddressBookDAO(DatabaseDefinitionIF databaseDefinition, ConnectionModeEnum connectionMode) throws SQLException {
		super(connectionMode);

		dbAccess = DatabaseConnectionFactory.buildConnection(databaseDefinition);
		
		openConnection() ;
	}
	
	public AddressBookDAO(DatabaseDefinitionIF databaseDefinition, String username, String password, ConnectionModeEnum connectionMode) throws SQLException {
		super(connectionMode);

		dbAccess = DatabaseConnectionFactory.buildConnection(databaseDefinition, username, password);
		
		openConnection() ;
	}


	
	public List<ContactInfoTOIF> getContactInfo(ContactTO currentContactTO, ContactInfoTypeTO contactInfoType) throws SQLException {
		ContactInfoTOIF contactInfo ;
		try {
			contactInfo = (ContactInfoTOIF) Tables.fromTableName(contactInfoType.getTableName()).getTransferableObjectClass().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			
			return null ;
		}
		
		contactInfo.setContactId(currentContactTO.getId()) ;
		
		return selectTOList(contactInfo) ;
	}
	
	
	public void insert(ContactInfoTOIF to) throws SQLException {
		if (to.getId() == null)	{
			String maxQuery = String.format("SELECT MAX (id) AS currentMax FROM %s WHERE contactId = ? AND contactCategory = ?", to.getTOStructure().getRepresentedTable()) ;
			PreparedStatement ps = dbAccess.prepareStatement(maxQuery) ;
			
			ps.setInt(1, to.getContactId()) ;
			ps.setInt(2, to.getContactCategory()) ;
			
			ResultSet rs = ps.executeQuery() ;
			
			if (rs.next())	{
				int currentMax = rs.getInt("currentMax") ;
				to.setId(currentMax + 1) ;
			}
			else	{
				throw new SQLException("Unable to get current max for " + to.toStringCompact()) ;
			}
		}
		
		super.insert(to);
	}
	
	
	public void deleteAllContactTypeForContact(ContactInfoTOIF contactInfo, Integer id) throws SQLException {
		String sql = String.format("DELETE FROM %s WHERE contactId = %d", contactInfo.getTOStructure().getRepresentedTable().toString(), id) ;
		dbAccess.executeUpdate(sql) ;
	}
	
	public Integer getMaxValue(TableInfo representedTable, ColumnInfo<Integer> id) throws SQLException {
		String query = String.format("SELECT MAX(%s) AS maxValue FROM %s", id, representedTable);
		
		ResultSet rs = dbAccess.executeQuery(query) ;
		
		if (rs.next()){
			return rs.getInt(1) ;
		}
		
		return -1 ;
	}



	public void createMainDB()	{
		try {
			dbAccess.setAutoCommit(false) ;
			
			createContactInfoTypeTable() ;
			createContactCategoryTable() ;
			createContactTypeTable() ;
			createPreferedContactInfoTable() ;
			createContactTable() ;
			
			createPhoneTable() ;
			createAddressTable() ;
			createEmailTable() ;
			
			dbAccess.commit() ;
			dbAccess.setAutoCommit(true) ;
			
			insertDummyData() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void insertDummyData() throws SQLException {
		Integer itemId = 1 ;
		ContactTO contactTO = new ContactTO() ;
		
		contactTO.setId(itemId++) ;
		contactTO.setFullName("Philippe Langevin") ;
		contactTO.setContactType(1) ;
		insert(contactTO) ;

		contactTO.setId(itemId++) ;
		contactTO.setFullName("Michel Langevin") ;
		contactTO.setContactType(2) ;
		insert(contactTO) ;

		contactTO.setId(itemId++) ;
		contactTO.setFullName("Patrick Langevin") ;
		contactTO.setContactType(3) ;
		insert(contactTO) ;
		
		contactTO.setId(itemId++) ;
		contactTO.setFullName("Johanne Chaput") ;
		contactTO.setContactType(4) ;
		insert(contactTO) ;

		itemId = 1 ;
		PhoneTO phoneTO = new PhoneTO() ;
		
		phoneTO.setContactId(1) ;
		phoneTO.setId(itemId++) ;	// 1
		phoneTO.setPhoneNumber("514.708.4275") ;
		phoneTO.setContactCategory(1) ;
		super.insert(phoneTO) ;

		phoneTO.setContactId(1) ;
		phoneTO.setId(itemId++) ;	// 2
		phoneTO.setPhoneNumber("450.973.6784") ;
		phoneTO.setContactCategory(3) ;
		super.insert(phoneTO) ;
		
		/*
		 * Reset for a new contactId
		 */
		itemId = 1 ;
		
		phoneTO.setContactId(4) ;
		phoneTO.setId(itemId++) ;	// 1
		phoneTO.setPhoneNumber("450.224.2409") ;
		phoneTO.setContactCategory(2) ;
		super.insert(phoneTO) ;

		phoneTO.setContactId(4) ;
		phoneTO.setId(itemId++) ;	// 2
		phoneTO.setPhoneNumber("514.910.7911") ;
		phoneTO.setContactCategory(1) ;
		super.insert(phoneTO) ;

		
		
		
		itemId = 1 ;
		AddressTO addressTO = new AddressTO() ;
		
		addressTO.setContactId(1) ;
		addressTO.setId(itemId++) ;	// 1
		addressTO.setCivicNumber(314) ;
		addressTO.setStreet("de la Promenade, App 2") ;
		addressTO.setCity("Boisbriand") ;
		addressTO.setZipCode("J7G 1N3") ;
		addressTO.setContactCategory(2) ;
		super.insert(addressTO) ;

		addressTO.setContactId(1) ;
		addressTO.setId(itemId++) ;	//	2
		addressTO.setCivicNumber(2425) ;
		addressTO.setStreet("Michelin") ;
		addressTO.setCity("Laval") ;
		addressTO.setZipCode("H7L 5B9") ;
		addressTO.setContactCategory(3) ;
		super.insert(addressTO) ;
		
		/*
		 * Reset for a new contactId
		 */
		itemId = 1 ;
		
		addressTO.setContactId(4) ;
		addressTO.setId(itemId++) ;	// 1
		addressTO.setCivicNumber(97) ;
		addressTO.setStreet("chemin des Épinettes") ;
		addressTO.setCity("Ste-Anne-des-Lacs") ;
		addressTO.setZipCode("J0R 1B0") ;
		addressTO.setContactCategory(1) ;
		super.insert(addressTO) ;
		
		
		
		itemId = 1 ;
		EmailTO emailTO = new EmailTO() ;
		
		emailTO.setContactId(1) ;
		emailTO.setId(itemId++) ;
		emailTO.setEmail("philippe.langevin@gmail.com") ;
		emailTO.setContactCategory(2) ;
		insert(emailTO) ;

		emailTO.setContactId(1) ;
		emailTO.setId(itemId++) ;
		emailTO.setEmail("plangevin@ctecworld.com") ;
		emailTO.setContactCategory(3) ;
		insert(emailTO) ;
		
		/*
		 * Reset for a new contactId
		 */
		itemId = 1 ;

		emailTO.setContactId(4) ;
		emailTO.setId(itemId++) ;
		emailTO.setEmail("lublanne@hotmail.com") ;
		emailTO.setContactCategory(1) ;
		insert(emailTO) ;
	}


	private void createContactTable() throws SQLException	{
		String tableName = Tables.CONTACT.toString() ;
		dbAccess.executeUpdate(String.format("DROP TABLE IF EXISTS %s;", tableName)) ;
		dbAccess.executeUpdate(String.format("CREATE TABLE %s (%s %s, %s %s, %s %s);", 
									tableName, 
									ContactTOStructure.ColumnNames.id, ContactTOStructure.ColumnNames.id.getMetaData().getSQLDeclarationString() + " PRIMARY KEY",
									ContactTOStructure.ColumnNames.fullName, ContactTOStructure.ColumnNames.fullName.getMetaData().getSQLDeclarationString(),
									ContactTOStructure.ColumnNames.contactType, ContactTOStructure.ColumnNames.contactType.getMetaData().getSQLDeclarationString())) ;

	}

	
	private void createPhoneTable() throws SQLException	{
		String tableName = Tables.PHONE.toString() ;
		
		dbAccess.executeUpdate(String.format("DROP TABLE IF EXISTS %s;", tableName)) ;
		dbAccess.executeUpdate(String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s);", 
									tableName, 
									PhoneTOStructure.ColumnNames.contactId, PhoneTOStructure.ColumnNames.contactId.getMetaData().getSQLDeclarationString(), 
									PhoneTOStructure.ColumnNames.id, PhoneTOStructure.ColumnNames.id.getMetaData().getSQLDeclarationString(),
									PhoneTOStructure.ColumnNames.phoneNumber, PhoneTOStructure.ColumnNames.phoneNumber.getMetaData().getSQLDeclarationString(),
									PhoneTOStructure.ColumnNames.contactCategory, PhoneTOStructure.ColumnNames.contactCategory.getMetaData().getSQLDeclarationString())) ;

	}
	
	private void createAddressTable() throws SQLException	{
		String tableName = Tables.ADDRESS.toString() ;
		
		dbAccess.executeUpdate(String.format("DROP TABLE IF EXISTS %s;", tableName)) ;
		dbAccess.executeUpdate(String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s, %s %s, %s %s, %s %s);", 
									tableName, 
									AddressTOStructure.ColumnNames.contactId, AddressTOStructure.ColumnNames.contactId.getMetaData().getSQLDeclarationString(),
									AddressTOStructure.ColumnNames.id, AddressTOStructure.ColumnNames.id.getMetaData().getSQLDeclarationString(),
									AddressTOStructure.ColumnNames.civicNumber, AddressTOStructure.ColumnNames.civicNumber.getMetaData().getSQLDeclarationString(),
									AddressTOStructure.ColumnNames.street, AddressTOStructure.ColumnNames.street.getMetaData().getSQLDeclarationString(),
									AddressTOStructure.ColumnNames.city, AddressTOStructure.ColumnNames.city.getMetaData().getSQLDeclarationString(),
									AddressTOStructure.ColumnNames.zipCode, AddressTOStructure.ColumnNames.zipCode.getMetaData().getSQLDeclarationString(),
									AddressTOStructure.ColumnNames.contactCategory, AddressTOStructure.ColumnNames.contactCategory.getMetaData().getSQLDeclarationString())) ;

	}
	
	private void createEmailTable() throws SQLException	{
		String tableName = Tables.EMAIL.toString() ;
		
		dbAccess.executeUpdate(String.format("DROP TABLE IF EXISTS %s;", tableName)) ;
		dbAccess.executeUpdate(String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s);", 
									tableName, 
									EmailTOStructure.ColumnNames.contactId, EmailTOStructure.ColumnNames.contactId.getMetaData().getSQLDeclarationString(), 
									EmailTOStructure.ColumnNames.id, EmailTOStructure.ColumnNames.id.getMetaData().getSQLDeclarationString(),
									EmailTOStructure.ColumnNames.email, EmailTOStructure.ColumnNames.email.getMetaData().getSQLDeclarationString(),
									EmailTOStructure.ColumnNames.contactCategory, EmailTOStructure.ColumnNames.contactCategory.getMetaData().getSQLDeclarationString())) ;

	}
	
	private void createPreferedContactInfoTable() throws SQLException {
		String tableName = Tables.PREFEREDCONTACTINFO.toString() ;
		dbAccess.executeUpdate(String.format("DROP TABLE IF EXISTS %s;", tableName)) ;
		dbAccess.executeUpdate(String.format("CREATE TABLE %s (%s %s, %s %s, %s %s);", 
									tableName, 
									PreferedContactInfoTOStructure.ColumnNames.contactId, PreferedContactInfoTOStructure.ColumnNames.contactId.getMetaData().getSQLDeclarationString(),
									PreferedContactInfoTOStructure.ColumnNames.contactInfoType, PreferedContactInfoTOStructure.ColumnNames.contactInfoType.getMetaData().getSQLDeclarationString(),
									PreferedContactInfoTOStructure.ColumnNames.preferedId, PreferedContactInfoTOStructure.ColumnNames.preferedId.getMetaData().getSQLDeclarationString())) ;
	}

	
	private void createContactInfoTypeTable() throws SQLException	{
		String tableName = Tables.CONTACTINFOTYPE.toString() ;
		dbAccess.executeUpdate(String.format("DROP TABLE IF EXISTS %s;", tableName)) ;
		dbAccess.executeUpdate(String.format("CREATE TABLE %s (%s %s, %s %s, %s %s);", 
									tableName, 
									ContactInfoTypeTOStructure.ColumnNames.id, ContactInfoTypeTOStructure.ColumnNames.id.getMetaData().getSQLDeclarationString(), 
									ContactInfoTypeTOStructure.ColumnNames.nameKey, ContactInfoTypeTOStructure.ColumnNames.nameKey.getMetaData().getSQLDeclarationString(), 
									ContactInfoTypeTOStructure.ColumnNames.tableName, ContactInfoTypeTOStructure.ColumnNames.tableName.getMetaData().getSQLDeclarationString())) ;

		Integer itemId = 1 ;
		ContactInfoTypeTO to = new ContactInfoTypeTO() ;
		
		to.setId(itemId++) ;
		to.setNameKey("Phone") ;
		to.setTableName(Tables.PHONE.toString()) ;
		insert(to) ;

		to.setId(itemId++) ;
		to.setNameKey("Address") ;
		to.setTableName(Tables.ADDRESS.toString()) ;
		insert(to) ;

		to.setId(itemId++) ;
		to.setNameKey("Email") ;
		to.setTableName(Tables.EMAIL.toString()) ;
		insert(to) ;
	}
	
	private void createContactTypeTable() throws SQLException	{
		String tableName = Tables.CONTACTTYPE.toString() ;
		dbAccess.executeUpdate(String.format("DROP TABLE IF EXISTS %s;", tableName)) ;
		dbAccess.executeUpdate(String.format("CREATE TABLE %s (%s %s, %s %s);", 
									tableName, 
									ContactTypeTOStructure.ColumnNames.id, ContactTypeTOStructure.ColumnNames.id.getMetaData().getSQLDeclarationString(), 
									ContactTypeTOStructure.ColumnNames.nameKey, ContactTypeTOStructure.ColumnNames.nameKey.getMetaData().getSQLDeclarationString())) ;

		Integer itemId = 1 ;
		ContactTypeTO to = new ContactTypeTO() ;
		
		to.setId(itemId++) ;
		to.setNameKey("Client") ;
		insert(to) ;

		to.setId(itemId++) ;
		to.setNameKey("Partner") ;
		insert(to) ;

		to.setId(itemId++) ;
		to.setNameKey("Supplier") ;
		insert(to) ;

		to.setId(itemId++) ;
		to.setNameKey("Other") ;
		insert(to) ;
	}
	
	private void createContactCategoryTable() throws SQLException	{
		String tableName = Tables.CONTACTCATEGORY.toString() ;
		dbAccess.executeUpdate(String.format("DROP TABLE IF EXISTS %s;", tableName)) ;
		dbAccess.executeUpdate(String.format("CREATE TABLE %s (%s %s, %s %s);", 
									tableName, 
									ContactCategoryTOStructure.ColumnNames.id, ContactCategoryTOStructure.ColumnNames.id.getMetaData().getSQLDeclarationString(), 
									ContactCategoryTOStructure.ColumnNames.nameKey,ContactCategoryTOStructure.ColumnNames.nameKey.getMetaData().getSQLDeclarationString())) ;
		
		int itemId = 1 ;
		ContactCategoryTO pt = new ContactCategoryTO() ;
		
		pt.setId(itemId++) ;
		pt.setNameKey("Mobile");
		insert(pt) ;

		pt.setId(itemId++) ;
		pt.setNameKey("Home");
		insert(pt) ;

		pt.setId(itemId++) ;
		pt.setNameKey("Work");
		insert(pt) ;

		pt.setId(itemId++) ;
		pt.setNameKey("Cottage");
		insert(pt) ;
		
		pt.setId(itemId++) ;
		pt.setNameKey("Other");
		insert(pt) ;
	}
}
