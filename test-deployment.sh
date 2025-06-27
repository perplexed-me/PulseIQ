#!/bin/bash

# PulseIQ Azure Deployment Test Script
# Tests your deployed application on Azure VM: 132.196.64.104

echo "🧪 Testing PulseIQ Deployment on Azure VM: 132.196.64.104"
echo "=========================================================="

AZURE_IP="132.196.64.104"
FRONTEND_URL="http://$AZURE_IP:8080"
BACKEND_URL="http://$AZURE_IP:8085"

# Test 1: Port Accessibility
echo ""
echo "🔌 Testing Port Accessibility..."
for port in 8080 8085; do
    echo -n "  Port $port: "
    if nc -zv $AZURE_IP $port 2>/dev/null; then
        echo "✅ Open"
    else
        echo "❌ Closed or unreachable"
    fi
done

# Test 2: Frontend Accessibility
echo ""
echo "🌐 Testing Frontend..."
echo -n "  Frontend ($FRONTEND_URL): "
if curl -s -o /dev/null -w "%{http_code}" $FRONTEND_URL | grep -q "200"; then
    echo "✅ Accessible"
else
    echo "❌ Not accessible"
fi

# Test 3: Backend Health Check
echo ""
echo "⚙️ Testing Backend Health..."
echo -n "  Backend Health ($BACKEND_URL/actuator/health): "
HEALTH_RESPONSE=$(curl -s $BACKEND_URL/actuator/health 2>/dev/null)
if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
    echo "✅ Healthy"
    echo "    Response: $HEALTH_RESPONSE"
else
    echo "❌ Unhealthy or unreachable"
    echo "    Response: $HEALTH_RESPONSE"
fi

# Test 4: Backend Info Endpoint
echo ""
echo "ℹ️ Testing Backend Info..."
echo -n "  Backend Info ($BACKEND_URL/actuator/info): "
INFO_RESPONSE=$(curl -s $BACKEND_URL/actuator/info 2>/dev/null)
if [ -n "$INFO_RESPONSE" ]; then
    echo "✅ Available"
    echo "    Response: $INFO_RESPONSE"
else
    echo "❌ Not available"
fi

# Test 5: Response Time Test
echo ""
echo "⏱️ Testing Response Times..."
echo -n "  Frontend response time: "
FRONTEND_TIME=$(curl -s -o /dev/null -w "%{time_total}" $FRONTEND_URL 2>/dev/null)
echo "${FRONTEND_TIME}s"

echo -n "  Backend response time: "
BACKEND_TIME=$(curl -s -o /dev/null -w "%{time_total}" $BACKEND_URL/actuator/health 2>/dev/null)
echo "${BACKEND_TIME}s"

# Test 6: SSH Connectivity (if you have SSH access)
echo ""
echo "🔐 Testing SSH Access..."
echo -n "  SSH to $AZURE_IP: "
if timeout 5 ssh -o ConnectTimeout=5 -o BatchMode=yes azureuser@$AZURE_IP exit 2>/dev/null; then
    echo "✅ SSH accessible"
else
    echo "❌ SSH not accessible (this is normal if you don't have SSH keys configured)"
fi

# Summary
echo ""
echo "📊 Test Summary"
echo "==============="
echo "Frontend URL: $FRONTEND_URL"
echo "Backend URL: $BACKEND_URL"
echo "Azure VM IP: $AZURE_IP"
echo ""
echo "🎯 Next Steps:"
echo "1. If tests pass: Your application is deployed successfully!"
echo "2. If tests fail: Check Azure VM firewall and service status"
echo "3. SSH to VM: ssh azureuser@$AZURE_IP"
echo "4. Check containers: docker ps"
echo "5. Check logs: docker logs pulseiq_backend"
echo ""
echo "📱 Manual Browser Test:"
echo "   Open $FRONTEND_URL in your browser"

# Additional diagnostic information
echo ""
echo "🔍 Diagnostic Commands (run these on your Azure VM if tests fail):"
echo "   sudo ufw status                          # Check firewall"
echo "   docker ps                                # Check running containers"
echo "   docker-compose ps                        # Check service status"
echo "   docker logs pulseiq_backend --tail=20    # Check backend logs"
echo "   docker logs pulseiq_frontend --tail=20   # Check frontend logs"
echo "   docker logs pulseiq_postgres --tail=20   # Check database logs"
