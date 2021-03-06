/*
 * Copyright (C) 2011 Benoit GUEROUT <bguerout at gmail dot com> and Yves AMSELLEM <amsellem dot yves at gmail dot com>
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
 */

package org.jongo.spike.dbref.jackson;

import com.mongodb.DB;
import com.mongodb.DBRef;
import com.mongodb.util.JSON;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class ReferenceDeserializer extends JsonDeserializer<Reference> {

    private ObjectMapper mapper;
    private DB db;

    public ReferenceDeserializer(ObjectMapper mapper, DB db) {
        this.mapper = mapper;
        this.db = db;
    }

    @Override
    public Reference deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String json = parser.readValueAsTree().toString();
        return new Reference(convertToDBRef(json), mapper);
    }

    private DBRef convertToDBRef(String json) {
        DBRef dbRef = (DBRef) JSON.parse(json);
        return new DBRef(db, dbRef.getRef(), dbRef.getId());
    }

}
