pipeline {
    agent any

    environment {
        MYSQL_ROOT_PASSWORD = '12345'
        MYSQL_DATABASE = 'jobseeker'
    }

    services {
        mysql {
            image 'mysql:9.1'
            envVars {
                MYSQL_ROOT_PASSWORD = '12345'
                MYSQL_DATABASE = 'jobseeker'
            }
            options '--health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=5'
            ports '3306:3306'
        }
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
        }
    }
}
