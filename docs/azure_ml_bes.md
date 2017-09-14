Execution of Azure Machine Learning job via Batch Execution Service
====================================================
This is an example Java application for executing a job inside Azure Machine Learning (AML) via Batch Execution Service (BES).  As opposed to the Request-Response Service (RRS) for quick jobs restricted to less than 90 seconds, BES is used for running longer jobs asynchronously.  

This is how it works using BES - 
1) Client application upload input dataset to cloud storage.
2) Client application submit and start the AML job through REST API 
3) Periodically, the client application will check to determine if the job is still running through the REST API.  The API is also used for cancelling jobs.
3)  Once the job is completed, the client application can download output data from cloud storage

![AML BES ](assets/AzureMLBES.png?raw=true)

In Azure Machine Learning Studio, you can combine different modules (e.g. for data cleansing, filtering) to create a data analytical experiment that suits your needs.  Once completed and tested, the experiment can be published as a Web Service through the AML Studio.  A API key and API URL will be provided to permit access from any client application.  This is the only type of authentication supports for AML jobs at the moment.

Example Details
---------------
Machine Learning module : R Script Module
Storage for input and output data : Azure Blob

Requirements
------------------
- Maven 3.0 or higher to build
- Java 7 or higher
- Azure Subscription

Recommended Readings
-----------------
Azure Machine Learning
https://azure.microsoft.com/en-us/services/machine-learning/

How to consume an Azure Machine Learning Web service that has been deployed from a Machine Learning experiment
https://docs.microsoft.com/en-us/azure/machine-learning/machine-learning-consume-web-services


Steps
--------
Step 1:  **Setup an experiment in Azure ML and deploy it as a Web Service**
This example code uses the following R Script experiment to demonstate how a job in AML is executed.

![R Script experiment](assets/Azure_ML_Experiment.png?raw=true)
By connecting the sample 'Movie Rating' to the dataset1 port of the R script module indicates the input file follows the same schema as the 'Moving Rating' dataset.  Web Services modules are connected to the input and output port of the R script module such that this experiment can published as a Web Service.  As mentioned before, the input data must be provided as a file from a cloud storage, hence the Web Service input expects the storage connection string.  The R script simply outputs the movie list in descending rating orders and forward it to the Web Service output module.  Once this experiment is setup and ran, you can use the 'Visualise' action (by right clicking the output port of the R script module) to view the results by using 'Movie Rating' as the input dataset.

![R Script ](assets/R_Script.png?raw=true)

Click the **Deploy as Web Service** action to deploy

Step 2: Create an Azure Blob Storage account for storing the output files.  Alternatively, create a new container to keep these separate from your other blob data under the same account.

Step 3: Once the experiment is published as a web service, update the following properties in *resources/azureML.properties* with your experiment and storage account details:
 - API URL
 - API key
 - Blob Storage Account
 - Blob Storage Account Key
 - Blob Storage Container Name

**Check the REST request JSON string matches the format provided in the properties file**
Go to BES Web Service page for your published experiment and look for **jobs** action section.  Check the expected JSON string matches the format given in *resources/azureML.properties* file (property azure.ml.bes.submitJobRequest.json).

Step 4:  Execute MyApp.java to run AML job:
```
Upload input dataset ....
Submit job to Azure ML ....
Send start job request to Azure ML ....
Checking job status....
Mon Mar 06 14:54:58 AEST 2017:GET https://ussouthcentral.services.azureml.net/workspaces/XXXXXXXXX/services/XXXXXXX/jobs/XXXXXXX?api-version=2.0 HTTP/1.1
Job status: NotStarted
Checking job status....
Mon Mar 06 14:54:59 AEST 2017:GET https://ussouthcentral.services.azureml.net/workspaces/XXXXXXXXX/services/XXXXXXX/jobs/XXXXXXX?api-version=2.0 HTTP/1.1
Job status: Running
Checking job status....
Mon Mar 06 14:55:00 AEST 2017:GET https://ussouthcentral.services.azureml.net/workspaces/XXXXXXXXX/services/XXXXXXX/jobs/XXXXXXX?api-version=2.0 HTTP/1.1
Job status: Running
...
Checking job status....
Mon Mar 06 14:55:16 AEST 2017:GET https://ussouthcentral.services.azureml.net/workspaces/XXXXXXXXX/services/XXXXXXX/jobs/XXXXXXX?api-version=2.0 HTTP/1.1
Job status: Finished
Mon Mar 06 14:55:17 AEST 2017: Job completed
Download input dataset....
Output file: /home/username/dev/azure_ml_demo/azureMLOutput.csv
```
