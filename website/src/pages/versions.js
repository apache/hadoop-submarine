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

 import React from 'react';
 import Link from '@docusaurus/Link';
 import Layout from '@theme/Layout';
 import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
 import {useVersions, useLatestVersion} from '@theme/hooks/useDocs';
 
function Version() {
  const versions = useVersions();
  const latestVersion = useLatestVersion();
	const {
    siteConfig
  } = useDocusaurusContext();
  const currentVersion = versions.find((version) => version.name === 'current');
  const pastVersions = versions.filter(
    (version) => version !== latestVersion && version.name !== 'current',
  );
  const repoUrl = `https://github.com/${siteConfig.organizationName}/${siteConfig.projectName}`;
	console.log(siteConfig);
  return (
    <Layout
      title="Versions"
      description="Submarine Versions page listing all documented site versions">
      <main className="container margin-vert--lg">
        <h1>Submarine documentation versions</h1>

        {latestVersion && (
          <div className="margin-bottom--lg">
            <h3 id="next">Current version (Stable)</h3>
            <p>
              Here you can find the documentation for current released version.
            </p>
            <table>
              <tbody>
                <tr>
                  <th>{latestVersion.label}</th>
                  <td>
                    <Link to={latestVersion.path}>Documentation</Link>
                  </td>
                  <td>
                    <a href={`${repoUrl}/releases/tag/v${latestVersion.name}`}>
                      Release Notes
                    </a>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        )}

        {currentVersion !== latestVersion && (
          <div className="margin-bottom--lg">
            <h3 id="latest">Next version (Unreleased)</h3>
            <p>
              Here you can find the documentation for work-in-process unreleased
              version.
            </p>
            <table>
              <tbody>
                <tr>
                  <th>{currentVersion.label}</th>
                  <td>
                    <Link to={currentVersion.path}>Documentation</Link>
                  </td>
									<td>
                    <Link to={repoUrl}>Source code</Link>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        )}

        {pastVersions.length > 0 && (
          <div className="margin-bottom--lg">
            <h3 id="archive">Past versions (Not maintained anymore)</h3>
            <p>
              Here you can find documentation for previous versions of
              Submarine.
            </p>
            <table>
              <tbody>
                {pastVersions.map((version) => (
                  <tr key={version.name}>
                    <th>{version.label}</th>
                    <td>
                      <Link to={version.path}>Documentation</Link>
                    </td>
                    <td>
                      <a href={`${repoUrl}/releases/tag/v${version.name}`}>
                        Release Notes
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </Layout>
  );
}

export default Version;
