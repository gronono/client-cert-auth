# L'authorité de certification
## Création de la clé privée et de la demande de certification:
*$ openssl req -new -newkey rsa -nodes -out ca.csr -keyout ca.key*
```
Generating a 2048 bit RSA private key
....................................................................................................................................................................................+++
...............................................+++
writing new private key to 'ca.key'
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:NC
State or Province Name (full name) [Some-State]:Nouvelle-Caledonie
Locality Name (eg, city) []:Noumea
Organization Name (eg, company) [Internet Widgits Pty Ltd]:
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:CA pour les tests
Email Address []:

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:
```

## Création du certificat autosigné de la CA
*$ openssl x509 -trustout -signkey ca.key -days 3650 -req -in ca.csr -out ca.pem*
```
Signature ok
subject=/C=NC/ST=Nouvelle-Caledonie/L=Noumea/O=Internet Widgits Pty Ltd/CN=CA pour les tests
Getting Private key
```

## Conversion en .crt
*$ openssl x509 -outform der -in ca.pem -out ca.crt*

## Création du fichier contenant le futur numéro de série du prochain certificat signé par notre CA
*$ echo 02 > ca.srl*

## Importation dans le navigateur
Comme le certificat est autosigné, il faut l'importer dans le navigateur.
Dans firefox, Préférences > Avancé > Onglet Certificats > Afficher les certificats > Onglet Autorités > Importer ... > Cocher "Confirmer cette AC pour identifier des sites web."

## Création d'un keystore contenant notre CA.
*$ keytool -import -keystore trust.jks -trustcacerts -file ca.crt -storepass secret-trust*
```
Propriétaire : CN=CA pour les tests, O=Internet Widgits Pty Ltd, L=Noumea, ST=Nouvelle-Caledonie, C=NC
Emetteur : CN=CA pour les tests, O=Internet Widgits Pty Ltd, L=Noumea, ST=Nouvelle-Caledonie, C=NC
Numéro de série : de3e03b80320ac4a
Valide du : Tue Jan 27 21:03:51 NCT 2015 au : Thu Feb 26 21:03:51 NCT 2015
Empreintes du certificat :
	 MD5:  AA:6C:40:2E:75:59:53:0E:9A:E8:11:C7:67:B6:3B:8C
	 SHA1 : DA:8B:F7:13:B5:51:CD:C7:98:38:9D:9A:24:46:EF:FE:BA:B7:3C:0D
	 SHA256 : AB:3C:5E:24:EC:E9:EC:48:A5:32:2C:F6:2C:26:DD:C8:17:A5:3E:95:68:34:9F:95:D2:68:45:07:33:87:49:E8
	 Nom de l'algorithme de signature : SHA256withRSA
	 Version : 1
Faire confiance à ce certificat ? [non] :  oui
Certificat ajouté au fichier de clés
```

## Copie du truststore
Copier le fichier trust.jks dans le dossier src/main/resources de l'application

# Serveur
## Création de la clé privée 
(on passe par keytool et non openssl pour avoir directement la clé privée dans le keystore)
*$ keytool -genkey -alias server -keystore server.jks -storepass secret-server*
```
Quels sont vos nom et prénom ?
  [Unknown]:  localhost
Quel est le nom de votre unité organisationnelle ?
  [Unknown]:  
Quel est le nom de votre entreprise ?
  [Unknown]:  
Quel est le nom de votre ville de résidence ?
  [Unknown]:  
Quel est le nom de votre état ou province ?
  [Unknown]:  
Quel est le code pays à deux lettres pour cette unité ?
  [Unknown]:  
Est-ce CN=localhost, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown ?
  [non]:  oui

Entrez le mot de passe de la clé pour <server>
	(appuyez sur Entrée s'il s'agit du mot de passe du fichier de clés) :  
```

## Demande de certification
*$ keytool -certreq -alias server -file server.csr -keystore server.jks -storepass secret-server*

