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
          // install deps
          powershell 'npm ci'

          // run production build with fallback
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

            # ---------- prepare frontend build context ----------
            Write-Host "Preparing frontend build context..."
            $destBrowser = Join-Path "Frontend" "browser"

            # remove any old copy
            if (Test-Path $destBrowser) { Remove-Item -Recurse -Force $destBrowser }

            $builtBrowser = Join-Path "Frontend" "dist/frontend/browser"
            if (-not (Test-Path -Path $builtBrowser)) {
              throw "Frontend build artifact not found at $builtBrowser - ensure Build Frontend stage completed successfully."
            }

            Copy-Item -Recurse -Force $builtBrowser $destBrowser

            # copy optional files if present (put them at top-level Frontend/ so Dockerfile.prod can COPY them)
            $maybe1 = Join-Path "Frontend" "dist/frontend/prerendered-routes.json"
            $maybe1b = Join-Path $destBrowser "prerendered-routes.json"
            if (Test-Path $maybe1) {
              Copy-Item -Force $maybe1 (Join-Path "Frontend" "prerendered-routes.json")
            } elseif (Test-Path $maybe1b) {
              Copy-Item -Force $maybe1b (Join-Path "Frontend" "prerendered-routes.json")
            }

            $maybe2 = Join-Path "Frontend" "dist/frontend/3rdpartylicenses.txt"
            $maybe2b = Join-Path $destBrowser "3rdpartylicenses.txt"
            if (Test-Path $maybe2) {
              Copy-Item -Force $maybe2 (Join-Path "Frontend" "3rdpartylicenses.txt")
            } elseif (Test-Path $maybe2b) {
              Copy-Item -Force $maybe2b (Join-Path "Frontend" "3rdpartylicenses.txt")
            }

            # ---------- docker login (try secure then fallback) ----------
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

            # ---------- image names ----------
            if (-not $env:BACKEND_IMAGE -or -not $env:IMAGE_TAG) {
              throw "Required environment variables missing: BACKEND_IMAGE or IMAGE_TAG"
            }

            $backend = "$($env:BACKEND_IMAGE):$($env:IMAGE_TAG)"
            $backendLatest = "$($env:BACKEND_IMAGE):latest"
            $frontend = "$($env:FRONTEND_IMAGE):$($env:IMAGE_TAG)"
            $frontendLatest = "$($env:FRONTEND_IMAGE):latest"

            # ---------- build & tag backend ----------
            Write-Host "Building backend image: $backend"
            docker build -t $backend -f Backend/Dockerfile Backend
            if ($LASTEXITCODE -ne 0) { throw "Backend docker build failed (exit $LASTEXITCODE)" }
            docker tag $backend $backendLatest

            # ---------- build & tag frontend (prod Dockerfile) ----------
            Write-Host "Building frontend image (prod) : $frontend"
            docker build -t $frontend -f Frontend/Dockerfile.prod Frontend
            if ($LASTEXITCODE -ne 0) { throw "Frontend docker build failed (exit $LASTEXITCODE)" }
            docker tag $frontend $frontendLatest

            # ---------- push ----------
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

            # cleanup optional workspace artifacts (safe)
            Remove-Item -Recurse -Force Frontend\\browser -ErrorAction SilentlyContinue
            Remove-Item -Force Frontend\\prerendered-routes.json -ErrorAction SilentlyContinue
            Remove-Item -Force Frontend\\3rdpartylicenses.txt -ErrorAction SilentlyContinue
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
