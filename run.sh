#!/bin/bash
# ══════════════════════════════════════════════════════
#  Smart Public Complaint Management System
#  Quick Start Script
# ══════════════════════════════════════════════════════

echo ""
echo "╔══════════════════════════════════════════════════╗"
echo "║  SMART PUBLIC COMPLAINT MANAGEMENT SYSTEM        ║"
echo "║  Starting backend...                             ║"
echo "╚══════════════════════════════════════════════════╝"
echo ""

cd "$(dirname "$0")/backend"

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java 17+"
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
echo "✓ Java version: $(java -version 2>&1 | head -1)"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven 3.8+"
    exit 1
fi
echo "✓ Maven found"

echo ""
echo "📦 Building project..."
mvn clean package -q -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Check errors above."
    exit 1
fi

echo "✅ Build successful!"
echo ""
echo "🚀 Starting server on http://localhost:8080"
echo "   Open frontend/index.html in your browser"
echo ""
echo "   Default logins:"
echo "   Admin:   admin / admin123"
echo "   Citizen: citizen1 / citizen123"
echo "   Officer: officer1 / officer123"
echo ""
echo "Press Ctrl+C to stop"
echo "────────────────────────────────────────────────────"

mvn spring-boot:run
