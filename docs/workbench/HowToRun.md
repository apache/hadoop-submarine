<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
# How To Run Submarine Workbench

## Run Submarine on docker

By using the official image of submarine, only one docker command is required to run submarine workbench.

It should be noted that since the submarine workbench depends on the submarine database, so you need to run the docker container of the submarine database first.

```
docker run -it -p 3306:3306 -d --name submarine-data -e MYSQL_ROOT_PASSWORD=password apache/submarine:database-0.3.0
docker run -it -d --link=submarine-data:submarine-data --name submarine-server apache/submarine:submarine-0.3.0
```

## Run submarine workbench

```
cd submarine
./bin/submarine-daemon.sh [start|stop|restart]
```
To start workbench server, you need to download mysql jdbc jar and put it in the
path of workbench/lib for the first time. Or you can add parameter, getMysqlJar,
to get mysql jar automatically.
```
cd submarine
./bin/submarine-daemon.sh start getMysqlJar
```

## submarine-env.sh

`submarine-env.sh` is automatically executed each time the `submarine-daemon.sh` script is executed, so we can set the `submarine-daemon.sh` script and the environment variables in the `SubmarineServer` process via `submarine-env.sh`.

| Name                | Variable                                                     |
| ------------------- | ------------------------------------------------------------ |
| JAVA_HOME           | Set your java home path, default is `java`.                  |
| SUBMARINE_JAVA_OPTS | Set the JAVA OPTS parameter when the submarine workbench process starts. If you need to debug the submarine workbench process, you can set it to `-agentlib:jdwp=transport=dt_socket, server=y,suspend=n,address=5005` |
| SUBMARINE_MEM       | Set the java memory parameter when the submarine workbench process starts. |
| MYSQL_JAR_URL       | The customized URL to download mysql jdbc jar.               |
| MYSQL_VERSION       | The version of mysql jdbc jar to downloaded. The default value is 5.1.39. It's used to generate the default value of MYSQL_JDBC_URL |

## submarine-site.xml

`submarine-site.xml` is the configuration file for the entire `Submarine` system to run.

| Name                               | Variable                                                     |
| ---------------------------------- | ------------------------------------------------------------ |
| submarine.server.addr              | submarine server address, default is `0.0.0.0`               |
| submarine.server.port              | submarine server port, default `8080`                        |
| submarine.ssl                      | Should SSL be used by the submarine servers?, default `false` |
| submarine.server.ssl.port          | Server ssl port. (used when ssl property is set to true), default `8483` |
| submarine.ssl.client.auth          | Should client authentication be used for SSL connections?    |
| submarine.ssl.keystore.path        | Path to keystore relative to submarine configuration directory |
| submarine.ssl.keystore.type        | The format of the given keystore (e.g. JKS or PKCS12)        |
| submarine.ssl.keystore.password    | Keystore password. Can be obfuscated by the Jetty Password tool |
| submarine.ssl.key.manager.password | Key Manager password. Defaults to keystore password. Can be obfuscated. |
| submarine.ssl.truststore.path      | Path to truststore relative to submarine configuration directory. Defaults to the keystore path |
| submarine.ssl.truststore.type      | The format of the given truststore (e.g. JKS or PKCS12). Defaults to the same type as the keystore type |
| submarine.ssl.truststore.password  | Truststore password. Can be obfuscated by the Jetty Password tool. Defaults to the keystore password |
| workbench.web.war                  | Submarine workbench web war file path.                       |



## Compile

[Build From Code Guide](../development/BuildFromCode.md)

```$xslt
cd submarine/submarine-dist/target/submarine-dist-<version>/submarine-dist-<version>/
./bin/submarine-daemon.sh [start|stop|restart]
```
