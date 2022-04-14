import { Stack, StackProps, RemovalPolicy, Duration } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as s3 from 'aws-cdk-lib/aws-s3'
import { BlockPublicAccess } from 'aws-cdk-lib/aws-s3';
import * as cf from 'aws-cdk-lib/aws-cloudfront'
import * as cfo from 'aws-cdk-lib/aws-cloudfront-origins'
import { PolicyStatement, CanonicalUserPrincipal, Effect } from 'aws-cdk-lib/aws-iam';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager'
import * as lambda from 'aws-cdk-lib/aws-lambda'
import {readFileSync} from 'fs'
import * as path from 'path'
import { ViewerProtocolPolicy } from 'aws-cdk-lib/aws-cloudfront';

export class CdkStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const bucket = new s3.Bucket(this, 'bucket-for-testing', {
      bucketName: "cf-s3-presigned-test-bucket",
      removalPolicy: RemovalPolicy.DESTROY,
      autoDeleteObjects: true
    })

    const cdIdentity = new cf.OriginAccessIdentity(this, 'cd-aoi', {
      comment: "OAI for CF distro",
    })

    bucket.addToResourcePolicy(new PolicyStatement({
      actions: ['s3:GetObject'],
      resources: [bucket.arnForObjects('*')],
      principals: [new CanonicalUserPrincipal( cdIdentity.cloudFrontOriginAccessIdentityS3CanonicalUserId)]
    }))

    // create key group
    // read public key content
    const pubKeyFile = readFileSync('../keys/public_key.pem')
    const publicKey = new cf.PublicKey(this, 'cf-presigned-pubkey', {
      encodedKey: pubKeyFile.toString()
    })

    const cfKeyGroup = new cf.KeyGroup(this, 'cf-keygroup', {
      items: [publicKey]
    })
    // create private key
    const privateKeyFile = readFileSync('../keys/private_key_pkcs8.pem')
    const privKeyValue = secretsmanager.SecretStringValueBeta1.fromUnsafePlaintext(privateKeyFile.toString())
    const privateKeySecretName = 'cf-secret-name'
    const privateKeySecret = new secretsmanager.Secret(this, 'cf-priv-key', {
      secretStringBeta1: privKeyValue,
      secretName: privateKeySecretName,
      removalPolicy: RemovalPolicy.DESTROY,
      
    })

    const cfDistro = new cf.Distribution(this, 'pre-sign-dist', {
      defaultBehavior: {
        origin:  new cfo.S3Origin(bucket,{
          originAccessIdentity: cdIdentity
        }),
        trustedKeyGroups: [cfKeyGroup],
        viewerProtocolPolicy: ViewerProtocolPolicy.HTTPS_ONLY
      }
    })

    const lambdaFunction = new lambda.Function(this, 'presign-url-lambda', {
      runtime: lambda.Runtime.JAVA_11,
      handler: 'co.bitshifted.samples.PreSignedUrlHandler',
      code: lambda.Code.fromAsset(path.join('../target', 'cf-s3-lambda-presigned-url-1.0.0-SNAPSHOT.jar')),
      memorySize: 256,
      timeout: Duration.minutes(1),
      environment: { 
        "CF_DISTRO_BASE_URL": cfDistro.distributionDomainName,
        "PRIVATE_KEY_SECRET_NAME": privateKeySecretName,
        "CF_KEYPAIR_ID": publicKey.publicKeyId
      }
    })
    lambdaFunction.addToRolePolicy(new PolicyStatement({
        actions: ["secretsmanager:GetSecretValue"],
        resources: [privateKeySecret.secretArn],
        effect: Effect.ALLOW
    }))
    
  }
}
