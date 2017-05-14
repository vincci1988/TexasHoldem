package exp_web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebHUNLAPI {

	public WebHUNLAPI(int maxGameCnt) {
		DesiredCapabilities caps = DesiredCapabilities.chrome();
		driver = new ChromeDriver(caps);
		driver.get("http://www.slumbot.com");
		js = (JavascriptExecutor) driver;
		wait = new WebDriverWait(driver, 10);
		nexthand = driver.findElement(By.id("nexthand"));
		fold = driver.findElement(By.id("fold"));
		check = driver.findElement(By.id("check"));
		call = driver.findElement(By.id("call"));
		bet = driver.findElement(By.id("arbbet"));
		bsize = driver.findElement(By.id("betsize"));
		allin = driver.findElement(By.id("allin"));
		this.maxGameCnt = maxGameCnt;
		actCursor = 0;
		oppmove = 0;
	}

	public void deal() {
		wait.until(ExpectedConditions.elementToBeClickable(nexthand));
		nexthand.click();
	}

	public String getOppHoleCards() {
		wait.until(ExpectedConditions.elementToBeClickable(nexthand));
		return format("" + js.executeScript("return opphi.concat(opplo);"));
	}

	public String getAgentHoleCards() {
		wait.until(ExpectedConditions.or(ExpectedConditions.elementToBeClickable(nexthand),
				ExpectedConditions.elementToBeClickable(fold)));
		return format("" + js.executeScript("return ourhi.concat(ourlo);"));
	}

	public String getBoard() {
		wait.until(ExpectedConditions.or(ExpectedConditions.elementToBeClickable(nexthand),
				ExpectedConditions.elementToBeClickable(fold), ExpectedConditions.elementToBeClickable(check)));
		return format("" + js.executeScript("return board;"));
	}

	public char getActtion() {
		wait.until(ExpectedConditions.or(ExpectedConditions.elementToBeClickable(nexthand),
				ExpectedConditions.elementToBeClickable(fold), ExpectedConditions.elementToBeClickable(check)));
		String actions = "" + js.executeScript("return currentaction;");
		// System.out.println(actions + " " + actCursor + " " + oppmove);
		for (int i = actCursor; i < actions.length(); i++) {
			char action = actions.charAt(i);
			if (action == 'b' || action == 'c' || action == 'k' || action == 'f') {
				if (oppmove == 0) {
					actCursor = i + 1;
					return action;
				}
				oppmove--;
			}
		}
		return 'f';
	}

	public void fold() {
		fold.click();
	}

	public void check() {
		if (check.isEnabled())
			check.click();
		else
			call.click();
	}

	public void call() {
		call.click();
	}

	public void raise(int amt) {
		bsize.sendKeys("" + amt);
		bet.click();
	}

	public void allin() {
		if (allin.isEnabled())
			allin.click();
		else
			call.click();
	}

	public int getBet() {
		return Integer.parseInt("" + js.executeScript("return oppbet;"));
	}

	public void quit() {
		driver.quit();
	}
	
	public void oppmoved() {
		oppmove++;
	}

	public void reset() {
		actCursor = 0;
		oppmove = 0;
	}

	private String format(String cards) {
		String res = "";
		for (int i = 0; i < cards.length(); i += 2) {
			res += cards.charAt(i + 1);
			res += cards.charAt(i);
		}
		return res;
	}

	WebDriver driver;
	JavascriptExecutor js;
	WebDriverWait wait;
	WebElement nexthand;
	WebElement fold;
	WebElement check;
	WebElement call;
	WebElement bet;
	WebElement bsize;
	WebElement allin;
	int maxGameCnt;
	int actCursor;
	int oppmove;
}
