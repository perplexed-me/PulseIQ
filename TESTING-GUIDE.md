# PulseIQ Testing Guide

## Testing Options

### 1. Local Docker Testing (Before Azure Deployment)

Test your Docker setup locally before deploying to Azure:

```bash
# Build and test locally using the full docker-compose
cd /Users/mohammad/Downloads/PulseIQ-user

# Start local environment
docker-compose -f docker-compose.full.yml up -d

# Check service status
docker-compose -f docker-compose.full.yml ps

# Test endpoints
curl http://localhost:8085/actuator/health  # Backend health
curl http://localhost:8080/                # Frontend
```

### 2. Azure Production Testing

Your Azure VM IP: **132.196.64.104**

#### Manual Deployment Test

```bash
# SSH to your Azure VM
ssh azureuser@132.196.64.104

# Clone your repository
git clone https://github.com/your-username/PulseIQ-user.git
cd PulseIQ-user

# Create environment file with your Azure IP
cat > .env.azure << EOF
DOCKER_USERNAME=your_dockerhub_username
DB_PASSWORD=your_secure_database_password
JWT_SECRET=your_jwt_secret_key
FRONTEND_URL=http://132.196.64.104:8080
BACKEND_URL=http://132.196.64.104:8085
FIREBASE_ENABLED=true
EOF

# Deploy using self-hosted PostgreSQL
docker-compose -f docker-compose.azure.yml --env-file .env.azure up -d

# Wait for services to start
sleep 60

# Check service status
docker-compose -f docker-compose.azure.yml ps
```

#### Health Check Commands

```bash
# On your Azure VM, run these tests:

# 1. Check Docker containers
docker ps

# 2. Check service health
docker-compose -f docker-compose.azure.yml ps

# 3. Test database connectivity
docker exec pulseiq_postgres pg_isready -U pulseiq_user -d pulseiq_db

# 4. Test backend health endpoint
curl http://localhost:8085/actuator/health

# 5. Test frontend
curl http://localhost:8080/

# 6. Check logs if issues
docker logs pulseiq_backend --tail=50
docker logs pulseiq_frontend --tail=50
docker logs pulseiq_postgres --tail=50
```

### 3. Remote Testing (From Your Local Machine)

Test your deployed application from your local machine:

```bash
# Test backend health
curl http://132.196.64.104:8085/actuator/health

# Test frontend
curl http://132.196.64.104:8080/

# Test if ports are accessible
nc -zv 132.196.64.104 8080  # Frontend
nc -zv 132.196.64.104 8085  # Backend

# If you have any API endpoints, test them
curl http://132.196.64.104:8085/api/health
```

### 4. Browser Testing

Open these URLs in your browser:

- **Frontend:** http://132.196.64.104:8080
- **Backend Health:** http://132.196.64.104:8085/actuator/health
- **Backend Info:** http://132.196.64.104:8085/actuator/info

### 5. GitHub Actions CI/CD Testing

To test the automated deployment:

1. **Set up GitHub Secrets:**
```
DOCKER_USERNAME=your_dockerhub_username
DOCKER_PASSWORD=your_dockerhub_password
AZURE_SSH_HOST=132.196.64.104
AZURE_SSH_USER=azureuser
AZURE_SSH_PRIVATE_KEY=your_private_ssh_key
DB_PASSWORD=your_secure_database_password
JWT_SECRET=your_jwt_secret_key
FIREBASE_SERVICE_ACCOUNT_JSON=your_firebase_json
SPRING_DATASOURCE_URL=your_test_database_url
SPRING_DATASOURCE_USERNAME=your_test_db_username
SPRING_DATASOURCE_PASSWORD=your_test_db_password
```

2. **Trigger deployment:**
```bash
# Push to main branch to trigger CI/CD
git add .
git commit -m "Deploy to Azure VM 132.196.64.104"
git push origin main
```

3. **Monitor deployment:**
- Go to GitHub Actions tab in your repository
- Watch the CI/CD pipeline execute
- Check logs for any errors

