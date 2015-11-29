package be.uantwerpen.iw.ei.se.webdriver.testcases;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by Thomas on 23/11/2015.
 */
public class WebDriverTestCases
{
    private static String baseURL;
    private static WebDriver driver;

    public WebDriverTestCases(String baseURL, WebDriver driver)
    {
        this.baseURL = baseURL;
        this.driver = driver;
    }

    public void loginWithCredentialsThomasHuybrechts()
    {
        //Login page
        driver.get(baseURL + "login");

        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys("thomas.huybrechts");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("login")).click();

        //Wait for main portal page to load
        Wait<WebDriver> wait = new WebDriverWait(driver, 1500);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mainPortalPage")));

        Assert.assertTrue("Title should start with Click-O-Matic. Result: " + driver.getTitle(), driver.getTitle().startsWith("Click-O-Matic"));
    }
    public void createANewUser()
    {
        // open | /users |
        driver.get(baseURL + "/users");
        // click | link=Create user |
        driver.findElement(By.linkText("Create user")).click();
        // type | id=firstName | selenium
        driver.findElement(By.id("firstName")).clear();
        driver.findElement(By.id("firstName")).sendKeys("selenium");
        // type | id=lastName | test
        driver.findElement(By.id("lastName")).clear();
        driver.findElement(By.id("lastName")).sendKeys("test");
        // type | id=userName | test
        driver.findElement(By.id("userName")).clear();
        driver.findElement(By.id("userName")).sendKeys("test");
        // type | id=password | test
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("test");
        // click | css=input.btn.btn-primary |
        driver.findElement(By.cssSelector("input.btn.btn-primary")).click();

        Wait<WebDriver> wait = new WebDriverWait(driver, 1500);
       // wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mainPortalPage")));

        Assert.assertTrue("Title should start with Create user. Result: "  + driver.getTitle(),driver.getTitle().startsWith("Create user"));
    }

    public void editUser()
    {
        driver.get(baseURL + "/users");
        driver.findElement(By.xpath("//tr[4]/td[3]/a/span")).click();
        driver.findElement(By.id("firstName")).clear();
        driver.findElement(By.id("firstName")).sendKeys("Tim");
        driver.findElement(By.id("lastName")).clear();
        driver.findElement(By.id("lastName")).sendKeys("Verstraete");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.cssSelector("button.btn.btn-primary")).click();

        Wait<WebDriver> wait = new WebDriverWait(driver, 1500);
        //wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mainPortalPage")));
        Assert.assertTrue("Title should start with User settings. Result: "  + driver.getTitle(),driver.getTitle().startsWith("User settings"));
    }

    public void deleteUser(){
        driver.get(baseURL + "/");
        driver.findElement(By.xpath("//div[@id='bs-example-navbar-collapse-1']/ul/li[3]/a/span[3]")).click();
        driver.findElement(By.xpath("//tr[4]/td[4]/a/span")).click();

        Wait<WebDriver> wait = new WebDriverWait(driver, 1500);
        Assert.assertTrue("Title should start with User settings. Result: "  + driver.getTitle(),driver.getTitle().startsWith("User settings"));

    }


}
