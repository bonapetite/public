import java.util.Date;

public class MyApp {

    public static void main(String[] args) throws Exception {
        final String inputFilename = "azureMLInput.csv";
        final String outputFilename = "azureMLOutput.csv";
        AzureMLBESClient client = new AzureMLBESClient("azureML.properties", inputFilename, outputFilename);
        client.setVerbose(true);
        client.uploadInputDataset(inputFilename);
        String jobId = client.submitJob();
        client.startJob(jobId);
        final int ESTIMATED_JOB_DURATION_IN_MILLIS = 1000;
        Thread.sleep(ESTIMATED_JOB_DURATION_IN_MILLIS);
        while (!client.isJobFinished(jobId)) {
            Thread.sleep(1000);
        }
        System.out.println(new Date() + ": Job completed");
        client.downloadDataset(outputFilename, outputFilename);
    }
}
