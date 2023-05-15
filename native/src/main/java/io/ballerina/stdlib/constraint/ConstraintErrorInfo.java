/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.stdlib.constraint;

import java.util.List;
import java.util.stream.Collectors;

import static io.ballerina.stdlib.constraint.Constants.SYMBOL_SEPARATOR;
import static io.ballerina.stdlib.constraint.ErrorUtils.buildDefaultErrorMessage;

/**
 * Represents a constraint violation error information.
 */
public class ConstraintErrorInfo {
    final String path;
    final String message;
    final List<String> failedConstraints;
    final boolean onMemberType;

    public ConstraintErrorInfo(String path, String message, List<String> failedConstraints) {
        this.path = path;
        this.message = message;
        this.failedConstraints = failedConstraints;
        this.onMemberType = false;
    }

    public ConstraintErrorInfo(String path, String message, List<String> failedConstraints, boolean onMemberType) {
        this.path = path;
        this.message = message;
        this.failedConstraints = failedConstraints;
        this.onMemberType = onMemberType;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasMessage() {
        return !onMemberType && message != null;
    }

    public String getErrorMessage() {
        return message != null ? message : buildDefaultErrorMessage(failedConstraints);
    }

    public List<String> getFailedConstraintsWithPath() {
        return failedConstraints.stream().map(
                failedConstraint -> path + SYMBOL_SEPARATOR + failedConstraint).collect(Collectors.toList());
    }
}
