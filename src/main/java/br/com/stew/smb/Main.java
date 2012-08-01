package br.com.stew.smb;

import java.util.ArrayList;
import java.util.List;
import br.com.stew.smb.db.MainSchema;
import br.com.stew.smb.db.table.PlayerTable;

public class Main
{
	// 3059
	public static void main(String[] args)
	{

		String connectionUrl = "jdbc:sqlite:D:\\Stew\\Programming\\Projects\\Striker Manager Bot\\db.sqlite";
		Schema schema = new MainSchema(connectionUrl, null, null);

		List<Processor> processorList = new ArrayList<Processor>();

		int start = schema.getDaoHolder().getDao(PlayerTable.class).getFirstHole(PlayerTable.Fields.Ref);
		int last = 3000000;

		for(int i = 0; i <= processorList.size() - 1; i++)
		{
			processorList.get(i).processPlayers(start + i, last + i, processorList.size());
		}
	}
}
