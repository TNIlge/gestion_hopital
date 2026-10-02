# ==============================================================================
# Étape 1 : Build de l'application Angular
# ==============================================================================
FROM node:20-alpine AS build

WORKDIR /app

# Copie des fichiers de dépendances pour bénéficier du cache Docker
COPY package*.json ./

RUN npm install

# Copie du code source complet
COPY . .

# Compilation en mode production
RUN npm run build

# ==============================================================================
# Étape 2 : Image d'exécution légère avec Nginx
# ==============================================================================
FROM nginx:alpine

# Copie de la configuration Nginx optimisée pour SPA Angular
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copie des fichiers compilés depuis l'étape de build
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
