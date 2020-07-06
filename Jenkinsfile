#!/usr/bin/env groovy

node('nimble-jenkins-slave') {

    // -----------------------------------------------
    // --------------- Efactory Federated Search Branch ----------------
    // -----------------------------------------------
    if (env.BRANCH_NAME == 'efactory_federated_search') {

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
            sh 'mvn docker:build -Ddocker.image.tag=federated-search'
        }

        stage('Push Docker') {
            sh 'docker push nimbleplatform/indexing-service:federated-search'
        }

        stage('Deploy') {
            sh 'ssh staging "cd /srv/efactoryefs/docker_setup/prod-efac-efs && ./run-efac-portal.sh restart-single indexing-service"'
        }
    }

    // -----------------------------------------------
    // ---------------- Release Tags -----------------
    // -----------------------------------------------
    if( env.TAG_NAME ==~ /^\d+.\d+.\d+$/) {

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

        stage('Set version') {
            sh 'mvn org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=' + env.TAG_NAME
        }

//        stage('Run Tests') {
//            sh 'mvn clean test'
//        }

        stage('Build Java') {
            sh 'mvn clean install -DskipTests'
        }

        stage('Build Docker') {
            sh 'mvn docker:build'
        }

        stage('Push Docker') {
            sh 'docker push nimbleplatform/indexing-service:' + env.TAG_NAME
            sh 'docker push nimbleplatform/indexing-service:latest'
        }

        stage('Deploy MVP') {
            sh 'ssh nimble "cd /data/deployment_setup/prod/ && sudo ./run-prod.sh restart-single indexing-service"'
        }

        stage('Deploy FMP') {
            sh 'ssh fmp-prod "cd /srv/nimble-fmp/ && ./run-fmp-prod.sh restart-single indexing-service"'
        }

        stage('Deploy Efactory') {
            sh 'ssh efac-prod "cd /srv/nimble-efac/ && ./run-efac-prod.sh restart-single indexing-service"'
        }
    }
}
