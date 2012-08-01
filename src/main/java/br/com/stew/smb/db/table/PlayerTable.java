package br.com.stew.smb.db.table;

import java.math.BigDecimal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "PLAYER")
public class PlayerTable implements Table
{
	private static final long	serialVersionUID	= 1L;

	public class Fields
	{
		public static final String	_ID				= "_ID";
		public static final String	TeamID			= "TeamID";
		public static final String	Ref				= "ref";
		public static final String	Junior			= "Junior";
		public static final String	Media			= "Media";
		public static final String	Posicao			= "Posicao";
		public static final String	Idade			= "Idade";
		public static final String	Salario			= "Salario";
		public static final String	Release			= "Release";
		public static final String	FirstMedia		= "FirstMedia";
		public static final String	DataFirstMedia	= "DataFirstMedia";
	}

	@DatabaseField(columnName = Fields._ID, generatedId = true)
	public int			id;

	@DatabaseField(columnName = Fields.TeamID, canBeNull = false)
	public int			team;

	@DatabaseField(columnName = Fields.Ref, canBeNull = false, unique = true)
	public int			ref;

	@DatabaseField(columnName = Fields.Junior, canBeNull = false)
	public boolean		junior;

	@DatabaseField(columnName = Fields.Media, canBeNull = false)
	public BigDecimal	media;

	@DatabaseField(columnName = Fields.Posicao, canBeNull = false)
	public String		posicao;

	@DatabaseField(columnName = Fields.Idade, canBeNull = false)
	public int			idade;

	@DatabaseField(columnName = Fields.Salario, canBeNull = false)
	public int			salario;

	@DatabaseField(columnName = Fields.Release, canBeNull = false)
	public int			release;

	@DatabaseField(columnName = Fields.FirstMedia, canBeNull = false, defaultValue = "0")
	public int			firstMedia;

	@DatabaseField(columnName = Fields.DataFirstMedia, canBeNull = false, defaultValue = "0")
	public String		dataFirstMedia;

	//
	// Constructors
	//
	public PlayerTable()
	{}

	public PlayerTable(int team, int ref, boolean junior, BigDecimal media, String posicao, int idade, int salario, int release)
	{
		this.team = team;
		this.ref = ref;
		this.junior = junior;
		this.media = media;
		this.posicao = posicao;
		this.idade = idade;
		this.salario = salario;
		this.release = release;
	}
}
