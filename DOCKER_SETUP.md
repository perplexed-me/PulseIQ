# Docker Compose Files Guide

This project includes several Docker Compose files for different deployment scenarios. Here's when to use each:

## Files Overview

### 🏠 Local Development

- **`docker-compose.dev.yml`** - For active development with hot reload
- **`docker-compose.full.yml`** - For testing full production build locally
- **`docker-compose.yml`** - Basic local setup (deprecated, use `.dev.yml` instead)

### ☁️ Azure Deployment

- **`docker-compose.azure.yml`** - Complete Azure deployment with PostgreSQL container
- **`docker-compose.azure-managed-db.yml`** - Azure deployment using Azure Database for PostgreSQL (RECOMMENDED)

## Which File Should You Use?

### For Local Development

```bash
# Development with hot reload
docker-compose -f docker-compose.dev.yml up

# Test production build locally
docker-compose -f docker-compose.full.yml up
```

### For Azure Production

```bash
# If using Azure Database for PostgreSQL (RECOMMENDED)
docker-compose -f docker-compose.azure-managed-db.yml --env-file .env.azure-managed-db up -d

# If running PostgreSQL in container (not recommended for production)
docker-compose -f docker-compose.azure.yml --env-file .env.azure up -d
```

## 🎯 RECOMMENDED Setup

**For Production on Azure: Use `docker-compose.azure-managed-db.yml`**

### Benefits:

- ✅ Uses managed Azure Database for PostgreSQL
- ✅ Better performance and reliability
- ✅ Automatic backups and updates
- ✅ Better security and monitoring
- ✅ Reduces container resource usage

### Setup Steps:

1. Create Azure Database for PostgreSQL
2. Configure GitHub secrets (see `SECURITY_SETUP.md`)
3. Use `.github/workflows/cd-managed-db.yml` for deployment
4. Deploy with: `docker-compose.azure-managed-db.yml`

## Environment Files

Each Docker Compose file requires corresponding environment files:

- `.env.dev` - Local development
- `.env.azure` - Azure with container database
- `.env.azure-managed-db` - Azure with managed database (RECOMMENDED)

## Cleanup Unused Files

You can safely remove these files if using the recommended setup:

- `docker-compose.yml` (use `docker-compose.dev.yml` instead)
- `.env` (use specific environment files)

## Firebase Configuration

All production deployments require proper Firebase configuration:

- Backend: `FIREBASE_JSON` environment variable
- Frontend: `VITE_FIREBASE_*` environment variables
- Service account file: `firebase-service-account.json`

See `SECURITY_SETUP.md` for complete Firebase setup instructions.
