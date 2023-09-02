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
    Submarine API

    The Submarine REST API allows you to access Submarine resources such as,  experiments, environments and notebooks. The  API is hosted under the /v1 path on the Submarine server. For example,  to list experiments on a server hosted at http://localhost:8080, access http://localhost:8080/api/v1/experiment/  # noqa: E501

    The version of the OpenAPI document: 0.9.0-SNAPSHOT
    Contact: dev@submarine.apache.org
    Generated by: https://openapi-generator.tech
"""


import pprint
import re  # noqa: F401

import six

from submarine.client.configuration import Configuration


class NotebookPodSpec(object):
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
        'cpu': 'str',
        'env_vars': 'dict(str, str)',
        'gpu': 'str',
        'memory': 'str',
        'resources': 'str',
    }

    attribute_map = {
        'cpu': 'cpu',
        'env_vars': 'envVars',
        'gpu': 'gpu',
        'memory': 'memory',
        'resources': 'resources',
    }

    def __init__(
        self, cpu=None, env_vars=None, gpu=None, memory=None, resources=None, local_vars_configuration=None
    ):  # noqa: E501
        """NotebookPodSpec - a model defined in OpenAPI"""  # noqa: E501
        if local_vars_configuration is None:
            local_vars_configuration = Configuration()
        self.local_vars_configuration = local_vars_configuration

        self._cpu = None
        self._env_vars = None
        self._gpu = None
        self._memory = None
        self._resources = None
        self.discriminator = None

        if cpu is not None:
            self.cpu = cpu
        if env_vars is not None:
            self.env_vars = env_vars
        if gpu is not None:
            self.gpu = gpu
        if memory is not None:
            self.memory = memory
        if resources is not None:
            self.resources = resources

    @property
    def cpu(self):
        """Gets the cpu of this NotebookPodSpec.  # noqa: E501


        :return: The cpu of this NotebookPodSpec.  # noqa: E501
        :rtype: str
        """
        return self._cpu

    @cpu.setter
    def cpu(self, cpu):
        """Sets the cpu of this NotebookPodSpec.


        :param cpu: The cpu of this NotebookPodSpec.  # noqa: E501
        :type: str
        """

        self._cpu = cpu

    @property
    def env_vars(self):
        """Gets the env_vars of this NotebookPodSpec.  # noqa: E501


        :return: The env_vars of this NotebookPodSpec.  # noqa: E501
        :rtype: dict(str, str)
        """
        return self._env_vars

    @env_vars.setter
    def env_vars(self, env_vars):
        """Sets the env_vars of this NotebookPodSpec.


        :param env_vars: The env_vars of this NotebookPodSpec.  # noqa: E501
        :type: dict(str, str)
        """

        self._env_vars = env_vars

    @property
    def gpu(self):
        """Gets the gpu of this NotebookPodSpec.  # noqa: E501


        :return: The gpu of this NotebookPodSpec.  # noqa: E501
        :rtype: str
        """
        return self._gpu

    @gpu.setter
    def gpu(self, gpu):
        """Sets the gpu of this NotebookPodSpec.


        :param gpu: The gpu of this NotebookPodSpec.  # noqa: E501
        :type: str
        """

        self._gpu = gpu

    @property
    def memory(self):
        """Gets the memory of this NotebookPodSpec.  # noqa: E501


        :return: The memory of this NotebookPodSpec.  # noqa: E501
        :rtype: str
        """
        return self._memory

    @memory.setter
    def memory(self, memory):
        """Sets the memory of this NotebookPodSpec.


        :param memory: The memory of this NotebookPodSpec.  # noqa: E501
        :type: str
        """

        self._memory = memory

    @property
    def resources(self):
        """Gets the resources of this NotebookPodSpec.  # noqa: E501


        :return: The resources of this NotebookPodSpec.  # noqa: E501
        :rtype: str
        """
        return self._resources

    @resources.setter
    def resources(self, resources):
        """Sets the resources of this NotebookPodSpec.


        :param resources: The resources of this NotebookPodSpec.  # noqa: E501
        :type: str
        """

        self._resources = resources

    def to_dict(self):
        """Returns the model properties as a dict"""
        result = {}

        for attr, _ in six.iteritems(self.openapi_types):
            value = getattr(self, attr)
            if isinstance(value, list):
                result[attr] = list(map(lambda x: x.to_dict() if hasattr(x, "to_dict") else x, value))
            elif hasattr(value, "to_dict"):
                result[attr] = value.to_dict()
            elif isinstance(value, dict):
                result[attr] = dict(
                    map(
                        lambda item: (item[0], item[1].to_dict()) if hasattr(item[1], "to_dict") else item,
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
        if not isinstance(other, NotebookPodSpec):
            return False

        return self.to_dict() == other.to_dict()

    def __ne__(self, other):
        """Returns true if both objects are not equal"""
        if not isinstance(other, NotebookPodSpec):
            return True

        return self.to_dict() != other.to_dict()
