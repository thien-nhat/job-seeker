pipeline {
    agent any
    tools {
        jdk 'JDK 17'
    }
    environment {
        MYSQL_ROOT_PASSWORD = '12345'
        MYSQL_DATABASE = 'jobseeker'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set up JDK 17') {
            steps {
                script {
                    // Set up JDK 17
                    tool name: 'JDK 17', type: 'JDK'
                }
            }
        }

        stage('Start MySQL') {
            steps {
                script {
                    // Start MySQL container
                    docker.image('mysql:9.1').withRun('-e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_DATABASE=jobseeker -p 3306:3306') { c ->
                        // MySQL is now running
                    }
                }
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    // Build with Maven
                    withMaven(maven: 'Maven 3') {
                        sh 'mvn -B package --file pom.xml'
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    // Run tests
                    withMaven(maven: 'Maven 3') {
                        sh 'mvn test'
                    }
                }
            }
        }
    }

    post {
        always {
            // Clean up actions
            echo 'Cleaning up...'
        }
    }
}