## Testing Database Persistence

### Test Data Persistence Across Restarts

```bash
# SSH to Azure VM
ssh azureuser@132.196.64.104
cd ~/pulseiq-app

# 1. Add some test data (through your app's UI or API)
# 2. Restart the application
docker-compose -f docker-compose.azure.yml restart

# 3. Check if data persists
docker exec pulseiq_postgres psql -U pulseiq_user -d pulseiq_db -c "
    SELECT schemaname, tablename, n_tup_ins, n_tup_upd 
    FROM pg_stat_user_tables 
    WHERE schemaname = 'pulseiq';
"

# 4. Complete container recreation test
docker-compose -f docker-compose.azure.yml down
docker-compose -f docker-compose.azure.yml up -d

# 5. Verify data is still there after complete restart
```

## Troubleshooting Common Issues

### 1. Port Access Issues

```bash
# Check if ports are open on Azure VM
sudo ufw status
sudo netstat -tlnp | grep :8080
sudo netstat -tlnp | grep :8085

# If ports are not accessible externally:
sudo ufw allow 8080
sudo ufw allow 8085
```

### 2. Docker Issues

```bash
# Check Docker status
sudo systemctl status docker

# Check Docker Compose version
docker-compose --version

# Free up space if needed
docker system prune -f
docker volume prune -f
```

### 3. Service Health Issues

```bash
# Check individual service logs
docker logs pulseiq_backend --tail=100 --follow
docker logs pulseiq_frontend --tail=100 --follow
docker logs pulseiq_postgres --tail=100 --follow

# Check resource usage
docker stats --no-stream
free -h
df -h
```

### 4. Database Connection Issues

```bash
# Test database from backend container
docker exec pulseiq_backend nc -zv db 5432

# Check database logs
docker logs pulseiq_postgres | grep ERROR

# Connect to database directly
docker exec -it pulseiq_postgres psql -U pulseiq_user -d pulseiq_db
```

## Performance Testing

### Load Testing Commands

```bash
# Install Apache Bench for load testing
sudo apt install apache2-utils

# Test frontend load
ab -n 100 -c 10 http://132.196.64.104:8080/

# Test backend health endpoint load
ab -n 100 -c 10 http://132.196.64.104:8085/actuator/health

# Monitor during load test
docker stats --no-stream
```

## Step-by-Step Testing Checklist

### ✅ Pre-deployment Testing
- [ ] Local Docker build successful
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Docker images pushed to Docker Hub

### ✅ Azure VM Setup
- [ ] SSH access to 132.196.64.104 working
- [ ] Docker and Docker Compose installed
- [ ] Ports 8080, 8085 accessible
- [ ] Environment file configured correctly

### ✅ Deployment Testing
- [ ] Containers start successfully
- [ ] All services show as "healthy"
- [ ] Database connection established
- [ ] Frontend accessible at http://132.196.64.104:8080
- [ ] Backend health check at http://132.196.64.104:8085/actuator/health

### ✅ Functionality Testing
- [ ] User registration/login works
- [ ] API endpoints respond correctly
- [ ] Database operations (CRUD) work
- [ ] File uploads work (if applicable)
- [ ] Real-time features work (if applicable)

### ✅ Persistence Testing
- [ ] Data survives container restart
- [ ] Data survives complete deployment
- [ ] Schema updates work correctly

## Quick Test Commands

Run these commands to quickly verify everything is working:

```bash
# One-liner health check
curl -s http://132.196.64.104:8085/actuator/health | jq '.' && curl -s -o /dev/null -w "%{http_code}" http://132.196.64.104:8080/ && echo " - Frontend OK"

# Check if all ports are accessible
for port in 8080 8085; do echo -n "Port $port: "; nc -zv 132.196.64.104 $port; done
```

This comprehensive testing approach ensures your PulseIQ application is properly deployed and functioning on your Azure VM!
