# Add a new order for Bob with ID 1
curl -X PUT http://localhost:9090/orders-server/orders/1?customer_name=bob 

# Check the status of the order
curl -X GET http://localhost:9090/orders-server/orders/1 

# See all the orders in the system
curl -X GET http://localhost:9090/orders-server/orders/list