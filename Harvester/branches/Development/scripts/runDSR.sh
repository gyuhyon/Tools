#!/bin/bash

# Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the new BSD license
# which accompanies this distribution, and is available at
# http://www.opensource.org/licenses/bsd-license.html
# 
# Contributors:
#     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial API and implementation
#	  James Pence

# Set working directory
cd /usr/share/vivoingest

# Execute Fetch
#rm -rd XMLVault/h2dsr/xml
java -Xms1024m -Xmx3072M -cp bin/ingest-0.6.1.jar:bin/dependency/* org.vivoweb.ingest.fetch.JDBCFetch -X config/tasks/DSR-JDBCFetch.xml
#tar -czpf backups/h2dsr-xml.tar.gz XMLVault/h2dsr/xml
#tar -xzpf backups/h2dsr-xml.tar.gz XMLVault/h2dsr/xml

# Execute Translate
#rm -rd XMLVault/h2dsr/rdf
java -Xms1024m -Xmx3072M -cp bin/ingest-0.6.1.jar:bin/dependency/* org.vivoweb.ingest.translate.XSLTranslator -i config/recordHandlers/DSR-XML.xml -o config/recordHandlers/DSR-RDF.xml -x config/datamaps/DSRtoVIVO.xsl
#tar -czpf backups/h2dsr-rdf.tar.gz XMLVault/h2dsr/rdf
#tar -xzpf backups/h2dsr-rdf.tar.gz XMLVault/h2dsr/rdf

# Execute Transfer to import from record handler into local temp model
#rm -rd XMLVault/h2dsr/All
#java -Xms1024m -Xmx3072M -cp bin/ingest-0.6.1.jar:bin/dependency/* org.vivoweb.ingest.transfer.Transfer -o config/jenaModels/h2.xml -O modelName=dsrTempTransfer -O dbUrl="jdbc:h2:XMLVault/h2dsr/All/store;MODE=HSQLDB" -h config/recordHandlers/DSR-RDF.xml -n http://vivotest.ctrip.ufl.edu/vivo/individual/
#tar -czpf backups/h2dsr-All.tar.gz XMLVault/h2dsr/All
#tar -xzpf backups/h2dsr-All.tar.gz XMLVault/h2dsr/All

# Execute Score to match jobs with organizations
#rm -rd XMLVault/h2dsr/Scored
#java -Xms1024m -Xmx3072M -cp bin/ingest-0.6.1.jar:bin/dependency/* org.vivoweb.ingest.score.Score -v config/jenaModels/myVIVO.xml -i config/jenaModels/h2.xml -I dbUrl="jdbc:h2:XMLVault/h2dsr/All/store;MODE=HSQLDB" -I modelName=dsrTempTransfer -o config/jenaModels/h2.xml -O dbUrl="jdbc:h2:XMLVault/h2dsr/Scored/store;MODE=HSQLDB" -O modelName=dsrStaging -f "http://vivoweb.org/ontology/score#ufid=http://vivo.ufl.edu/ontology/vivo-ufl/ufid" -x "http://vivoweb.org/ontology/core#principalInvestigatorRoleOf" -y "http://vivoweb.org/ontology/core#relatedRole"

#date=`date +%Y-%m-%d_%k%M.%S`
#mysqldump -u root -p PASSWORD vitrodb > backups/vitrodb.dsrrun.$date.sql
#rm -rf backups/vitrodb.current.sql
#ln -s vitrodb.$date.sql backups/vitrodb.current.sql

# Execute Transfer from local temp model into main vivo model
#java -Xms1024m -Xmx3072M -cp bin/ingest-0.6.1.jar:bin/dependency/* org.vivoweb.ingest.transfer.Transfer -i config/jenaModels/h2.xml -I modelName=dsrStaging -I dbUrl="jdbc:h2:XMLVault/h2dsr/Scored/store;MODE=HSQLDB" -o config/jenaModels/myVIVO.xml

# Execute Transfer to copy into dsr model
#java -Xms1024m -Xmx3072M -cp bin/ingest-0.6.1.jar:bin/dependency/* org.vivoweb.ingest.transfer.Transfer -i config/jenaModels/h2.xml -I modelName=dsrStaging -I dbUrl="jdbc:h2:XMLVault/h2dsr/Scored/store;MODE=HSQLDB" -o config/jenaModels/myVIVO.xml -O modelName=dsr

#Restart Tomcat
#Tomcat must be restarted in order for the harvested data to appear in VIVO
#/etc/init.d/tomcat restart
#sleep 20s
#/etc/init.d/apache2 restart