package com.airhacks.enhydrator.transform;

import com.airhacks.enhydrator.flexpipe.RowTransformation;
import com.airhacks.enhydrator.in.Column;
import com.airhacks.enhydrator.in.Row;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * #%L
 * enhydrator
 * %%
 * Copyright (C) 2014 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 *
 * @author airhacks.com
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "datatype-name-mapper")
public class DatatypeNameMapper extends RowTransformation {

    private Map<String, Datatype> mappings;

    public DatatypeNameMapper() {
        this.mappings = new HashMap<>();
    }

    @Override
    public Row execute(Row input) {
        if (input == null) {
            return null;
        }
        this.mappings.entrySet().forEach(e -> changeDataType(input.getColumnByName(e.getKey()), e.getValue()));
        return input;
    }

    void changeDataType(Column column, Datatype mapping) {
        if (column == null) {
            return;
        }
        switch (mapping) {
            case DOUBLE:
                column.convertToDouble();
                break;
            case INTEGER:
                column.convertToInteger();
                break;
            case BOOLEAN:
                column.convertToBoolean();
                break;
        }
    }

    public DatatypeNameMapper addMapping(String name, Datatype mapping) {
        this.mappings.put(name, mapping);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.mappings);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DatatypeNameMapper other = (DatatypeNameMapper) obj;
        if (!Objects.equals(this.mappings, other.mappings)) {
            return false;
        }
        return true;
    }

}
