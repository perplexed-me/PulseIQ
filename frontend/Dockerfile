# ---------- Stage 1 : build the Vite / React app ----------
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci           # more reproducible than 'npm install'

COPY . .
RUN npm run build  


# ---------- Stage 2 : tiny Nginx image to serve /dist ----------
FROM nginx:alpine

# 🟢 1. Drop in our custom config FIRST(very important)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 🟢 2. THEN copy static assets on top
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g","daemon off;"]

