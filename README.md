# MicroSushiApp

## Run App

### Create ssh for auth-service

docker required

```BASH
docker compose -f .\ssh-keygen\docker-compose.yml up
```

### Build project

maven required

```BASH
mvn install -f pom.xml
```

### Run project

```BASH
docker compose up
```

## Endpoints

### account-service

#### POST `/api/v1/account/register` - registration

Body

```JSON
{
  "name": "String",
  "phone": "String",
  "email": "String",
  "dateOfBirth": "String ISO-8601",
  "password": "String",
  "confirmPassword": "String"
}
```

#### POST `/api/v1/account/login` - login

Body

```JSON
{
  "phone": "String",
  "password": "String"
}
```

#### POST `/api/v1/account/refresh-token` - refresh-token

Body

```JSON
{
  "refreshToken": "String"
}
```

#### GET `/api/v1/account` - get account (Authentication Bearer required)

### product-service

#### GET `/api/v1/product` - get all products

#### POST `/api/v1/product` - create product (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String",
  "price": 0.0,
  "base64Image": "String"
}
```