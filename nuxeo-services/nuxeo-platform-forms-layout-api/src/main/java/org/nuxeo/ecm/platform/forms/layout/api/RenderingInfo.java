/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.platform.forms.layout.api;

import java.io.Serializable;

/**
 * @since 5.5
 */
public interface RenderingInfo extends Serializable {

    public static enum LEVEL {
        error, warn, info
    }

    String getLevel();

    String getMessage();

    boolean isTranslated();

    /**
     * Returns a clone instance of this widget definition.
     * <p>
     * Useful for conversion of widget definition during export.
     */
    RenderingInfo clone();

}
