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
OpenAPI documentation JSON - `http://localhost:8080/api/v1/accounts/api-docs`
OpenAPI Swagger - `http://localhost:8080/api/v1/accounts/swagger-ui/index.html`
#### POST `/api/v1/accounts/register` - registration
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

#### POST `/api/v1/accounts/login` - login
Body
```JSON
{
  "phone": "String",
  "password": "String"
}
```

#### POST `/api/v1/accounts/refresh-token` - refresh-token

Body

```JSON
{
  "refreshToken": "String"
}
```

#### GET `/api/v1/accounts/self` - get account (Authentication Bearer required)

#### GET `/api/v1/accounts/{accountId}` - get account (Authentication Bearer and ROLE_ADMIN required)

#### GET `/api/v1/accounts` - get accounts (Authentication Bearer and ROLE_ADMIN required)

#### POST `/api/v1/accounts` - create account (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String",
  "phone": "String",
  "email": "String",
  "dateOfBirth": "String ISO-8601",
  "password": "String",
  "roles": []
}
```

#### PUT `/api/v1/accounts/{accountId}` - update account (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String",
  "phone": "String",
  "email": "String",
  "dateOfBirth": "String ISO-8601",
  "password": "String",
  "roles": []
}
```

#### DELETE `/api/v1/accounts/{accountId}` - get account (Authentication Bearer and ROLE_ADMIN required)

### product-service

OpenAPI documentation JSON - `http://localhost:8080/api/v1/products/api-docs`
OpenAPI Swagger - `http://localhost:8080/api/v1/products/swagger-ui/index.html`

#### _Products_

##### GET `/api/v1/products` - get all products

##### GET `/api/v1/products/{productId}` - get product

##### POST `/api/v1/products` - create product (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String",
  "price": 0.0,
  "base64Image": "String",
  "ingredients": [],
  "categories": []
}
```

##### PUT `/api/v1/products/{productId}` - update product (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String",
  "price": 0.0,
  "base64Image": "String",
  "ingredients": [],
  "categories": []
}
```

##### DELETE `/api/v1/products/{productId}` - delete product (Authentication Bearer and ROLE_ADMIN required)

#### _Ingredient_

##### GET `/api/v1/products/ingredients` - get all ingredients

##### GET `/api/v1/products/ingredients/{ingredientId}` - get ingredient

##### POST `/api/v1/products/ingredients` - create ingredient (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String"
}
```

##### PUT `/api/v1/products/ingredients/{ingredientId}` - update ingredient (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String"
}
```

##### DELETE `/api/v1/products/ingredients/{ingredientId}` - delete ingredient (Authentication Bearer and ROLE_ADMIN required)

#### _Category_

##### GET `/api/v1/products/categories` - get all categories

##### GET `/api/v1/products/categories/{categoryId}` - get category

##### POST `/api/v1/products/categories` - create category (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String"
}
```

##### PUT `/api/v1/products/categories/{categoryId}` - update category (Authentication Bearer and ROLE_ADMIN required)

Body

```JSON
{
  "name": "String"
}
```

##### DELETE `/api/v1/products/categories/{categoryId}` - delete category (Authentication Bearer and ROLE_ADMIN required)
