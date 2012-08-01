package br.com.stew.smb.db.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "CFG")
public class CfgTable implements Table
{
	private static final long	serialVersionUID	= 1L;

	public class Fields
	{
		public static final String	_ID		= "_ID";
		public static final String	Key		= "Key";
		public static final String	Value	= "Value";
	}

	@DatabaseField(columnName = Fields._ID, generatedId = true)
	public int		id;

	@DatabaseField(columnName = Fields.Key, canBeNull = false, unique = true)
	public String	key;

	@DatabaseField(columnName = Fields.Value, canBeNull = false)
	public String	value;

	//
	// Constructors
	//
	public CfgTable()
	{}

	public CfgTable(String key, String value)
	{
		this.key = key;
		this.value = value;
	}
}