## Certification avec notre CA
*$ openssl x509 -CA ca.pem -CAkey ca.key -CAserial ca.srl -req -in server.csr -out server.pem*
```
Signature ok
subject=/C=Unknown/ST=Unknown/L=Unknown/O=Unknown/OU=Unknown/CN=localhost
Getting CA Private Key
```

## Importation de la CA dans le keystore serveur
*$ keytool -import -alias ca -keystore server.jks -trustcacerts -file ca.pem -storepass secret-server*
```
Propriétaire : CN=CA pour les tests, O=Internet Widgits Pty Ltd, L=Noumea, ST=Nouvelle-Caledonie, C=NC
Emetteur : CN=CA pour les tests, O=Internet Widgits Pty Ltd, L=Noumea, ST=Nouvelle-Caledonie, C=NC
Numéro de série : d9d800b3f10824a6
Valide du : Tue Jan 27 21:56:27 NCT 2015 au : Fri Jan 24 21:56:27 NCT 2025
Empreintes du certificat :
	 MD5:  C3:F2:07:2E:D1:BE:24:36:61:8F:5D:3A:B4:9B:1C:86
	 SHA1 : FD:AE:26:4D:35:A0:A5:54:7A:E1:65:EE:78:AB:20:8D:B1:A5:E6:69
	 SHA256 : 81:F1:07:4B:8A:4F:D7:DF:40:C6:A9:2E:FC:82:4A:22:27:1F:BC:4F:35:07:CA:0A:1E:18:8B:08:16:51:A2:AA
	 Nom de l'algorithme de signature : SHA256withRSA
	 Version : 1
Faire confiance à ce certificat ? [non] :  oui
Certificat ajouté au fichier de clés
```

## Importation du certificat serveur dans le keystore serveur
*$ keytool -import -alias server -keystore server.jks -trustcacerts -file server.pem -storepass secret-server*
```
Réponse de certificat installée dans le fichier de clés
```

## Copie du keystore
Copier le fichier server.jks dans le répertoire src/main/resources

## Activation le SSL dans Spring Boot
On peut maintenant activer le ssl dans le fichier src/main/resources/application.properties
```
server.port = 8443
server.ssl.key-store = classpath:server.jks
server.ssl.key-store-password = secret-server
server.ssl.trust-store = classpath:trust.jks
server.ssl.trust-store-password = secret-trust
```

L'application doit fonctionner en HTTPS sans avertissement de sécurité (puisqu'on a ajouté notre CA dans le navigateur)

# Client
## Création d'une clé privée (pas besoin d'un keystore)
*$ openssl req -new -newkey rsa -nodes -out client.csr -keyout client.key*
```
Generating a 2048 bit RSA private key
........................................................................................................+++
..............+++
writing new private key to 'client.key'
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:NC
State or Province Name (full name) [Some-State]:Nouvelle-Calédonie
Locality Name (eg, city) []:Nouméa
Organization Name (eg, company) [Internet Widgits Pty Ltd]:
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:Gerard Bouchard
Email Address []:

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:
```

## Signature
*$ openssl x509 -CA ca.pem -CAkey ca.key -CAserial ca.srl -req -in client.csr -out client.pem*
```
Signature ok
subject=/C=NC/ST=Nouvelle-Caledonie/L=Noumea/O=Internet Widgits Pty Ltd/CN=Gerard Bouchard
Getting CA Private Key
```

## Export au format .p12
*$ openssl pkcs12 -export -clcerts -in client.pem -inkey client.key -out client.p12 -name client* 
```
Enter Export Password:secret-client
Verifying - Enter Export Password:secret-client
```

## Importation dans le navigateur
Il faut ajouter notre certificat client au navigateur.
Dans Firefox, Préférences > Avancé > Onglet Certificats > Afficher les certificats > Onglet "Vos certificats" > Importer ...

## Activation de l'authentification client dans Spring Boot
Puis dans la conf de Spring (application.properties), rajouter qu'on veut de l'authentification client
```
server.ssl.client-auth = want
```

Et voila.
