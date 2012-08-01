package br.com.stew.smb.page;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import br.com.stew.smb.db.dao.PlayerDao;
import br.com.stew.smb.db.table.PlayerTable;

public class PlayerProcessor
{
	private final WebDriver				browser;
	private final PlayerDao				playerDao;
	private final Map<String, String>	pos	= new HashMap<String, String>();
	private final LoginProcessor		loginProcessor;

	public int tryToInt(String str, int then)
	{
		try
		{
			return Integer.valueOf(str);
		}
		catch(NumberFormatException e)
		{
			return then;
		}
	}

	public int tryToInt(String str)
	{
		return tryToInt(str, -1);
	}

	public PlayerProcessor(WebDriver browser, PlayerDao playerDao, LoginProcessor loginProcessor)
	{
		this.browser = browser;
		this.playerDao = playerDao;
		this.loginProcessor = loginProcessor;

		pos.put("Goleiro", "GL");
		pos.put("Lat. esquerdo", "LE");
		pos.put("Lat. Direito", "LD");
		pos.put("Cent. esquerdo", "DFE");
		pos.put("Central dir.", "DFD");
		pos.put("Central", "DFC");
		pos.put("Meio esquerdo", "ME");
		pos.put("Méio dir", "MD");
		pos.put("Meio Cen. esq.", "MCE");
		pos.put("Meio central direito", "MCD");
		pos.put("Meio central", "MC");
		pos.put("Atacante Esq", "AE");
		pos.put("Atacante dir", "AD");
		pos.put("Ponta Esq.", "PE");
		pos.put("Ponta Dir", "ED");
		pos.put("Atacante Centr.", "AC");
		pos.put("Meio of", "MO");
		pos.put("Meio def", "MDF");
	}

	public void process(int ref)
	{
		PlayerTable row = new PlayerTable();
		row.ref = ref;
		row = playerDao.queryForFirstMatch(row);
		if(row != null)
		{
			return;
		}

		System.out.print(ref + " ");

		browser.navigate().to("http://br.strikermanager.com/jugador.php?id_jugador=" + ref);

		if(browser.findElements(By.cssSelector(".barrajugador")).size() == 1)
		{
			PlayerTable player = new PlayerTable();
			List<WebElement> aux;

			player.team = tryToInt(browser.findElement(By.xpath("//a[contains(@href,'equipo.php?id=')]")).getAttribute("href").replaceAll("[^0-9]", ""));
			player.ref = tryToInt(browser.findElement(By.xpath("//a[contains(@href,'?id_jugador=')]")).getAttribute("href").replaceAll("[^0-9]", ""));
			player.junior = browser.findElements(By.cssSelector(".barrajugador span")).get(1).getText().equals("(Juvenil)");

			player.media = new BigDecimal(browser.findElements(By.cssSelector(".resumenjugador2 .numerico")).get(4).getText().trim().replaceAll("<(.*?)>", ""));

			BigDecimal moral = new BigDecimal(1).add(new BigDecimal(browser.findElements(By.cssSelector(".jugbarranum")).get(11).getText().replaceAll("[^0-9]", "")));

			player.media = player.media.divide(moral, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

			player.posicao = pos.get(browser.findElements(By.cssSelector(".bl td")).get(1).getText());
			player.idade = tryToInt(browser.findElements(By.cssSelector(".bl td")).get(3).getText().replaceAll("[^0-9]", ""));
			player.salario = tryToInt(browser.findElements(By.cssSelector(".bl td")).get(13).getText().replaceAll("[^0-9]", ""));
			player.release = tryToInt(browser.findElements(By.cssSelector(".bl td")).get(15).getText().replaceAll("[^0-9]", ""));

			browser.navigate().to("http://br.strikermanager.com/historial.php?id_jugador=" + ref);

			aux = browser.findElements(By.cssSelector("#page tr"));
			player.dataFirstMedia = aux.get(aux.size() - 1).findElement(By.tagName("td")).getText();
			player.firstMedia = tryToInt(aux.get(aux.size() - 1).findElement(By.cssSelector(".numerico")).getText());

			playerDao.insert(player);
		}
		else
		{
			List<WebElement> elements = browser.findElements(By.cssSelector("#menserror .inside"));
			if(elements.size() == 1 && elements.get(0).getText().contains("aposentou"))
			{
				PlayerTable player = new PlayerTable(0, ref, false, new BigDecimal(0), "", 0, 0, 0);
				playerDao.insert(player);
			}
			else
			{
				loginProcessor.process();
				process(ref);
			}
		}
	}
}
