package exp_web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Executable {

	public static void main(String[] args) throws InterruptedException {
		DesiredCapabilities caps = DesiredCapabilities.chrome();
		driver = new ChromeDriver(caps);
		driver.get("http://www.slumbot.com");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		// register();
		// login();
		// WebElement element =
		// wait.until(ExpectedConditions.elementToBeClickable(By.id("nexthand")));
		WebElement nexthand = driver.findElement(By.id("nexthand"));
		WebElement pot = driver.findElement(By.id("pot"));
		WebElement allin = driver.findElement(By.id("allin"));
		WebElement call = driver.findElement(By.id("call"));
		WebElement actions = driver.findElement(By.id("currentaction"));
		for (int i = 0; i < 1000; i++) {
			nexthand.click();
			System.out.println("======================");
			while (true) {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				wait.until(ExpectedConditions.or(ExpectedConditions.elementToBeClickable(By.id("fold")),
						ExpectedConditions.elementToBeClickable(By.id("check")),
						ExpectedConditions.elementToBeClickable(nexthand)));
				String board = "" + js.executeScript("return board;");
				String myHoleCards = "" + js.executeScript("return ourhi;") + js.executeScript("return ourlo;");
				String oppHoleCards = js.executeScript("return opphi") == null ? "UNKNOWN"
						: "" + js.executeScript("return opphi") + js.executeScript("return opplo;");
				System.out.println(board);
				System.out.println(myHoleCards);
				System.out.println(oppHoleCards);
				System.out.println(actions.getText());
				if (nexthand.isEnabled())
					break;
				if (pot.isEnabled())
					pot.click();
				else if (allin.isEnabled())
					allin.click();
				else if (call.isEnabled())
					call.click();
			}
		}
		//driver.quit();

		// Enter something to search for
		// element.sendKeys("Cheese!");

		// Now submit the form. WebDriver will find the form for us from the
		// element
		// element.submit();

		// Check the title of the page

		// Google's search is rendered dynamically with JavaScript.
		// Wait for the page to load, timeout after 10 seconds
		/*
		 * (new WebDriverWait(driver, 10)).until(new
		 * ExpectedCondition<Boolean>() { public Boolean apply(WebDriver d) {
		 * return d.getTitle().toLowerCase().startsWith("cheese!"); } });
		 */

		//
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
	static String usrname = "test_webpt_1494429494";// "test_webpt_" +
													// (System.currentTimeMillis()
													// / 1000);
	static String password = "password";
}
