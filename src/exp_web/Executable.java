package exp_web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ASHE.*;
import advanced_players.*;
import simple_players.*;
import holdem.PlayerBase;

@SuppressWarnings("unused")
public class Executable {

	public static void main(String[] args) throws Exception {
		PlayerBase agent = new WildGambler(1); //Ashe(1);//, AsheParams.GenomeFile, "forest.txt");
		WebHUNLEval test = new WebHUNLEval(agent, 1000, 20);
		test.run();
	}

	static void register() {

		WebElement element = driver.findElement(By.id("register_trigger"));
		element.click();
		element = driver.findElement(By.id("regname"));
		element.sendKeys(usrname);
		element = driver.findElement(By.id("regpw"));
		element.sendKeys(password);
		element = driver.findElement(By.linkText("Register"));
		element.click();
	}

	static void login() {
		WebElement element = driver.findElement(By.id("register_trigger"));
		element = driver.findElement(By.id("login_trigger"));
		element.click();
		element = driver.findElement(By.id("loginname"));
		element.sendKeys(usrname);
		element = driver.findElement(By.id("loginpw"));
		element.sendKeys(password);
		element = driver.findElement(By.linkText("Login"));
		element.click();
	}

	static WebDriver driver;
	final static String usrname = "test_webpt_1494429494";
	final static String password = "password";
	final static int testGameNum = 1000;
}
