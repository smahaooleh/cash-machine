How to run test project:
1. download .zip or clone git repository
2. open in IDE and run 


Authentication API 
3. POST /api/v1/auth/register  - request to register user with:
* card number (String, 16 digits);
* PIN code (String, 4 digits);
Example:
{ "cardNumber": "1234123412341234" , "pinCode": "1234" }
4. POST /api/v1/auth/login - request to authenticate and get JWT with:
* card number (String, 16 digits);
* PIN code (String, 4 digits);
Example:
{    "cardNumber": "1234123412341234" ,"pinCode": "1234" }


Operation API (HEADER “Authorization” with “Bearer JWT”)
1. GET /api/v1/users/{id}/balance - request to get balance for user with id (Long).


2. GET /api/v1/users/{id}/transactions - request to get all transactions for user with id (Long).


3. GET /api/v1/users/{id}/transactions/filter?type={type} - request to get all transactions for user with id (Long) with type: {DEPOSIT, WITHDRAW, DEBITED, CREDITED}


4. POST /api/v1/users/{id}/deposit?amount={amount} - request to deposit balance for user with id (Long) on amount (Double)
* amount > 0


5. POST api/v1/users/{id}/deposit?amount={amount}&receiverCardNumber={cardNumber} - request to deposit balance for user with cardNumber (String, 16 digits) on amount (Double)
* amount > 0
* card number can not be the same then authenticated user card number


6. POST api/v1/users/{id}/withdraw?amount={amount} - request to withdrawal with amount (Double) for user with id (Long)
* amount > 0


7. POST api/v1/users/{id}/transfer?amount={amount}&receiverCardNumber={cardNumber} - request to transfer amount (Double) from balance of user with id (Long) to balance of user with cardNumber (String, 16 digits)
* amount > 0
* card number can not be the same then authenticated user card number