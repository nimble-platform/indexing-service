#!/usr/bin/env groovy

node('nimble-jenkins-slave') {

    // -----------------------------------------------
    // --------------- Staging Branch ----------------
    // -----------------------------------------------
    if (env.BRANCH_NAME == 'staging') {

        stage('Clone and Update') {
            git(url: 'https://github.com/nimble-platform/indexing-service.git', branch: env.BRANCH_NAME)
        }
        stage('Build Dependencies') {
            sh 'rm -rf common'
            sh 'git clone https://github.com/nimble-platform/common'
            dir('common') {
                sh 'git checkout ' + env.BRANCH_NAME
                sh 'mvn clean install'
            }
        }
//        stage('Run Tests') {
//            sh 'mvn clean test'
//        }

        stage('Build Java') {
            sh 'mvn clean install -DskipTests'
        }

        stage('Build Docker') {
            sh 'mvn docker:build -Ddocker.image.tag=staging'
        }

        stage('Push Docker') {
            sh 'docker push nimbleplatform/indexing-service:staging'
        }

        stage('Deploy') {
            sh 'ssh staging "cd /srv/nimble-staging/ && ./run-staging.sh restart-single indexing-service"'
        }
    }

    // -----------------------------------------------
    // ---------------- Master Branch ----------------
    // -----------------------------------------------
    if (env.BRANCH_NAME == 'master') {

        stage('Clone and Update') {
            git(url: 'https://github.com/nimble-platform/indexing-service.git', branch: env.BRANCH_NAME)
        }
        stage('Build Dependencies') {
            sh 'rm -rf common'
            sh 'git clone https://github.com/nimble-platform/common'
            dir('common') {
                sh 'git checkout ' + env.BRANCH_NAME
                sh 'mvn clean install'
            }
        }

//        stage('Run Tests') {
//            sh 'mvn clean test'
//        }

        stage('Build Java') {
            sh 'mvn clean install -DskipTests'
        }

        stage('Build Docker') {
            sh 'mvn docker:build -Ddocker.image.tag=latest'
        }

        stage('Push Docker') {
            sh 'docker push nimbleplatform/indexing-service:latest'
        }

        stage('Deploy MVP') {
            sh 'ssh nimble "cd /data/deployment_setup/prod/ && sudo ./run-prod.sh restart-single indexing-service"'
        }

        stage('Deploy FMP') {
            sh 'ssh fmp-prod "cd /srv/nimble-fmp/ && ./run-fmp-prod.sh restart-single indexing-service"'
        }
    }

    // -----------------------------------------------
    // ---------------- Release Tags -----------------
    // -----------------------------------------------
    if( env.TAG_NAME ==~ /^\d+.\d+.\d+$/) {

//        stage('Clone and Update') {
//            git(url: 'https://github.com/nimble-platform/indexing-service.git', branch: 'master')
//        }
//        stage('Build Dependencies') {
//            sh 'rm -rf common'
//            sh 'git clone https://github.com/nimble-platform/common'
//            dir('common') {
//                sh 'git checkout ' + env.BRANCH_NAME
//                sh 'mvn clean install'
//            }
//        }
//
//        stage('Set version') {
//            sh 'mvn org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=' + env.TAG_NAME
//            sh 'mvn -f identity-service/pom.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=' + env.TAG_NAME
//        }
//
//        stage('Run Tests') {
//            sh 'mvn clean test'
//        }
//
//        stage('Build Java') {
//            sh 'mvn clean install -DskipTests'
//        }
//
//        stage('Build Docker') {
//            sh 'mvn -f identity-service/pom.xml docker:build'
//        }
//
//        stage('Push Docker') {
//            sh 'docker push nimbleplatform/identity-service:' + env.TAG_NAME
//            sh 'docker push nimbleplatform/identity-service:latest'
//        }
//
//        stage('Deploy MVP') {
//            sh 'ssh nimble "cd /data/deployment_setup/prod/ && sudo ./run-prod.sh restart-single identity-service"'
//        }
//
//        stage('Deploy FMP') {
//            sh 'ssh fmp-prod "cd /srv/nimble-fmp/ && ./run-fmp-prod.sh restart-single identity-service"'
//        }
    }
}
