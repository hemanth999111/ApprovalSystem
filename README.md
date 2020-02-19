#Approval System Application

##Build Jar and install it:
```
mvn clean install
```
##deploy Jar:
```
java -jar target/ApproverSystem-1.0-SNAPSHOT.jar 
```

###REST APIs supported by Application:

####Create Employee

- Create employee with employee id: 1237 and employee name: hello
```$xslt
curl -X POST \
  http://localhost:8080/approval-system/v1/employees/ \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -H 'Postman-Token: ba7ca96a-8f8d-40e5-9697-ad82081e11f3' \
  -d '{
	"id": 1237,
	"name": "hello"
}'
```

####Create Approver

- Create Approver with approver id: 12345678 and approver name: approver
- Approver can approve documents of type 1 (TRAVEL) and 3 (MOBILE).

```$xslt
curl -X POST \
  http://localhost:8080/approval-system/v1/approvers/ \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -H 'Postman-Token: 4e04b290-0316-4bee-8d26-693b2f421529' \
  -d '{
	"id": 12345678,
	"name": "approver",
	"documentTypeList": [
			1, 3
		]
}'
```

####Upload Document
- Upload document of type 3 (MOBILE). Document is uploaded by employee:  1237.  Document id is generated and it will be available in response.
```$xslt
curl -X POST \
  http://localhost:8080/approval-system/v1/documents/ \
  -H 'Cache-Control: no-cache' \
  -H 'Postman-Token: 1e5cae1e-b97e-4d58-8209-e4fa9ae470a9' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -F file=@/home/hemanth/Downloads/EQiu91tUcAES1EC.jpeg \
  -F document-type=3 \
  -F employee-id=1237
```

###Update Document

- Update document with document id: 107996992. Document id is created when document is uploaded for first time.

```$xslt
curl -X PUT \
  http://localhost:8080/approval-system/v1/documents/ \
  -H 'Cache-Control: no-cache' \
  -H 'Postman-Token: 6d0b8b3f-91ac-4f32-a869-67dab0d218dc' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -F file=@/home/hemanth/Downloads/410639_2020_02_07.csv \
  -F document-id=107996992 \
  -F employee-id=1237
```

####Get all documents
-Get All documents available in Approval system.

```curl -X GET \
     http://localhost:8080/approval-system/v1/documents/ \
     -H 'Cache-Control: no-cache' \
     -H 'Postman-Token: a846164e-ee38-4a8a-b794-591667aa8118
```

####Get Single Document using document id
```$xslt
curl -X GET \
  http://localhost:8080/approval-system/v1/documents/107996992 \
  -H 'Cache-Control: no-cache' \
  -H 'Postman-Token: 10428a2f-b06f-497a-9005-de969470991c'
```

####Get All pending documents Given Approver Id
```$xslt
curl -X GET \
  http://localhost:8080/approval-system/v1/approvers/12345678/pending-documents \
  -H 'Cache-Control: no-cache' \
  -H 'Postman-Token: 6bb14776-c108-47e4-b8e4-6803c0997000'
```

####Approve/Reject Pending Document
- Approver: 12345678 is rejecting document with id: 107996992

```$xslt
curl -X PUT \
  http://localhost:8080/approval-system/v1/documents/107996992/approval-status \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -H 'Postman-Token: a280fe39-ebbf-4057-8d30-f9b5a9105c33' \
  -d '{
	"approvalStatus": "rejected",
	"approverId": 12345678
}'
```