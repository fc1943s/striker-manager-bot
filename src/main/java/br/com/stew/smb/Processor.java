package br.com.stew.smb;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import br.com.stew.smb.db.dao.PlayerDao;
import br.com.stew.smb.db.table.PlayerTable;
import br.com.stew.smb.page.LoginProcessor;
import br.com.stew.smb.page.PlayerProcessor;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class Processor
{
	private WebDriver				browser;
	private final PlayerDao			playerDao;
	private PlayerProcessor			playerProcessor;
	private LoginProcessor			loginProcessor;
	private final String			username;
	private final String			password;
	private final FirefoxProfile	profile;

	public Processor(Schema schema, String username, String password)
	{
		this.username = username;
		this.password = password;

		playerDao = (PlayerDao)schema.getDaoHolder().getDao(PlayerTable.class);

		profile = new FirefoxProfile();
		profile.setPreference("permissions.default.stylesheet", 2);
		profile.setPreference("permissions.default.image", 2);
		profile.setPreference("javascript.enabled", false);

		createBrowser();
	}

	private void createBrowser()
	{
		BrowserVersion.setDefault(BrowserVersion.FIREFOX_3_6);
		browser = new HtmlUnitDriver(false);
		//browser = new FirefoxDriver(profile);
		System.out.println("Handle: " + browser.getWindowHandle());

		loginProcessor = new LoginProcessor(browser, username, password);
		playerProcessor = new PlayerProcessor(browser, playerDao, loginProcessor);
	}

	public void processPlayers(final int start, final int last, final int increment)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				for(int i = start; i <= last; i += increment)
				{
					try
					{
						playerProcessor.process(i);
					}
					catch(UnreachableBrowserException e)
					{
						createBrowser();
						System.err.println(ExceptionUtils.getFullStackTrace(e));
					}
					catch(RuntimeException e)
					{
						i -= increment;
						System.err.println(ExceptionUtils.getFullStackTrace(e));
					}
					catch(Exception e)
					{
						createBrowser();
						System.err.println(ExceptionUtils.getFullStackTrace(e));
					}
				}
			}
		}.start();
	}
}
