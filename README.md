# test-app
Parallel Calculator
based on Akka Streams & Akka Http 

example use case:
curl -H "Content-Type: application/json" -X POST -d '{"expression": "(1-1)*2+3*(1-3+4)+10/3"}' http://localhost:5555/evaluate
