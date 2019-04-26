FROM mysql:8.0.15
COPY docker-entrypoint-initdb.d/ /docker-entrypoint-initdb.d/
HEALTHCHECK --interval=10s --timeout=3s CMD mysqladmin -uroot -p${MYSQL_ROOT_PASSWORD} ping -h 127.0.0.1 || exit 1