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

package org.jongo;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.jongo.model.People;
import org.jongo.util.JongoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class RemoveTest extends JongoTestCase {


    private MongoCollection collection;

    @Before
    public void setUp() throws Exception {
        collection = createEmptyCollection("users");
    }

    @After
    public void tearDown() throws Exception {
        dropCollection("users");
    }

    @Test
    public void canRemoveASpecificDocument() throws Exception {
        /* given */
        collection.save(new People("John"));
        collection.save(new People("Peter"));

        /* when */
        WriteResult writeResult = collection.remove("{name:'John'}");

        /* then */
        Iterable<People> peoples = collection.find("{}").as(People.class);
        assertThat(peoples).hasSize(1);
        assertThat(writeResult).isNotNull();
    }

    @Test
    public void canRemoveByObjectId() throws Exception {
        /* given */
        String johnId = collection.save(new People("John"));

        /* when */
        WriteResult writeResult = collection.remove(new ObjectId(johnId));

        /* then */
        Iterable<People> peoples = collection.find("{}").as(People.class);
        assertThat(peoples).isEmpty();
        assertThat(writeResult).isNotNull();
    }

    @Test
    public void canRemoveWithParameters() throws Exception {
        /* given */
        String johnId = collection.save(new People("John"));

        /* when */
        WriteResult writeResult = collection.remove("{_id:#}", new ObjectId(johnId));

        /* then */
        Iterable<People> peoples = collection.find("{}").as(People.class);
        assertThat(peoples).isEmpty();
        assertThat(writeResult).isNotNull();
    }
}
