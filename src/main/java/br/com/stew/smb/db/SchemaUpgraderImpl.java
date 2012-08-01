package br.com.stew.smb.db;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.stew.smb.db.dao.CfgDao;
import br.com.stew.smb.db.table.CfgTable;

import com.j256.ormlite.misc.TransactionManager;

// TODO Make generic
public final class SchemaUpgraderImpl implements SchemaUpgrader
{
	private static final Logger	logger	= LoggerFactory.getLogger(SchemaUpgraderImpl.class);

	public SchemaUpgraderImpl()
	{}

	@Override
	public void upgrade(final Schema schema)
	{
		if(!schema.getDaoHolder().hasDao(CfgTable.class))
		{
			schema.getDaoHolder().registerDao(new CfgDao(schema.getDaoHolder()));
		}

		schema.getDaoHolder().createRegisteredDaoTables();

		CfgDao cfgDao = (CfgDao)schema.getDaoHolder().getDao(CfgTable.class);

		int oldVersion = -1;

		CfgTable dbVersionCfg = cfgDao.getEntry(CfgDao.CfgKey.dbVersion);
		if(dbVersionCfg != null)
		{
			oldVersion = Integer.valueOf(dbVersionCfg.value);
		}
		else
		{
			logger.debug("dbVersion not found. Setting to -1.");
		}

		if(oldVersion < -1)
		{
			throw new RuntimeException("Invalid oldVersion number: " + oldVersion);
		}

		logger.debug("Upgrading schema. oldVersion=" + oldVersion);

		final Map<Integer, List<SimpleEntry<Method, Object>>> upgrades = new TreeMap<Integer, List<SimpleEntry<Method, Object>>>();

		List<Object> upgradeContainers = new ArrayList<Object>();

		for(Dao<?> v : schema.getDaoHolder().getDaoList())
		{
			upgradeContainers.add(v);
		}

		upgradeContainers.add(schema);

		for(Object v : upgradeContainers)
		{
			logger.debug("Scanning container: " + v.getClass().getName());
			for(Method v2 : v.getClass().getDeclaredMethods())
			{
				if(v2.getName().startsWith("upgrade") && !v2.getName().endsWith("upgrade"))
				{
					String upgradeStr = v2.getName().split("upgrade")[1];
					if(upgradeStr.matches("^[0-9]+$"))
					{
						int upgradeNumber = Integer.valueOf(upgradeStr);

						if(v2.getParameterTypes().length == 0)
						{
							List<SimpleEntry<Method, Object>> methods = upgrades.get(upgradeNumber);
							if(methods == null)
							{
								methods = new ArrayList<SimpleEntry<Method, Object>>();
								upgrades.put(upgradeNumber, methods);
							}
							methods.add(new SimpleEntry<Method, Object>(v2, v));
							continue;
						}
					}
					logger.debug("Invalid upgrade method found: " + v2.toString());
				}
			}
		}

		if(upgrades.size() == 0)
		{
			if(oldVersion == -1)
			{
				cfgDao.setEntry(CfgDao.CfgKey.dbVersion, "0");
			}
			logger.debug("No upgrades registered found. Skipping.");
			return;
		}

		int lastUpgradeVersion = (Integer)upgrades.keySet().toArray()[upgrades.size() - 1];

		if(oldVersion == lastUpgradeVersion)
		{
			logger.debug("No upgrades left to run. Skipping.");
			return;
		}

		if(oldVersion > lastUpgradeVersion)
		{
			logger.debug("Db version " + oldVersion + ", but found only upgrades until version " + lastUpgradeVersion + ". Saving and skipping.");
			cfgDao.setEntry(CfgDao.CfgKey.dbVersion, String.valueOf(lastUpgradeVersion));
			return;
		}

		for(final Integer v : upgrades.keySet())
		{
			if(oldVersion >= 0 && v <= oldVersion)
			{
				continue;
			}

			if((v == 0 && oldVersion == -1) || (v > 0 && oldVersion >= 0))
			{
				logger.debug("Running upgrades of version " + v);

				try
				{
					TransactionManager.callInTransaction(schema.getDaoHolder().getConnectionSource(), new Callable<Void>()
					{
						@Override
						public Void call()
						{
							if(v == 0)
							{
								logger.debug("upgrade 0 found and oldVersion = -1. Recreating tables.");
								schema.getDaoHolder().dropRegisteredDaoTables();
								schema.getDaoHolder().createRegisteredDaoTables();
							}

							for(SimpleEntry<Method, Object> v2 : upgrades.get(v))
							{
								logger.debug("Container: " + v2.getKey().getDeclaringClass().getName());
								try
								{
									v2.getKey().invoke(v2.getValue());
								}
								catch(Exception e)
								{
									throw new RuntimeException("Error while calling upgrade number " + v + " from container " + v2.getKey().getDeclaringClass().getName(), e);
								}
							}

							return null;
						}
					});
				}
				catch(SQLException e)
				{
					throw new RuntimeException("Error in transaction.", e);
				}
			}
			Integer newVersion = v;
			if(oldVersion == -1)
			{
				newVersion = lastUpgradeVersion;
			}

			logger.debug("Saving the following newVersion: " + newVersion);

			cfgDao.setEntry(CfgDao.CfgKey.dbVersion, String.valueOf(newVersion));

			if(oldVersion == -1)
			{
				logger.debug("Upgrade 0 (Data population) done and remaining upgrades skipped.");
				break;
			}
		}
	}
}
