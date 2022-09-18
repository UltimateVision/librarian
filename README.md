# Librarian

A simple microservice with REST API that handles document download from external source 
and stores it in database.

### Running

This is a Spring Boot application, just use `mvn spring-boot:run`.

### Usage

#### 1. Scheduling download
Schedule document download by using `POST /api/documents` and providing document source like this:
```json
{
    "url" : "http://source.doc"
}
```
In response you'll receive ID of document along it's current status:
```json
{
    "id": "5200381b-8fb5-4304-a36e-199cb07d5d42",
    "status": "NEW"
}
```

#### 2. Checking status of the document
By calling `GET /api/documents/status/{id}`, you can verify if document is `READY` for download.
In response API will return status of document with specified id:
```json
{
    "documentStatus": "DOWNLOADING"
}
```

#### 3. Download the document
When document status changes to `READY`, call `GET /api/documents/{id}`. 
API will serve the document preserving original `Content-Type` header.

### API docs
API specification in OpenAPI v3 format is available under `/api` path.
HTML version is available under `/swagger-ui/index.html`.

### Assumptions and limitations
* Only HTTP/S URLs are supported with standard security policy regarding certificates.
* For sake of simplicity, in-memory H2 Database is used to store documents. There is no persistence active by default - this may be reconfigured by providing different `application.properties` configuration.
* No retry for failed documents.
* No document deletion.
* No document update.