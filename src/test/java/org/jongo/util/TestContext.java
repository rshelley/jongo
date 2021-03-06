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

package org.jongo.util;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import org.jongo.marshall.Marshaller;
import org.jongo.marshall.Unmarshaller;

import java.net.UnknownHostException;

public class TestContext {

    private final DB db;
    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;


    public TestContext(Marshaller marshaller, Unmarshaller unmarshaller) {
        this(createConnection(), marshaller, unmarshaller);
    }

    public TestContext(DB db, Marshaller marshaller, Unmarshaller unmarshaller) {
        this.db = db;
        this.unmarshaller = unmarshaller;
        this.marshaller = marshaller;
    }

    public Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    public DB getDB() {
        return db;
    }

    private static DB createConnection() {
        String hqUri = System.getProperty("jongo.mongohq.uri");
        try {
            return hqUri == null ? usingLocalMongo() : usingMongoHQ(hqUri);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to reach mongo database test instance", e);
        }
    }

    private static DB usingMongoHQ(String mongoHQUri) throws UnknownHostException {
        MongoURI mongoURI = new MongoURI(mongoHQUri);
        DB db = mongoURI.connectDB();
        db.authenticate(mongoURI.getUsername(), mongoURI.getPassword());
        return db;
    }

    private static DB usingLocalMongo() throws UnknownHostException {
        return new Mongo("127.0.0.1").getDB("jongo");
    }
}
