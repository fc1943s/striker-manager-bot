package br.com.stew.smb.db.dao;
import br.com.stew.smb.db.table.PlayerTable;

public class PlayerDao extends BaseDao<PlayerTable>
{
	public PlayerDao(DaoHolder daoHolder)
	{
		super(daoHolder);
	}

	public int insert(PlayerTable row)
	{
		return dao.insert(row);
	}

	public int delete(PlayerTable row)
	{
		return dao.delete(row);
	}

	public int update(PlayerTable row)
	{
		return dao.update(row);
	}
}
