/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.submarine.client.cli.runjob.mxnet;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.submarine.client.cli.YamlConfigTestUtils;
import org.apache.submarine.client.cli.param.runjob.MXNetRunJobParameters;
import org.apache.submarine.client.cli.param.runjob.RunJobParameters;
import org.apache.submarine.client.cli.param.yaml.YamlParseException;
import org.apache.submarine.client.cli.runjob.RunJobCli;
import org.apache.submarine.client.cli.runjob.RunJobCliParsingCommonTest;
import org.apache.submarine.commons.runtime.conf.SubmarineLogs;
import org.apache.submarine.commons.runtime.exception.SubmarineRuntimeException;
import org.apache.submarine.commons.runtime.resource.ResourceUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

/**
 * Test class that verifies the correctness of MXNet
 * YAML configuration parsing.
 */
public class RunJobCliParsingMXNetYamlTest {
  private static final String OVERRIDDEN_PREFIX = "overridden_";
  private static final String DIR_NAME = "runjob-mxnet-yaml";
  private File yamlConfig;
  private static Logger LOG = LoggerFactory.getLogger(
    RunJobCliParsingMXNetYamlTest.class);

  @Before
  public void before() {
    SubmarineLogs.verboseOff();
  }

  @After
  public void after() {
    YamlConfigTestUtils.deleteFile(yamlConfig);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private void verifyBasicConfigValues(RunJobParameters jobRunParameters) {
    verifyBasicConfigValues(jobRunParameters,
        ImmutableList.of("env1=env1Value", "env2=env2Value"));
  }

  private void verifyBasicConfigValues(RunJobParameters jobRunParameters,
      List<String> expectedEnvs) {
    assertEquals("testInputPath", jobRunParameters.getInputPath());
    assertEquals("testCheckpointPath", jobRunParameters.getCheckpointPath());
    assertEquals("testDockerImage", jobRunParameters.getDockerImageName());

    assertNotNull(jobRunParameters.getLocalizations());
    assertEquals(2, jobRunParameters.getLocalizations().size());

    assertNotNull(jobRunParameters.getQuicklinks());
    assertEquals(2, jobRunParameters.getQuicklinks().size());

    assertTrue(SubmarineLogs.isVerbose());
    assertTrue(jobRunParameters.isWaitJobFinish());

    for (String env : expectedEnvs) {
      assertTrue(String.format(
          "%s should be in env list of jobRunParameters!", env),
          jobRunParameters.getEnvars().contains(env));
    }
  }

  private void verifyPsValues(RunJobParameters jobRunParameters,
      String prefix) {
    assertTrue(RunJobParameters.class + " must be an instance of " +
            MXNetRunJobParameters.class,
        jobRunParameters instanceof MXNetRunJobParameters);
    MXNetRunJobParameters mxNetParams =
        (MXNetRunJobParameters) jobRunParameters;

    assertEquals(4, mxNetParams.getNumPS());
    assertEquals(prefix + "testLaunchCmdPs", mxNetParams.getPSLaunchCmd());
    assertEquals(prefix + "testDockerImagePs",
        mxNetParams.getPsDockerImage());
    assertEquals(Resources.createResource(20500, 34),
        mxNetParams.getPsResource());
  }

  private void verifySchedulerValues(RunJobParameters jobRunParameters,
      String prefix) {
    assertTrue(RunJobParameters.class + " must be an instance of " +
        MXNetRunJobParameters.class, jobRunParameters instanceof MXNetRunJobParameters);
    MXNetRunJobParameters mxNetParams = (MXNetRunJobParameters) jobRunParameters;
    assertEquals(1, mxNetParams.getNumSchedulers());
    assertEquals(prefix + "testLaunchCmdScheduler",
        mxNetParams.getSchedulerLaunchCmd());
    assertEquals(prefix + "testDockerImageScheduler", mxNetParams.getSchedulerDockerImage());
    assertEquals(Resources.createResource(10240, 16),
        mxNetParams.getSchedulerResource());
  }

  private void verifyWorkerValues(RunJobParameters jobRunParameters, String prefix) {
    MXNetRunJobParameters mxNetParams =
        verifyWorkerCommonValues(jobRunParameters, prefix);
    assertEquals(Resources.createResource(20480, 32),
        mxNetParams.getWorkerResource());
  }

  private MXNetRunJobParameters verifyWorkerCommonValues(
          RunJobParameters jobRunParameters, String prefix) {
    assertTrue(RunJobParameters.class + " must be an instance of " +
                    MXNetRunJobParameters.class,
            jobRunParameters instanceof MXNetRunJobParameters);
    MXNetRunJobParameters mxNetParams =
            (MXNetRunJobParameters) jobRunParameters;

    assertEquals(3, mxNetParams.getNumWorkers());
    assertEquals(prefix + "testLaunchCmdWorker",
            mxNetParams.getWorkerLaunchCmd());
    assertEquals(prefix + "testDockerImageWorker",
            mxNetParams.getWorkerDockerImage());
    return mxNetParams;
  }

  private void verifyWorkerValuesWithGpu(RunJobParameters jobRunParameters, String prefix) {
    MXNetRunJobParameters mxNetParams =
        verifyWorkerCommonValues(jobRunParameters, prefix);
    Resource workResource = Resources.createResource(20480, 32);
    ResourceUtils.setResource(workResource, ResourceUtils.GPU_URI, 2);
    assertEquals(workResource, mxNetParams.getWorkerResource());
  }

  private void verifySecurityValues(RunJobParameters jobRunParameters) {
    assertEquals("keytabPath", jobRunParameters.getKeytab());
    assertEquals("testPrincipal", jobRunParameters.getPrincipal());
    assertTrue(jobRunParameters.isDistributeKeytab());
  }

  @Test
  public void testValidYamlParsing() throws Exception {
    RunJobCli runJobCli = new RunJobCli(RunJobCliParsingCommonTest.getMockClientContext());
    Assert.assertFalse(SubmarineLogs.isVerbose());

    yamlConfig = YamlConfigTestUtils.createTempFileWithContents(
        DIR_NAME + "/valid-config.yaml");
    runJobCli.run(
        new String[] {"-f", yamlConfig.getAbsolutePath(), "--verbose"});
    RunJobParameters jobRunParameters = runJobCli.getRunJobParameters();
    verifyBasicConfigValues(jobRunParameters);
    verifyPsValues(jobRunParameters, "");
    verifySchedulerValues(jobRunParameters, "");
    verifyWorkerValues(jobRunParameters, "");
    verifySecurityValues(jobRunParameters);
  }

  @Test
  public void testValidGpuYamlParsing() throws Exception {
    try {
      ResourceUtils.configureResourceType(ResourceUtils.GPU_URI);
    } catch (SubmarineRuntimeException e) {
      LOG.info("The hadoop dependency doesn't support gpu resource, " +
          "so just skip this test case.");
      return;
    }

    RunJobCli runJobCli = new RunJobCli(RunJobCliParsingCommonTest.getMockClientContext());
    Assert.assertFalse(SubmarineLogs.isVerbose());

    yamlConfig = YamlConfigTestUtils.createTempFileWithContents(
        DIR_NAME + "/valid-gpu-config.yaml");
    runJobCli.run(
        new String[] {"-f", yamlConfig.getAbsolutePath(), "--verbose"});

    RunJobParameters jobRunParameters = runJobCli.getRunJobParameters();
    verifyBasicConfigValues(jobRunParameters);
    verifyPsValues(jobRunParameters, "");
    verifySchedulerValues(jobRunParameters, "");
    verifyWorkerValuesWithGpu(jobRunParameters, "");
    verifySecurityValues(jobRunParameters);
  }

  @Test
  public void testRoleOverrides() throws Exception {
    RunJobCli runJobCli = new RunJobCli(RunJobCliParsingCommonTest.getMockClientContext());
    Assert.assertFalse(SubmarineLogs.isVerbose());

    yamlConfig = YamlConfigTestUtils.createTempFileWithContents(
        DIR_NAME + "/valid-config-with-overrides.yaml");

    runJobCli.run(
        new String[]{"-f", yamlConfig.getAbsolutePath(), "--verbose"});

    RunJobParameters jobRunParameters = runJobCli.getRunJobParameters();
    verifyBasicConfigValues(jobRunParameters);
    verifyPsValues(jobRunParameters, OVERRIDDEN_PREFIX);
    verifySchedulerValues(jobRunParameters, OVERRIDDEN_PREFIX);
    verifyWorkerValues(jobRunParameters, OVERRIDDEN_PREFIX);
    verifySecurityValues(jobRunParameters);
  }

  @Test
  public void testMissingPrincipalUnderSecuritySection() throws Exception {
    RunJobCli runJobCli = new RunJobCli(RunJobCliParsingCommonTest.getMockClientContext());
    yamlConfig = YamlConfigTestUtils.createTempFileWithContents(
        DIR_NAME + "/security-principal-is-missing.yaml");
    runJobCli.run(
        new String[]{"-f", yamlConfig.getAbsolutePath(), "--verbose"});

    RunJobParameters jobRunParameters = runJobCli.getRunJobParameters();
    verifyBasicConfigValues(jobRunParameters);
    verifyPsValues(jobRunParameters, "");
    verifySchedulerValues(jobRunParameters, "");
    verifyWorkerValues(jobRunParameters, "");

    //Verify security values
    assertEquals("keytabPath", jobRunParameters.getKeytab());
    assertNull("Principal should be null!", jobRunParameters.getPrincipal());
    assertTrue(jobRunParameters.isDistributeKeytab());
  }

  @Test
  public void testMissingEnvs() throws Exception {
    RunJobCli runJobCli = new RunJobCli(RunJobCliParsingCommonTest.getMockClientContext());
    yamlConfig = YamlConfigTestUtils.createTempFileWithContents(
        DIR_NAME + "/envs-are-missing.yaml");
    runJobCli.run(
        new String[]{"-f", yamlConfig.getAbsolutePath(), "--verbose"});

    RunJobParameters jobRunParameters = runJobCli.getRunJobParameters();
    verifyBasicConfigValues(jobRunParameters, ImmutableList.of());
    verifyPsValues(jobRunParameters, "");
    verifySchedulerValues(jobRunParameters, "");
    verifyWorkerValues(jobRunParameters, "");
    verifySecurityValues(jobRunParameters);
  }

  @Test
  public void testInvalidConfigTensorboardSectionIsDefined() throws Exception {
    RunJobCli runJobCli = new RunJobCli(RunJobCliParsingCommonTest.getMockClientContext());
    exception.expect(YamlParseException.class);
    exception.expectMessage("TensorBoard section should not be defined " +
        "when TensorFlow is not the selected framework!");
    yamlConfig = YamlConfigTestUtils.createTempFileWithContents(
        DIR_NAME + "/invalid-config-tensorboard-section.yaml");
    runJobCli.run(
        new String[]{"-f", yamlConfig.getAbsolutePath(), "--verbose"});
  }
}