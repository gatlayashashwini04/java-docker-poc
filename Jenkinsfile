
pipeline {
    agent any

    environment {
        // GitHub Repository
        GITHUB_URL = 'https://github.com/gatlayashashwini04/java-docker-poc.git'

        // Docker Hub Configuration
        DOCKER_IMAGE = 'YOUR_DOCKERHUB_USERNAME/java-docker-poc'
        DOCKER_CREDENTIALS = 'dockerhub-credentials'

        // Application Configuration
        CONTAINER_NAME = 'java-docker-app'
        APP_PORT = '8080'
    }

    stages {

        // 1. Git Checkout
        stage('1. Git Checkout') {
            steps {
                git branch: 'main',
                    url: "${GITHUB_URL}"
            }
        }

        // 2. Maven Build using Docker
        stage('2. Maven Build') {
            steps {
                sh '''
                    docker run --rm \
                        -v "$WORKSPACE":/app \
                        -w /app \
                        maven:3.9.9-eclipse-temurin-17 \
                        mvn clean package -DskipTests
                '''
            }
        }

        // 3. Docker Build
        stage('3. Docker Build') {
            steps {
                sh '''
                    docker build -t ${DOCKER_IMAGE}:latest .
                '''
            }
        }

        // 4. Publish to Docker Hub
        stage('4. Publish to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: "${DOCKER_CREDENTIALS}",
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login \
                            -u "$DOCKER_USER" --password-stdin

                        docker push ${DOCKER_IMAGE}:latest

                        docker logout
                    '''
                }
            }
        }

        // 5. Launch Container
        stage('5. Launch Container') {
            steps {
                sh '''
                    docker rm -f java-docker-app || true

                    docker run -d \
                        --name java-docker-app \
                        -p 8080:8080 \
                        ${DOCKER_IMAGE}:latest
                '''
            }
        }

        // 6. Test Website using CURL
        stage('6. Test Website using CURL') {
            steps {
                sh '''
                    echo "Testing Java application..."

                    for i in $(seq 1 30); do
                        if curl -fsS http://localhost:8080/; then
                            echo "Application is running successfully!"
                            exit 0
                        fi

                        sleep 5
                    done

                    echo "Application failed to respond"

                    docker logs java-docker-app

                    exit 1
                '''
            }
        }

        // 7. Kill Container
        stage('7. Kill Container') {
            steps {
                sh '''
                    docker stop java-docker-app || true
                    docker rm java-docker-app || true
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Jenkins pipeline completed successfully!'
        }

        failure {
            echo 'FAILURE: Please check Console Output.'
        }

        always {
            echo 'Jenkins pipeline execution finished.'
        }
    }
}

