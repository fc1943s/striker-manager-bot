package br.com.stew.smb.db;
import br.com.stew.smb.db.dao.PlayerDao;

public class MainSchema extends DesktopSchema
{
	public MainSchema(String connectionUrl, String username, String password)
	{
		super(new SchemaUpgraderImpl(), connectionUrl, username, password);
	}

	@Override
	public void daoRegistration()
	{
		getDaoHolder().registerDao(new PlayerDao(getDaoHolder()));
	}
}
