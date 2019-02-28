# indexing-service
Spring-Boot Micro-Service providing search capabilites with Apache SOLR

## requirements

A running Apache SOLR service is required. If necessary download from the [Apache Solr Website](https://lucene.apache.org/solr/). Extracting the binary zip file should be sufficient. For starting Apache SOLR run

```
<solr.home>/bin/solr start -cloud
```
Once Apache Solr is running point the browser to the [Solr Admin Page](http://localhost:8983/solr/) to verify the available collections.

in order to stop the SOLR service, run 

```
<solr.home>/bin/solr stop -all
```

### Service build and startup

 ```
 mvn clean spring-boot:run
 ```

 During startup, the service tries to create new collections for
 
 - OWL Classes (class)
 - OWL Propertis (props)
 - Manufactuer Party (manufacturer)
 - Catalogue Items (item)
 
 More detailed information on the purpose & functionality of the service can be found [here](https://secure.salzburgresearch.at/wiki/pages/viewpage.action?pageId=33062939) (Nimble-Login required)
 
 The Service may be verified online [Nimble-Staging](http://nimble-staging.salzburgresearch.at/index/actuator/info)
 
