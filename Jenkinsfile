pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        timestamps()
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    tools {
        maven 'Maven-3.9'
    }

    environment {
        IMAGE_NAME = 'my-java-app'
        CONTAINER_NAME = 'my-java-app'
        APP_PORT = '8082'

        DEPLOY_USER = 'ec2-user'
        DEPLOY_HOST = '172.31.6.65'
        SSH_CREDENTIALS_ID = 'ec2-2-ssh'
    }

    stages {

        stage('1 - Checkout') {
            steps {
                checkout scm
            }
        }

        stage('2 - Maven Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('3 - Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('4 - Maven Package') {
            steps {
                sh 'mvn package'
            }
        }


        stage('6 - Build Docker Image') {
            steps {
                sh 'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .'
            }
        }


        stage('8 - Save Docker Image') {
            steps {
                sh '''
                    rm -f ${IMAGE_NAME}.tar

                    docker save \
                      -o ${IMAGE_NAME}.tar \
                      ${IMAGE_NAME}:${BUILD_NUMBER}
                '''
            }
        }

        stage('9 - Deploy to EC2 #2') {
            steps {
                sshagent(credentials: ["${SSH_CREDENTIALS_ID}"]) {
                    sh '''
                        scp -o StrictHostKeyChecking=no \
                          ${IMAGE_NAME}.tar \
                          ${DEPLOY_USER}@${DEPLOY_HOST}:/home/ec2-user/${IMAGE_NAME}.tar

                        ssh -o StrictHostKeyChecking=no \
                          ${DEPLOY_USER}@${DEPLOY_HOST} "
                            set -e

                            docker load -i /home/ec2-user/${IMAGE_NAME}.tar

                            docker stop ${CONTAINER_NAME} || true

                            docker rm ${CONTAINER_NAME} || true

                            docker run -d \
                              --restart unless-stopped \
                              --name ${CONTAINER_NAME} \
                              -p ${APP_PORT}:${APP_PORT} \
                              ${IMAGE_NAME}:${BUILD_NUMBER}
                        "
                    '''
                }
            }
        }

        stage('10 - Smoke Test') {
            steps {
                sh '''
                    for i in 1 2 3 4 5 6; do

                      if curl --fail --silent \
                        http://${DEPLOY_HOST}:${APP_PORT}/health; then
                        exit 0
                      fi

                      sleep 5
                    done

                    exit 1
                '''
            }
        }
    }

    post {

        always {
            sh 'rm -f ${IMAGE_NAME}.tar || true'
        }

        success {
            echo 'Pipeline completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Open Console Output and find the first failed stage.'
        }
    }
}
