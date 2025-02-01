connect to FTP server via WING FTP
set the localhost FTP server or any other port foor external access
TLS version config shoul be set both on FTP server and application
add Docker-compose for run FTP server:
      - TLS_CN=localhost
      - TLS_ORG=MyOrganization
      - TLS_C=US 
for local certification. 
