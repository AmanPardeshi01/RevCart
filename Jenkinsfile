pipeline {
  agent any

  environment {
    DOCKERHUB_CRED = 'dockerhub-creds'
    DOCKERHUB_USER = 'amanpardeshi01'
    BACKEND_IMAGE   = "${DOCKERHUB_USER}/revcart-backend"
    FRONTEND_IMAGE  = "${DOCKERHUB_USER}/revcart-frontend"
    IMAGE_TAG       = "${env.BUILD_NUMBER ?: 'local'}"
    MVN_OPTS        = "-B -DskipTests"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build Backend (Maven)') {
      steps {
        dir('Backend') {
          powershell '''
            Write-Host "Running Maven package..."
            mvn ${env.MVN_OPTS} clean package
            if ($LASTEXITCODE -ne 0) { throw "Maven build failed (exit $LASTEXITCODE)" }
          '''
        }
      }
    }

    stage('Build Frontend (npm)') {
      steps {
        dir('Frontend') {
          powershell 'npm ci'

          powershell '''
            Write-Host "Running production build..."
            npm run build -- --configuration=production
            if ($LASTEXITCODE -ne 0) {
              Write-Host "Production build failed with exit code $LASTEXITCODE, trying default build..."
              npm run build
              if ($LASTEXITCODE -ne 0) {
                throw "Both production and default frontend builds failed (exit $LASTEXITCODE). See log above."
              } else {
                Write-Host "Default build succeeded."
              }
            } else {
              Write-Host "Production build succeeded."
            }
          '''
        }
      }
    }

    stage('Docker: build & push') {
  steps {
    withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CRED}", usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
      powershell '''
        $ErrorActionPreference = 'Stop'

        if (-not $env:DOCKER_CONFIG) {
          $env:DOCKER_CONFIG = Join-Path $env:USERPROFILE ".docker"
        }
        Write-Host "DOCKER_CONFIG = $env:DOCKER_CONFIG"

        # Secure login (password-stdin)
        $loginSucceeded = $false
        try {
          Write-Host "Trying docker login (password-stdin)..."
          $env:DH_PASS | docker login -u $env:DH_USER --password-stdin
          if ($LASTEXITCODE -eq 0) {
            Write-Host "docker login (stdin) succeeded."
            $loginSucceeded = $true
          } else {
            Write-Host "docker login (stdin) exit code: $LASTEXITCODE"
          }
        } catch {
          Write-Host "docker login (stdin) failed: $($_.Exception.Message)"
        }

        if (-not $loginSucceeded) {
          Write-Host "Attempting fallback docker login (insecure, debug only)..."
          docker login -u $env:DH_USER -p $env:DH_PASS
          if ($LASTEXITCODE -ne 0) { throw "Fallback docker login failed (exit $LASTEXITCODE)" }
          Write-Host "docker login (fallback) succeeded."
        }

        # Use PowerShell env vars for image names/tags
        if (-not $env:BACKEND_IMAGE -or -not $env:IMAGE_TAG) {
          throw "Required environment variables missing: BACKEND_IMAGE or IMAGE_TAG"
        }

        $backend = "$($env:BACKEND_IMAGE):$($env:IMAGE_TAG)"
        $backendLatest = "$($env:BACKEND_IMAGE):latest"
        $frontend = "$($env:FRONTEND_IMAGE):$($env:IMAGE_TAG)"
        $frontendLatest = "$($env:FRONTEND_IMAGE):latest"

        Write-Host "Building backend image: $backend"
        docker build -t $backend -f Backend/Dockerfile Backend
        if ($LASTEXITCODE -ne 0) { throw "Backend docker build failed (exit $LASTEXITCODE)" }
        docker tag $backend $backendLatest

        Write-Host "Building frontend image: $frontend"
        docker build -t $frontend -f Frontend/Dockerfile Frontend
        if ($LASTEXITCODE -ne 0) { throw "Frontend docker build failed (exit $LASTEXITCODE)" }
        docker tag $frontend $frontendLatest

        Write-Host "Pushing images..."
        docker push $backend
        if ($LASTEXITCODE -ne 0) { throw "Push failed: $backend" }
        docker push $backendLatest
        if ($LASTEXITCODE -ne 0) { throw "Push failed: $backendLatest" }
        docker push $frontend
        if ($LASTEXITCODE -ne 0) { throw "Push failed: $frontend" }
        docker push $frontendLatest
        if ($LASTEXITCODE -ne 0) { throw "Push failed: $frontendLatest" }

        Write-Host "Docker logout"
        docker logout
      '''
    }
  }
}

  }

  post {
    success { echo "Build and push succeeded: ${env.BUILD_NUMBER}" }
    failure { echo "Build failed. Check console output." }
  }
}
