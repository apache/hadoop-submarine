/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.submarine.integration;

import org.apache.submarine.AbstractSubmarineIT;
import org.apache.submarine.WebDriverManager;
import org.apache.submarine.SubmarineITUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

public class teamIT extends AbstractSubmarineIT {

  public final static Logger LOG = LoggerFactory.getLogger(teamIT.class);

  @BeforeClass
  public static void startUp(){
    LOG.info("[Testcase]: teamIT");
    driver =  WebDriverManager.getWebDriver();
  }

  @AfterClass
  public static void tearDown(){
    driver.quit();
  }

  @Test
  public void teamTest() throws Exception {
    // Login
    LOG.info("Login");
    pollingWait(By.cssSelector("input[ng-reflect-name='userName']"), MAX_BROWSER_TIMEOUT_SEC).sendKeys("admin");
    pollingWait(By.cssSelector("input[ng-reflect-name='password']"), MAX_BROWSER_TIMEOUT_SEC).sendKeys("admin");
    clickAndWait(By.cssSelector("button[class='login-form-button ant-btn ant-btn-primary']"));
    pollingWait(By.cssSelector("a[routerlink='/workbench/dashboard']"), MAX_BROWSER_TIMEOUT_SEC);

    // Routing to workspace
    pollingWait(By.xpath("//span[contains(text(), \"Workspace\")]"), MAX_BROWSER_TIMEOUT_SEC).click();
    Assert.assertEquals(driver.getCurrentUrl(), "http://localhost:8080/workbench/workspace");

    //Test team part
    pollingWait(By.xpath("//li[contains(text(), \"Team\")]"), MAX_BROWSER_TIMEOUT_SEC).click();
    Assert.assertEquals(pollingWait(By.xpath("//div[@id='teamDiv']"), MAX_BROWSER_TIMEOUT_SEC).isDisplayed(), true);

    clickAndWait(By.cssSelector("button[id='btnAddTeam']"));
    pollingWait(By.xpath("//input[@id='inputNewTeamName']"), MAX_BROWSER_TIMEOUT_SEC).sendKeys("NewTeamNameTest");
    pollingWait(By.xpath("//input[@id='inputNewTeamOwner']"), MAX_BROWSER_TIMEOUT_SEC).sendKeys("NewTeamOwnerTest");
    clickAndWait(By.cssSelector("button[id='submitNewTeamBtn']"));
    Assert.assertEquals(pollingWait(By.xpath("//td[contains(., 'NewTeamNameTest')]"), MAX_BROWSER_TIMEOUT_SEC).isDisplayed(), true);
  }
}
