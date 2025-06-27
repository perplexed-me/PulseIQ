# PulseIQ Deployment Changes - Firebase Fix

## Issues Fixed

### 1. GitHub Token Dependency Removed

- **Problem**: CD workflow was downloading files using GitHub API with authentication tokens
- **Solution**: Files are now copied using `scp-action` during deployment, eliminating token dependency

### 2. Firebase Configuration Conflict Resolved

- **Problem**: Firebase service account was configured both via environment variable (`FIREBASE_JSON`) and mounted file, causing conflicts
- **Solution**: Removed `FIREBASE_JSON` environment variable, using only mounted file approach

### 3. Enhanced Error Detection and Debugging

- **Problem**: Limited visibility into deployment issues and Firebase configuration problems
- **Solution**: Added comprehensive logging, validation, and error reporting

## Changes Made

### 1. Updated `.github/workflows/cd.yml`

#### File Transfer Changes:

- Added `appleboy/scp-action` to copy deployment files directly
- Removed GitHub API calls for downloading `docker-compose.azure.yml` and `init-schema.sql`
- Files are now transferred from the build context

#### Firebase Configuration:

- Removed `FIREBASE_JSON` environment variable creation
- Added proper Firebase service account JSON file creation with validation
- Added JSON validation check using Python before deployment

#### Enhanced Monitoring:

- Improved service health monitoring with better error detection
- Added detailed logging for each deployment step
- Added Firebase configuration validation and debugging
- Enhanced connectivity testing with proper error reporting

### 2. Updated `docker-compose.azure.yml`

- Removed `FIREBASE_JSON=${FIREBASE_JSON}` environment variable
- Backend now uses only the mounted Firebase service account file at `/app/firebase-service-account.json`

## How Firebase Now Works

1. **GitHub Actions**: Creates `firebase-service-account.json` from the secret `FIREBASE_SERVICE_ACCOUNT_JSON`
2. **Docker Compose**: Mounts this file to `/app/firebase-service-account.json` in the backend container
3. **Backend Java Code**: Automatically detects and loads the mounted file (no environment variable needed)

## Expected Behavior

### During Deployment:

```
✅ Firebase service account JSON is valid
✅ Project ID: your-project-id
✅ Database schema file prepared
✅ Environment Configuration
```

### Backend Logs:

```
🔥 Firebase Configuration Debug
✅ Firebase initialized successfully
✅ Google authentication is available
```

### If Firebase Fails:

```
❌ Firebase service account JSON is invalid
❌ Firebase authentication service is not configured
```

## Troubleshooting

### If "Firebase authentication service is not available" Still Appears:

1. **Check Secret Configuration**:

   - Ensure `FIREBASE_SERVICE_ACCOUNT_JSON` GitHub secret contains valid JSON
   - Verify JSON format using an online JSON validator

2. **Check Deployment Logs**:

   - Look for "Firebase service account JSON is valid" message
   - Check if Project ID is extracted correctly

3. **Check Backend Logs**:

   ```bash
   docker-compose -f docker-compose.azure.yml logs backend | grep -i firebase
   ```

4. **Verify File Permissions**:
   ```bash
   docker exec pulseiq_backend ls -la /app/firebase-service-account.json
   ```

## Required GitHub Secrets

Ensure these secrets are configured in your GitHub repository:

- `DOCKER_USERNAME` - Docker Hub username
- `DOCKER_PASSWORD` - Docker Hub password/token
- `AZURE_SSH_HOST` - Azure VM IP address
- `AZURE_SSH_USER` - Azure VM username
- `AZURE_SSH_PRIVATE_KEY` - SSH private key for Azure VM
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `FIREBASE_SERVICE_ACCOUNT_JSON` - Complete Firebase service account JSON (not base64 encoded)
- `VITE_FIREBASE_*` - Frontend Firebase configuration variables

## Testing the Fix

After deployment, test Firebase functionality:

1. **Health Check**: `http://your-server:8085/actuator/health`
2. **Firebase Endpoint**: `http://your-server:8085/api/auth/google-patient` (should return 400/405, not 500)
3. **Frontend Login**: Try Google authentication in the frontend

The error "Firebase authentication service is not available" should no longer appear if the Firebase service account JSON is properly configured.
