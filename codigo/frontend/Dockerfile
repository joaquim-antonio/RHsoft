# ---------------------------------------------------------------------------
# RHSoft - Frontend (HTML/CSS/JS estatico servido por nginx)
#
# Nao ha build step: o frontend e JS puro e os arquivos sao servidos como
# estao. O codigo chama a API em http://localhost:8080 (ver assets/js/services/),
# e o compose expoe o backend nessa porta do host, entao nao ha proxy aqui.
# ---------------------------------------------------------------------------
FROM nginx:1.27-alpine

# Configuracao do servidor
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Site estatico. O contexto de build ja deve apontar para src/.
COPY src/ /usr/share/nginx/html/

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
    CMD wget -q --spider http://127.0.0.1/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
