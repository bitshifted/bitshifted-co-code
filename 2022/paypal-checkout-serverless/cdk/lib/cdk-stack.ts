import * as cdk from '@aws-cdk/core';
import * as dynamodb from '@aws-cdk/aws-dynamodb'
import * as lambda from '@aws-cdk/aws-lambda'
import * as api from '@aws-cdk/aws-apigateway'
import * as path from 'path'
import { CfnParameter, RemovalPolicy, Duration } from '@aws-cdk/core';
// import * as sqs from '@aws-cdk/aws-sqs';

export class CdkStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

      // create transactions table
      const txTable = new dynamodb.Table(this, 'TxTable', {
        partitionKey: {name: 'paypalTxId', type: dynamodb.AttributeType.STRING},
        billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
        removalPolicy: RemovalPolicy.DESTROY
      })

      const appId = new CfnParameter(this, 'appId', {
        type: 'String'
      })

      const appSecret = new CfnParameter(this, 'appSecret', {
        type: 'String'
      })

      const lambdaFn =  new lambda.Function(this, 'checkout-function', {
        runtime: lambda.Runtime.JAVA_11,
        handler: 'co.bitshifted.sample.CheckoutHandler',
        code: lambda.Code.fromAsset(path.join('../target', 'paypal-checkout-serverless-1.0-SNAPSHOT.jar')),
        environment: {
          "PAYPAL_BASE_URL": "https://api-m.sandbox.paypal.com",
          "PAYPAL_APP_ID": appId.valueAsString,
          "PAYPAL_SECRET": appSecret.valueAsString
        },
        memorySize: 256,
        timeout: Duration.minutes(1)
      })

      txTable.grantReadWriteData(lambdaFn)

      const checkoutApi = new api.RestApi(this, 'checkout-api', {
        deployOptions: {
          stageName : 'dev'
        }
      })

      const checkout = checkoutApi.root.addResource('checkout')
      checkout.addMethod('POST', new api.LambdaIntegration(lambdaFn, {
        proxy: true
      }))

      const checkoutSuccess = checkout.addResource('success')
      checkoutSuccess.addMethod('GET', new api.LambdaIntegration(lambdaFn, {
        proxy: true
      }))

   
  }
}
