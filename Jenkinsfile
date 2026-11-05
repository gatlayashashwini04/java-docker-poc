
pipeline {
    agent any

    environment {
        IMAGE_NAME = 'yashashwini28/java-docker-poc'
        IMAGE_TAG = "${BUILD_NUMBER}"

        CONTAINER_NAME = "java-docker-poc-${BUILD_NUMBER}"

        HOST_PORT = '18080'

        HEALTH_URL = 'http://127.0.0.1:18080/health'
    }

    stages {

        stage('1. Git Checkout') {
            steps {
                echo 'Checking out source code'

                git branch: 'main',
                    url: 'https://github.com/YOUR_GITHUB_USERNAME/java-docker-poc.git'
            }
        }

        stage('2. Maven Build') {
            steps {
                echo 'Building Java application'

                sh 'java -version'
                sh 'mvn -version'
                sh 'mvn clean package'

                sh 'ls -lh target/*.jar'
            }
        }

        stage('3. Docker Build') {
            steps {
                echo 'Building Docker image'

                sh '''
                    docker build --platform linux/amd64 \
                    -t ${IMAGE_NAME}:${IMAGE_TAG} \
                    -t ${IMAGE_NAME}:latest .
                '''
            }
        }

        stage('4. Publish Docker Image') {
            steps {
                echo 'Pushing image to Docker Hub'

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DH_USER',
                        passwordVariable: 'DH_TOKEN'
                    )
                ]) {
                    sh '''
                        echo "$DH_TOKEN" | docker login \
                        -u "$DH_USER" --password-stdin

                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        docker push ${IMAGE_NAME}:latest

                        docker logout
                    '''
                }
            }
        }

        stage('5. Launch Container') {
            steps {
                echo 'Starting application container'

                sh '''
                    docker rm -f ${CONTAINER_NAME} 2>/dev/null || true

                    docker run -d \
                    --name ${CONTAINER_NAME} \
                    -p ${HOST_PORT}:8080 \
                    ${IMAGE_NAME}:${IMAGE_TAG}

                    docker ps
                '''
            }
        }

        stage('6. Test Site using CURL') {
            steps {
                echo 'Testing application health endpoint'

                sh '''
                    curl --fail \
                    --silent \
                    --show-error \
                    --retry 10 \
                    --retry-connrefused \
                    --retry-delay 3 \
                    ${HEALTH_URL}
                '''
            }
        }

        stage('7. Kill Container') {
            steps {
                echo 'Stopping and removing application container'

                sh '''
                    docker stop ${CONTAINER_NAME}
                    docker rm ${CONTAINER_NAME}
                '''
            }
        }
    }

    post {
        always {
            echo 'Cleaning up application container'

            sh '''
                docker rm -f ${CONTAINER_NAME} 2>/dev/null || true
            '''
        }

        success {
            echo 'Java Docker Jenkins POC completed successfully'
        }

        failure {
            echo 'Pipeline failed. Please check Console Output.'
        }
    }
}

