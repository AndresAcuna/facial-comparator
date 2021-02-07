# Face Comparison using Lambdas and AWS Rekognize

This repository contains source code that  will compare all files on a specified S3 Bucket using AWS Rekognize, the files must be images of a single individual.


## Instructions:

Clone the repository at https://github.com/AndresAcuna/facial-comparator

The directory will contain three directories

    -docs - contains miscellaneous documents
    -awslambda - contains the source code for the 
        AWS Lambda, written in Java. The lambda
        runs using the Java 11 Runtime. A resulting
        jar was packaged using Maven and the shade
        plugin.
    -reactapp - contains the source code for the React 
        application. 

The repository does not contain any of the dependencies, both
for the Lambda and the React app.

## Instructions for running the React application:

The package.json is located in the reactapp/ directory, navigate
to the reactapp/ directory and run:

    npm install 

in order to install all the dependencies of the aws application.

Once the dependencies are installed, run

    npm start

This will serve the files on port 3000.

## Application Instructions

The application will show one button and an empty table below.

Click the button to trigger the Lambda through an API Gateway.

Wait for the Lambda to return the results.
Once the results are complete, the table will be updated
    with the pictures of the faces as well as a similarity
    Score on the right hand side, if the pictures are not loaded
    then the application will display the filenames. 
Hovering over the pictures will display the filename as well.




