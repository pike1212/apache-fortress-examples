FROM apachedirectory/openldap-for-apache-fortress-tests

ADD BaseData.ldif /tmp
RUN slapadd -f /etc/ldap/slapd.conf -l /tmp/BaseData.ldif -n2