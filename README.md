# DataCacheManager

This Cache implementation application has 5 endpoints 
a) To add an item to cache
b) To get an item from cache
c) To remove an item from cache and db
d) To remove all items from cache and db
e) To remove items only from cache

Springboot framework (v2.7.5) with Java > 8 (Java 17) has been used where controller sends request to the service which performs the business logic. Repository has been mocked up here as a service for now. spring-boot-starter-test has been used for junits (mockito etc)
slf4j has been used for logging and springdoc-openapi-ui has been used for swagger documentation

A) Swagger documentation will be generated below with the API definitions
http://localhost:8080/swagger-ui/index.html

B) Logs will be generated in logs folder

C) Few examples executed for the cache for below 5 API endpoints 

Add entry - /api/cache/add (post)
=========
eg. requests
{ "id": 1, "content": "First item" }
{ "id": 2, "content": "Second item" }
{ "id": 3, "content": "Third item" }
{ "id": 4, "content": "Fourth item" }

Get entry - /api/cache/get  (post)
===========================
eg. request 
{ "id": 1, "content": "First item" }

Remove entry (from db and cache)- /api/cache/remove (delete)
=========================================
eg. request 
{ "id": 1, "content": "First item" }

Remove all (from db and cache) - /api/cache/removeAll (delete) -> No request required
==============================================================

clear - /api/cache/clear (delete)  -> No request required
==================================
