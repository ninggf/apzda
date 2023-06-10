FROM nginx:1.23.1-alpine

COPY dist /var/www/html/
COPY nginx.conf /etc/nginx/

RUN cd /var/www/html; \
    find ./ -type f -name "*.js" | xargs gzip -7 -k;\
    find ./ -type f -name "*.css" | xargs gzip -7 -k;\
    find ./ -type f -name "*.svg" | xargs gzip -7 -k;\
    find ./ -type f -name "*.html" | xargs gzip -7 -k;\
    for f in $(find ./ -type f -name "*.js.gz"); do\
        mv $f "${f%.*.*}.gz.js";\
    done ;\
    for f in $(find ./ -type f -name "*.css.gz"); do\
        mv $f "${f%.*.*}.gz.css";\
    done ;\
    for f in $(find ./ -type f -name "*.svg.gz"); do\
        mv $f "${f%.*.*}.gz.svg";\
    done;\
    for f in $(find ./ -type f -name "*.html.gz"); do\
        mv $f "${f%.*.*}.gz.html";\
    done

EXPOSE 80

STOPSIGNAL SIGQUIT
