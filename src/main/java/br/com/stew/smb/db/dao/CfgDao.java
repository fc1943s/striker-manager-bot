package br.com.stew.smb.db.dao;
import br.com.stew.smb.db.table.CfgTable;

public class CfgDao extends BaseDao<CfgTable>
{
	public class CfgKey
	{
		public static final String	dbVersion	= "dbVersion";
	}

	public CfgDao(DaoHolder daoHolder)
	{
		super(daoHolder);
	}

	public CfgTable setEntry(String key, String value)
	{
		CfgTable cfg = new CfgTable(key, null);
		CfgTable match = dao.queryForFirstMatch(cfg);

		value = value == null ? "" : value;

		if(match != null)
		{
			match.value = value;
			dao.update(match);
		}
		else
		{
			cfg.value = value;
			dao.insert(cfg);
		}
		return cfg;
	}

	public CfgTable getEntry(String key)
	{
		CfgTable cfg = new CfgTable(key, null);
		return dao.queryForFirstMatch(cfg);
	}

	public String getValue(String key)
	{
		CfgTable row = getEntry(key);
		if(row == null)
		{
			return "";
		}
		return row.value;
	}
}
