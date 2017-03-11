Execution of Azure Machine Learning job via Batch Execution Service (BES)
====================================================
This is an example Java application for executing a Job inside Azure Machine Learning via Batch Execution Service (BES).  As opposed to the synchronous Request-Response Service (RRS), BES is used for running a job with duration longer than 90 seconds.  BES requires input and output data to be stored in a cloud storage.  Client application can poll for job status through the job API and download the output data file once the job is completed.  At the moment, only RRS accepts input data in the HTTP request (in JSON format) while BES do not, hopefully this option will be available in the future.

You can combine different machine learning modules to create an experiment and the same infrastructure is required for publishing the experiment as a Web Service.  The job used here is a R Script experiment that uses Azure Blob storage as the data files store.

Requirements
------------------
- Maven 3.0 or higher to build
- Java 7 or higher
- Azure Subscription

Background
-----------------
Azure Machine Learning
https://azure.microsoft.com/en-us/services/machine-learning/

How to consume an Azure Machine Learning Web service that has been deployed from a Machine Learning experiment
https://docs.microsoft.com/en-us/azure/machine-learning/machine-learning-consume-web-services


Steps
--------
Step 1:  **Setup an experiment in Azure ML and deploy it as a Web Service**
This example code use the following R Script experiment for the machine learning job.  that uses the 'Movie Rating' as the input dataset.

![R Script experiment](assets/Azure_ML_Experiment.png?raw=true)
By connecting the sample 'Movie Rating' to the dataset1 port of the R script module indicates the input file follows the same schema as the 'Moving Rating' dataset.  Web Services modules are connected to the input and output port of the R script module such that this experiment can published as a Web Service.  As mentioned before, the input data must be provided as a file from a cloud storage, hence the Web Service input expects the storage connection string.  The R script simply outputs the movie list in descending rating orders and forward it to the Web Service output module.  Once this experiment is setup and ran, you can use the 'Visualise' action (by right clicking the output port of the R script module) to view the results by using 'Movie Rating' as the input dataset.

![R Script ](assets/R_Script.png?raw=true)

Click the **Deploy as Web Service** action to deploy

Step 2: Create an Azure Blob Storage account for storing data files.  Alternatively, create a new container in an existing blob storage account.

Step 3: Once the experiment is published as a web service, update the following properties in *resources/azureML.properties*:
 - API URL
 - API key
 - Blob Storage Account
 - Blob Storage Account Key
 - Blob Storage Container Name

**Check the REST request JSON string matches the format provided in the properties file**
Go to BES Web Service page for your published experiment.  Look for the REST request for the **jobs** action (should be the first in the list) and check the expected JSON string matches the format given in *resources/azureML.properties* file  (property azure.ml.bes.submitJobRequest.json).

Step 4:  Execute MyApp.java to execute the job:
```
Upload input dataset ....
Submit job to Azure ML ....
Send start job request to Azure ML ....
Checking job status....
Mon Mar 06 14:54:58 AEST 2017:GET https://ussouthcentral.services.azureml.net/workspaces/b21dd66a750d46bd8fad86be77685657/services/XXXXXXX/jobs/XXXXXXX?api-version=2.0 HTTP/1.1
Job status: NotStarted
Checking job status....
Mon Mar 06 14:54:59 AEST 2017:GET https://ussouthcentral.services.azureml.net/workspaces/b21dd66a750d46bd8fad86be77685657/services/XXXXXXX/jobs/XXXXXXX?api-version=2.0 HTTP/1.1
Job status: Running
Checking job status....
Mon Mar 06 14:55:00 AEST 2017:GET https://ussouthcentral.services.azureml.net/workspaces/b21dd66a750d46bd8fad86be77685657/services/XXXXXXX/jobs/XXXXXXX?api-version=2.0 HTTP/1.1
Job status: Running
...
Checking job status....
Mon Mar 06 14:55:16 AEST 2017:GET https://ussouthcentral.services.azureml.net/workspaces/b21dd66a750d46bd8fad86be77685657/services/XXXXXXX/jobs/XXXXXXX?api-version=2.0 HTTP/1.1
Job status: Finished
Mon Mar 06 14:55:17 AEST 2017: Job completed
Download input dataset....
Output file: /home/username/dev/azure_ml_demo/azureMLOutput.csv
```
