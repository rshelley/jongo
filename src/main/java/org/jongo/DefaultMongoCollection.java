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

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.jongo.marshall.Marshaller;
import org.jongo.marshall.Unmarshaller;

import static org.jongo.Jongo.toDBObject;

class DefaultMongoCollection implements MongoCollection {

    public static final String MONGO_ID = "_id";
    private final DBCollection collection;
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;
    private final QueryFactory queryFactory;

    DefaultMongoCollection(DBCollection dbCollection, Marshaller marshaller, Unmarshaller unmarshaller) {
        this.collection = dbCollection;
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
        this.queryFactory = new QueryFactory();
    }

    public FindOne findOne(String query) {
        return new FindOne(collection, queryFactory.createQuery(query), unmarshaller);
    }

    public FindOne findOne(ObjectId id) {
        if (id == null) {
            throw new IllegalArgumentException("Object id must not be null");
        }
        return new FindOne(collection, queryFactory.createQuery("{_id:#}", id), unmarshaller);
    }

    public FindOne findOne(String query, Object... parameters) {
        return new FindOne(collection, queryFactory.createQuery(query, parameters), unmarshaller);
    }

    public Find find(String query) {
        return new Find(collection, queryFactory.createQuery(query), unmarshaller);
    }

    public Find find(String query, Object... parameters) {
        return new Find(collection, queryFactory.createQuery(query, parameters), unmarshaller);
    }

    public long count() {
        return collection.count();
    }

    public long count(String query) {
        return collection.count(queryFactory.createQuery(query).toDBObject());
    }

    public long count(String query, Object... parameters) {
        return collection.count(queryFactory.createQuery(query, parameters).toDBObject());
    }

    public WriteResult update(String query, String modifier) {
        return update(query, modifier, collection.getWriteConcern());
    }

    public WriteResult update(String query, String modifier, WriteConcern concern) {
        return update(query, modifier, false, true, concern);
    }

    public WriteResult upsert(String query, String modifier) {
        return upsert(query, modifier, collection.getWriteConcern());
    }

    public WriteResult upsert(String query, String modifier, WriteConcern concern) {
        return update(query, modifier, true, false, concern);
    }

    private WriteResult update(String query, String modifier, boolean upsert, boolean multi, WriteConcern concern) {
        return collection.update(toDBObject(query), toDBObject(modifier), upsert, multi, concern);
    }

    public <D> String save(D document) {
        return save(document, collection.getWriteConcern());
    }

    public <D> String save(D document, WriteConcern concern) {
        String entityAsJson = marshaller.marshall(document);
        DBObject dbObject = toDBObject(entityAsJson);
        collection.save(dbObject, concern);
        return dbObject.get(MONGO_ID).toString();
    }

    public WriteResult insert(String query) {
        return collection.save(queryFactory.createQuery(query).toDBObject());
    }

    public WriteResult insert(String query, Object... parameters) {
        return collection.save(queryFactory.createQuery(query, parameters).toDBObject());
    }

    public WriteResult remove(String query) {
        return collection.remove(toDBObject(query));
    }

    public WriteResult remove(String query, Object... parameters) {
        return collection.remove(queryFactory.createQuery(query, parameters).toDBObject());
    }

    public WriteResult remove(ObjectId id) {
        return remove("{_id:#}", id);
    }

    @SuppressWarnings("unchecked")
    public <T> Iterable<T> distinct(String key, String query, final Class<T> clazz) {
        Distinct distinct = new Distinct(collection, unmarshaller, key, queryFactory.createQuery(query));
        return distinct.as(clazz);
    }

    public void drop() {
        collection.drop();
    }

    public void ensureIndex(String index) {
        collection.ensureIndex(toDBObject(index));
    }

    public String getName() {
        return collection.getName();
    }

    public DBCollection getDBCollection() {
        return collection;
    }

}
