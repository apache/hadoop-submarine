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


class EnvironmentSpec(object):
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
        'description': 'str',
        'docker_image': 'str',
        'image': 'str',
        'kernel_spec': 'KernelSpec',
        'name': 'str',
    }

    attribute_map = {
        'description': 'description',
        'docker_image': 'dockerImage',
        'image': 'image',
        'kernel_spec': 'kernelSpec',
        'name': 'name',
    }

    def __init__(
        self,
        description=None,
        docker_image=None,
        image=None,
        kernel_spec=None,
        name=None,
        local_vars_configuration=None,
    ):  # noqa: E501
        """EnvironmentSpec - a model defined in OpenAPI"""  # noqa: E501
        if local_vars_configuration is None:
            local_vars_configuration = Configuration()
        self.local_vars_configuration = local_vars_configuration

        self._description = None
        self._docker_image = None
        self._image = None
        self._kernel_spec = None
        self._name = None
        self.discriminator = None

        if description is not None:
            self.description = description
        if docker_image is not None:
            self.docker_image = docker_image
        if image is not None:
            self.image = image
        if kernel_spec is not None:
            self.kernel_spec = kernel_spec
        if name is not None:
            self.name = name

    @property
    def description(self):
        """Gets the description of this EnvironmentSpec.  # noqa: E501


        :return: The description of this EnvironmentSpec.  # noqa: E501
        :rtype: str
        """
        return self._description

    @description.setter
    def description(self, description):
        """Sets the description of this EnvironmentSpec.


        :param description: The description of this EnvironmentSpec.  # noqa: E501
        :type: str
        """

        self._description = description

    @property
    def docker_image(self):
        """Gets the docker_image of this EnvironmentSpec.  # noqa: E501


        :return: The docker_image of this EnvironmentSpec.  # noqa: E501
        :rtype: str
        """
        return self._docker_image

    @docker_image.setter
    def docker_image(self, docker_image):
        """Sets the docker_image of this EnvironmentSpec.


        :param docker_image: The docker_image of this EnvironmentSpec.  # noqa: E501
        :type: str
        """

        self._docker_image = docker_image

    @property
    def image(self):
        """Gets the image of this EnvironmentSpec.  # noqa: E501


        :return: The image of this EnvironmentSpec.  # noqa: E501
        :rtype: str
        """
        return self._image

    @image.setter
    def image(self, image):
        """Sets the image of this EnvironmentSpec.


        :param image: The image of this EnvironmentSpec.  # noqa: E501
        :type: str
        """

        self._image = image

    @property
    def kernel_spec(self):
        """Gets the kernel_spec of this EnvironmentSpec.  # noqa: E501


        :return: The kernel_spec of this EnvironmentSpec.  # noqa: E501
        :rtype: KernelSpec
        """
        return self._kernel_spec

    @kernel_spec.setter
    def kernel_spec(self, kernel_spec):
        """Sets the kernel_spec of this EnvironmentSpec.


        :param kernel_spec: The kernel_spec of this EnvironmentSpec.  # noqa: E501
        :type: KernelSpec
        """

        self._kernel_spec = kernel_spec

    @property
    def name(self):
        """Gets the name of this EnvironmentSpec.  # noqa: E501


        :return: The name of this EnvironmentSpec.  # noqa: E501
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name):
        """Sets the name of this EnvironmentSpec.


        :param name: The name of this EnvironmentSpec.  # noqa: E501
        :type: str
        """

        self._name = name

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
        if not isinstance(other, EnvironmentSpec):
            return False

        return self.to_dict() == other.to_dict()

    def __ne__(self, other):
        """Returns true if both objects are not equal"""
        if not isinstance(other, EnvironmentSpec):
            return True

        return self.to_dict() != other.to_dict()
