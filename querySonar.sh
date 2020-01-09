echo "current branch is : "$BUILDKITE_BRANCH
SonarStatus_Url="https://sonarcloud.io/api/qualitygates/project_status?projectKey=java-mdc&branch="$BUILDKITE_BRANCH
Status=`curl -s $SonarStatus_Url | jq '.projectStatus.status'`
echo "current branch sonarcloud status is : "$Status
if [ "$Status" = '"ERROR"' ]; then
    echo "Sonar Check Failed"
    echo "Please visit https://sonarcloud.io/dashboard?branch="$BUILDKITE_BRANCH"&id=java-mdc for more details"
    exit 1
else
    echo "Sonar Check Pass"
    exit 0
fi