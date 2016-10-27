# Stripe OAuth Java 8 example

This is a simple example project illustrating how to implement [Stripe's OAuth flow](https://stripe.com/docs/connect/standalone-accounts) to connect standalone accounts to a platform.

## Use with Heroku

Deploy the project on your Heroku account:

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

When deploying, you'll be prompted for your secret API key (you can find it in the [API keys](https://dashboard.stripe.com/account/apikeys) tab of your account settings) and your platform's client_id (you can find in the [Connect](https://dashboard.stripe.com/account/applications/settings) tab of your account settings).

Once the app is running on Heroku, head back to your [Connect](https://dashboard.stripe.com/account/applications/settings) settings and set the redirect URI to your app's URL followed by `/oauth/callback`. E.g. if the URL of the Heroku app is `https://thawing-crag-63114.herokuapp.com`, set the redirect URI to be `https://thawing-crag-63114.herokuapp.com/oauth/callback`.

## Use as a standalone project

### Requirements

- Java 1.8 or later
- Maven

### Instructions

Clone the repository:

```bash
git clone https://github.com/ob-stripe/example-stripe-java8-oauth.git
```

Compile the project:

```bash
mvn compile
```

Get your secret API key (you can find it in the [API keys](https://dashboard.stripe.com/account/apikeys) tab of your account settings) and your platform's client_id (you can find in the [Connect](https://dashboard.stripe.com/account/applications/settings) tab of your account settings) and run the project:

```bash
STRIPE_TEST_SECRET_KEY=sk_test_... \
STRIPE_DEVELOPMENT_CLIENT_ID=ca_... \
mvn exec:java
```

Head back to your [Connect](https://dashboard.stripe.com/account/applications/settings) settings and set the redirect URI to `http://localhost:4567/oauth/callback`.

In your browser, go to http://localhost:4567.
