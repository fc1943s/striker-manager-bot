package br.com.stew.smb.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginProcessor
{
	private final String	username;
	private final String	password;
	private final WebDriver	browser;

	public LoginProcessor(WebDriver browser, String username, String password)
	{
		this.username = username;
		this.password = password;
		this.browser = browser;
	}

	public void process()
	{
		if(browser.findElements(By.name("alias")).size() == 0)
		{
			browser.navigate().to("http://br.strikermanager.com");
		}
		if(browser.findElements(By.name("alias")).size() == 1)
		{
			WebElement element;

			element = browser.findElement(By.name("alias"));
			element.sendKeys(username);
			element = browser.findElement(By.name("pass"));
			element.sendKeys(password);

			element.submit();
		}
		else
		{
			throw new RuntimeException("Error processing login. URL: " + browser.getCurrentUrl());
		}
	}
}
