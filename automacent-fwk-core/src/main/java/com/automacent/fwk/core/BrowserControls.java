package com.automacent.fwk.core;

import java.util.Set;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import com.automacent.fwk.annotations.Step;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.ThreadUtils;
import com.automacent.fwk.utils.WebUtils;

/**
 * This class provides access to browser functionalities and is extended by the
 * {@link WebTestSteps} class. If browser controls are required in the Test
 * classes, use the {@link WebTestSteps} class.
 * 
 * @author sighil.sivadas
 */
public abstract class BrowserControls {

	private static Logger _logger = Logger.getLogger(BrowserControls.class);

	protected WebDriver driver;

	public BrowserControls() {
		this.driver = BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver();
	}

	/**
	 * Open the URL in browser window
	 * 
	 * @param url URL of the application to load
	 */
	@Step
	public void openUrl(String url) {
		driver.get(url);
	}

	// Window -------------------------------------------------------

	/**
	 * Switch to window specified by the window handle
	 * 
	 * @param windowHandle Selenium window handle
	 */
	@Step
	public void switchToWindow(String windowHandle) {
		driver.switchTo().window(windowHandle);
	}

	/**
	 * Switch to window specified by the index
	 * 
	 * @param index Index of window
	 */
	@Step
	public void switchToWindow(int index) {
		Set<String> windowHandles = driver.getWindowHandles();
		Assert.assertTrue(windowHandles.size() > index, String.format(
				"The number of window handles %s does not match the provided index %s", windowHandles.size(), index));
		int count = 0;
		for (String windowHandle : windowHandles)
			if (count++ == index) {
				driver.switchTo().window(windowHandle);
				break;
			}
	}

	/**
	 * Switch to the parent window. [Window of index 0].
	 */
	@Step
	public void switchToParentWindow() {
		Set<String> windowHandles = driver.getWindowHandles();
		if (windowHandles.size() > 1) {
			for (String windowHandle : windowHandles) {
				switchToWindow(windowHandle);
				WebUtils.handleCertificateError(driver);
				break;
			}
		} else {
			switchToWindow(windowHandles.iterator().next());
		}
		ThreadUtils.sleepFor(5);
	}

	/**
	 * Close all the child windows and then switch to the parent window
	 */
	@Step
	public void closeAllChildWindows() {
		Set<String> windowHandles = driver.getWindowHandles();
		int count = 0;
		for (String windowHandle : windowHandles)
			if (count++ != 0) {
				driver.switchTo().window(windowHandle);
				driver.close();
			}
		switchToParentWindow();
	}

	/**
	 * Get the open window count
	 * 
	 * @return Window count
	 */
	@Step
	public int getWindowCount() {
		return driver.getWindowHandles().size();
	}

	/**
	 * Open a new window. This uses the keyboard short cut CTL+n
	 */
	@Step
	public void openNewWindow() {
		new Actions(driver).keyDown(Keys.LEFT_CONTROL).sendKeys("n").keyUp(Keys.LEFT_CONTROL).build().perform();
		ThreadUtils.sleepFor(5);
	}

	/**
	 * Closes the current window on which driver has control.
	 */
	@Step
	public void closeCurrentWindow() {
		driver.close();
	}

	/**
	 * Check if a new window is open. This method assumes that the new window is of
	 * index 1. [Index starts from 0].
	 * 
	 * @param timeoutInSeconds Time to wait for new window
	 * @return true if found
	 */
	@Step
	protected boolean isNewWindowOpen(int timeoutInSeconds) {
		boolean isWindowOpen = false;
		int count = 0;
		while (count++ < timeoutInSeconds / 6) {
			if (driver.getWindowHandles().size() >= 2) {
				isWindowOpen = true;
				break;
			}
			_logger.debug("New window not found. Rery count - " + count);
			ThreadUtils.sleepFor(6);
		}
		return isWindowOpen;
	}

	// Frame ----------------------------------------------

	/**
	 * Switch to the default or top level frame
	 */
	@Step
	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
	}

	// ----------------------------------------------------

	/**
	 * Click back button of the browser.
	 */
	@Step
	protected void navigateBackInBrowserHistory() {
		driver.navigate().back();
	}

	/**
	 * Refresh the browser
	 */
	@Step
	protected void refreshBrowser() {
		driver.navigate().refresh();
	}

	/**
	 * Get the Page source
	 * 
	 * @return Page source
	 */
	@Step
	public String getPageSource() {
		return driver.getPageSource();
	}

	/**
	 * Click escape button. Useful for closing wizards
	 */
	@Step
	public void clickEscapeButton() {
		new Actions(driver).sendKeys(Keys.ESCAPE).build().perform();
	}

	/**
	 * Click enter button.
	 */
	@Step
	public void clickEnterButton() {
		new Actions(driver).sendKeys(Keys.ENTER).build().perform();
	}

}
