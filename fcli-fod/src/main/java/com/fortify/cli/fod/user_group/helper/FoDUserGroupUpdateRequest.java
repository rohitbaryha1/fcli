/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.cli.fod.user_group.helper;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.core.annotation.ReflectiveAccess;
import lombok.Getter;
import lombok.ToString;

@ReflectiveAccess
@Getter
@ToString
public class FoDUserGroupUpdateRequest {
    private String name;
    private Boolean addAllUsers = false;
    private Boolean removeAllUsers = false;
    private JsonNode addUsers;
    private JsonNode removeUsers;
    private JsonNode addApplications;
    private JsonNode removeApplications;

    public FoDUserGroupUpdateRequest setName(String name) {
        this.name = name;
        return this;
    }

    public FoDUserGroupUpdateRequest setAddAllUsers(Boolean addAllUsers) {
        this.addAllUsers = addAllUsers;
        return this;
    }

    public FoDUserGroupUpdateRequest setRemoveAllUsers(Boolean removeAllUsers) {
        this.removeAllUsers = removeAllUsers;
        return this;
    }

    public FoDUserGroupUpdateRequest setAddUsers(JsonNode ids) {
        this.addUsers = ids;
        return this;
    }

    public FoDUserGroupUpdateRequest setRemoveUsers(JsonNode ids) {
        this.removeUsers = ids;
        return this;
    }

    public FoDUserGroupUpdateRequest setAddApplications(JsonNode ids) {
        this.addApplications = ids;
        return this;
    }

    public FoDUserGroupUpdateRequest setRemoveApplications(JsonNode ids) {
        this.removeApplications = ids;
        return this;
    }


}
