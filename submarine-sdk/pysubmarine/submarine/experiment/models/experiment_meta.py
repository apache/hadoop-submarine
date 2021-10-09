# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# coding: utf-8

"""
    Submarine Experiment API

    The Submarine REST API allows you to create, list, and get experiments. The API is hosted under the /v1/experiment route on the Submarine server. For example, to list experiments on a server hosted at http://localhost:8080, access http://localhost:8080/api/v1/experiment/  # noqa: E501

    The version of the OpenAPI document: 0.7.0-SNAPSHOT
    Contact: dev@submarine.apache.org
    Generated by: https://openapi-generator.tech
"""


import pprint
import re  # noqa: F401

import six

from submarine.experiment.configuration import Configuration


class ExperimentMeta(object):
    """NOTE: This class is auto generated by OpenAPI Generator.
    Ref: https://openapi-generator.tech

    Do not edit the class manually.
    """

    """
    Attributes:
      openapi_types (dict): The key is attribute name
                            and the value is attribute type.
      attribute_map (dict): The key is attribute name
                            and the value is json key in definition.
    """
    openapi_types = {
        "name": "str",
        "namespace": "str",
        "framework": "str",
        "cmd": "str",
        "env_vars": "dict(str, str)",
    }

    attribute_map = {
        "name": "name",
        "namespace": "namespace",
        "framework": "framework",
        "cmd": "cmd",
        "env_vars": "envVars",
    }

    def __init__(
        self,
        name: str = None,
        namespace: str = None,
        framework: str = None,
        cmd: str = None,
        env_vars: dict = None,
        local_vars_configuration: Configuration = None,
    ):  # noqa: E501
        """ExperimentMeta - a model defined in OpenAPI"""  # noqa: E501
        if local_vars_configuration is None:
            local_vars_configuration = Configuration()
        self.local_vars_configuration = local_vars_configuration

        self._name = None
        self._namespace = None
        self._framework = None
        self._cmd = None
        self._env_vars = None
        self.discriminator = None

        if name is not None:
            self.name = name
        if namespace is not None:
            self.namespace = namespace
        if framework is not None:
            self.framework = framework
        if cmd is not None:
            self.cmd = cmd
        if env_vars is not None:
            self.env_vars = env_vars

    @property
    def name(self):
        """Gets the name of this ExperimentMeta.  # noqa: E501


        :return: The name of this ExperimentMeta.  # noqa: E501
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name: str):
        """Sets the name of this ExperimentMeta.


        :param name: The name of this ExperimentMeta.  # noqa: E501
        :type: str
        """

        self._name = name

    @property
    def namespace(self):
        """Gets the namespace of this ExperimentMeta.  # noqa: E501


        :return: The namespace of this ExperimentMeta.  # noqa: E501
        :rtype: str
        """
        return self._namespace

    @namespace.setter
    def namespace(self, namespace: str):
        """Sets the namespace of this ExperimentMeta.


        :param namespace: The namespace of this ExperimentMeta.  # noqa: E501
        :type: str
        """

        self._namespace = namespace

    @property
    def framework(self):
        """Gets the framework of this ExperimentMeta.  # noqa: E501


        :return: The framework of this ExperimentMeta.  # noqa: E501
        :rtype: str
        """
        return self._framework

    @framework.setter
    def framework(self, framework: str):
        """Sets the framework of this ExperimentMeta.


        :param framework: The framework of this ExperimentMeta.  # noqa: E501
        :type: str
        """

        self._framework = framework

    @property
    def cmd(self):
        """Gets the cmd of this ExperimentMeta.  # noqa: E501


        :return: The cmd of this ExperimentMeta.  # noqa: E501
        :rtype: str
        """
        return self._cmd

    @cmd.setter
    def cmd(self, cmd: str):
        """Sets the cmd of this ExperimentMeta.


        :param cmd: The cmd of this ExperimentMeta.  # noqa: E501
        :type: str
        """

        self._cmd = cmd

    @property
    def env_vars(self):
        """Gets the env_vars of this ExperimentMeta.  # noqa: E501


        :return: The env_vars of this ExperimentMeta.  # noqa: E501
        :rtype: dict(str, str)
        """
        return self._env_vars

    @env_vars.setter
    def env_vars(self, env_vars: dict):
        """Sets the env_vars of this ExperimentMeta.


        :param env_vars: The env_vars of this ExperimentMeta.  # noqa: E501
        :type: dict(str, str)
        """

        self._env_vars = env_vars

    def to_dict(self):
        """Returns the model properties as a dict"""
        result = {}

        for attr, _ in six.iteritems(self.openapi_types):
            value = getattr(self, attr)
            if isinstance(value, list):
                result[attr] = list(
                    map(lambda x: x.to_dict() if hasattr(x, "to_dict") else x, value)
                )
            elif hasattr(value, "to_dict"):
                result[attr] = value.to_dict()
            elif isinstance(value, dict):
                result[attr] = dict(
                    map(
                        lambda item: (item[0], item[1].to_dict())
                        if hasattr(item[1], "to_dict")
                        else item,
                        value.items(),
                    )
                )
            else:
                result[attr] = value

        return result

    def to_str(self):
        """Returns the string representation of the model"""
        return pprint.pformat(self.to_dict())

    def __repr__(self):
        """For `print` and `pprint`"""
        return self.to_str()

    def __eq__(self, other):
        """Returns true if both objects are equal"""
        if not isinstance(other, ExperimentMeta):
            return False

        return self.to_dict() == other.to_dict()

    def __ne__(self, other):
        """Returns true if both objects are not equal"""
        if not isinstance(other, ExperimentMeta):
            return True

        return self.to_dict() != other.to_dict()
