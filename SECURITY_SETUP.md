# Security Setup Guide

## ⚠️ IMPORTANT: GitHub Push Protection

GitHub has blocked your push because it detected Google Cloud Service Account credentials in your committed files. This guide will help you fix this security issue properly.

## What We Fixed

1. **Removed all hardcoded secrets** from `.env` and `docker-compose.yml`
2. **Updated `.gitignore`** to prevent future secret commits
3. **Created secure templates** for environment configuration

## Required GitHub Secrets

You need to add the following secrets to your GitHub repository (Settings → Secrets and variables → Actions):

### Firebase Configuration
- `FIREBASE_SERVICE_ACCOUNT_JSON`: Your complete Firebase service account JSON (the one currently in your .env file)

### JWT Configuration  
- `JWT_SECRET`: A secure JWT secret (generate with: `openssl rand -base64 32`)

### Database Configuration
- `DB_PASSWORD`: Your production database password

### Azure Deployment
- `AZURE_SSH_HOST`: Your Azure VM IP address
- `AZURE_SSH_USER`: Your Azure VM username  
- `AZURE_SSH_PRIVATE_KEY`: Your SSH private key for Azure VM

### Docker Registry
- `DOCKER_USERNAME`: Your Docker Hub username
- `DOCKER_PASSWORD`: Your Docker Hub password

### Frontend Firebase Config
- `VITE_FIREBASE_API_KEY`: Your Firebase web app API key
- `VITE_FIREBASE_AUTH_DOMAIN`: Your Firebase auth domain
- `VITE_FIREBASE_PROJECT_ID`: Your Firebase project ID
- `VITE_FIREBASE_STORAGE_BUCKET`: Your Firebase storage bucket
- `VITE_FIREBASE_MESSAGING_SENDER_ID`: Your Firebase messaging sender ID
- `VITE_FIREBASE_APP_ID`: Your Firebase app ID
- `VITE_FIREBASE_MEASUREMENT_ID`: Your Firebase measurement ID (optional)

## How to Add GitHub Secrets

1. Go to your GitHub repository
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each secret listed above

## Next Steps

1. **Add all secrets to GitHub repository secrets**
2. **Remove the committed .env file from git history**:
   ```bash
   # Remove .env from git tracking
   git rm .env --cached
   
   # Commit the security fixes
   git add .gitignore docker-compose.yml .env.example
   git commit -m "security: Remove hardcoded secrets and add secure configuration templates"
   
   # If you need to remove .env from git history completely:
   git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch .env' --prune-empty --tag-name-filter cat -- --all
   ```

3. **Create your local .env file**:
   ```bash
   cp .env.example .env
   # Edit .env with your local development values
   ```

4. **Push your changes**:
   ```bash
   git push origin main
   ```

5. **Deploy via GitHub Actions** - Your CD pipeline will now use secure secrets!

## Local Development

For local development, create a `.env` file (not committed) with your development values:

```bash
cp .env.example .env
# Edit .env with your actual values for local development
```

## Verification

After adding secrets and pushing:
1. GitHub Actions should run successfully
2. Your application should deploy to Azure
3. Firebase authentication should work properly
4. No secrets should be visible in your repository

## Security Best Practices Applied

✅ No secrets in committed files  
✅ Secrets injected via CI/CD only  
✅ Proper .gitignore configuration  
✅ Template files for easy setup  
✅ Clear documentation for team members  

## Troubleshooting

If you still see push protection errors:
1. Make sure ALL secrets are removed from committed files
2. Check `git log --oneline` for any commits with secrets
3. Use `git filter-branch` to clean history if needed
4. Contact GitHub support if push protection persists after cleanup
